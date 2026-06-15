<template>
  <div class="export-center">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">报表导出中心</span>
      </template>

      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 16px">
        <template #title>
          推荐流程：下载模板 → 线下填报 → 导入校验 → 导出正式报表
        </template>
      </el-alert>

      <el-tabs v-model="activeTab">
        <!-- ===== Tab 1: 报表导出 ===== -->
        <el-tab-pane label="报表导出" name="export">
          <div class="filter-bar">
            <el-select v-model="exportForm.reportType" placeholder="请选择报表" style="width: 260px">
              <el-option-group v-for="group in reportTypeGroups" :key="group.label" :label="group.label">
                <el-option v-for="opt in group.options" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-option-group>
            </el-select>
            <el-input v-model="exportForm.orgId" placeholder="组织ID（可选）" style="width: 160px" clearable />
            <el-date-picker v-model="exportForm.period" type="month" placeholder="选择月份" value-format="YYYY-MM" style="width: 160px" />
            <el-button type="primary" @click="handleExport" :loading="exporting">导出 Excel</el-button>
          </div>

          <el-table :data="tasks" v-loading="loading" stripe>
            <el-table-column label="报表名称" min-width="180">
              <template #default="{ row }">{{ reportTypeLabel(row.exportType) }}</template>
            </el-table-column>
            <el-table-column label="周期" prop="reportPeriod" width="100" />
            <el-table-column label="文件名" prop="fileName" min-width="200">
              <template #default="{ row }">{{ row.fileName || '-' }}</template>
            </el-table-column>
            <el-table-column label="数据行数" prop="totalRows" width="100">
              <template #default="{ row }">{{ row.totalRows ?? '-' }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="170">
              <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.status === 'COMPLETED'" type="primary" link size="small" @click="handleDownload(row)">下载</el-button>
                <el-button v-else-if="row.status === 'FAILED'" type="warning" link size="small" @click="handleRetry(row)">重试</el-button>
                <el-text v-else type="info" size="small">处理中...</el-text>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination v-if="total > pageSize" :current-page="currentPage" :page-size="pageSize"
            :total="total" layout="prev, pager, next" @current-change="handlePageChange"
            style="margin-top: 16px; justify-content: center" />
        </el-tab-pane>

        <!-- ===== Tab 2: 报表导入 ===== -->
        <el-tab-pane label="报表导入" name="import">
          <el-select v-model="importExportType" placeholder="选择导入报表类型" style="width: 300px; margin-bottom: 16px">
            <el-option-group v-for="group in importableGroups" :key="group.label" :label="group.label">
              <el-option v-for="opt in group.options" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-option-group>
          </el-select>

          <el-upload ref="uploadRef" drag :auto-upload="false" :on-change="handleFileChange" :limit="1" accept=".xlsx">
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">将填好的 Excel 文件拖到此处，或 <em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">仅支持 .xlsx 格式，请使用标准模板（模板下载 Tab 获取）</div>
            </template>
          </el-upload>

          <div style="margin-top: 16px; display: flex; gap: 12px">
            <el-button type="primary" @click="handlePreviewImport" :loading="previewing" :disabled="!importFile">校验预览</el-button>
            <el-button type="success" @click="handleConfirmImport" :loading="importing"
              :disabled="!importPreview.previewData?.length || importPreview.errorRows > 0">确认导入</el-button>
          </div>

          <el-alert v-if="importPreview.errorRows > 0" type="error" style="margin-top: 16px" show-icon>
            <template #title>校验发现 {{ importPreview.errorRows }} 个错误，共 {{ importPreview.totalRows }} 行数据</template>
          </el-alert>
          <el-alert v-else-if="importPreview.totalRows > 0" type="success" style="margin-top: 16px" show-icon>
            <template #title>校验通过：{{ importPreview.totalRows }} 行数据，无错误</template>
          </el-alert>

          <el-table v-if="importPreview.errors?.length" :data="importPreview.errors" border size="small" style="margin-top: 8px">
            <el-table-column prop="row" label="行号" width="80" />
            <el-table-column prop="field" label="字段" width="120" />
            <el-table-column prop="reason" label="原因" />
          </el-table>

          <el-table v-if="importPreview.previewData?.length" :data="importPreview.previewData" border stripe
            max-height="400" style="margin-top: 16px">
            <el-table-column v-for="(_, key) in importPreview.previewData[0]" :key="key" :prop="key"
              :label="String(key)" min-width="120" />
          </el-table>
        </el-tab-pane>

        <!-- ===== Tab 3: 模板下载 ===== -->
        <el-tab-pane label="模板下载" name="template">
          <div style="display: flex; gap: 12px; align-items: center">
            <el-select v-model="templateType" placeholder="请选择报表类型" style="width: 300px">
              <el-option-group v-for="group in reportTypeGroups" :key="group.label" :label="group.label">
                <el-option v-for="opt in group.options" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-option-group>
            </el-select>
            <el-button type="primary" @click="handleDownloadTemplate" :loading="downloadingTemplate" :disabled="!templateType">下载模板</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import {
  createExportTask,
  listExportTasks,
  downloadExportFile,
  downloadTemplate,
  previewImport,
  importReport,
  reportTypeGroups,
} from '@/api/export'
import type { ExportTask, ImportResult } from '@/api/export'
import { ElMessage } from 'element-plus'

const activeTab = ref('export')

// ---- Helpers ----
function reportTypeLabel(type: string): string {
  for (const group of reportTypeGroups) {
    const found = group.options.find((o) => o.value === type)
    if (found) return found.label
  }
  return type
}

function statusLabel(status: string): string {
  const map: Record<string, string> = {
    PENDING: '排队中',
    PROCESSING: '处理中',
    COMPLETED: '已完成',
    FAILED: '失败',
  }
  return map[status] || status
}

function statusTagType(status: string): string {
  const map: Record<string, string> = {
    PENDING: 'info',
    PROCESSING: 'warning',
    COMPLETED: 'success',
    FAILED: 'danger',
  }
  return map[status] || 'info'
}

function formatTime(time: string): string {
  return time?.replace('T', ' ').substring(0, 19) || ''
}

// ---- Export Tab ----
const tasks = ref<ExportTask[]>([])
const loading = ref(false)
const exporting = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = 20
const exportForm = reactive({ reportType: '', orgId: '', period: '' })

async function fetchTasks() {
  loading.value = true
  try {
    const res = await listExportTasks(currentPage.value, pageSize)
    const data = res.data as any
    tasks.value = data.records || []
    total.value = data.total || 0
  } catch {
    tasks.value = []
  } finally {
    loading.value = false
  }
}

async function handleExport() {
  if (!exportForm.reportType) {
    ElMessage.warning('请选择报表类型')
    return
  }
  exporting.value = true
  try {
    const params = JSON.stringify({
      orgId: exportForm.orgId || null,
      period: exportForm.period || null,
    })
    await createExportTask({ exportType: exportForm.reportType, params })
    ElMessage.success('导出任务已创建')
    fetchTasks()
  } catch {
    // handled by global error handler
  } finally {
    exporting.value = false
  }
}

async function handleDownload(row: ExportTask) {
  try {
    const blob = await downloadExportFile(row.id)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = row.fileName || 'export.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } catch {
    // handled by global error handler
  }
}

async function handleRetry(row: ExportTask) {
  await createExportTask({
    exportType: row.exportType,
    params: row.params || undefined,
  })
  ElMessage.success('已重新创建导出任务')
  fetchTasks()
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchTasks()
}

// ---- Import Tab ----
const importExportType = ref('ASSET_BASE_LIST')
const importFile = ref<File | null>(null)
const importPreview = ref<ImportResult>({
  totalRows: 0,
  successRows: 0,
  errorRows: 0,
  errors: [],
  previewData: [],
})
const previewing = ref(false)
const importing = ref(false)
const uploadRef = ref()

const importableGroups = [
  {
    label: '综合报表',
    options: [
      { value: 'ASSET_BASE_LIST', label: '资产底数清单' },
      { value: 'ASSET_LIST', label: '资产台账' },
      { value: 'BALANCE_SHEET', label: '资产负债表' },
      { value: 'PROBLEM_ASSET_LIST', label: '问题资产及整治清单' },
      { value: 'REVITALIZATION_LIST', label: '盘活利用清单' },
    ],
  },
]

function handleFileChange(file: any) {
  importFile.value = file.raw
}

async function handlePreviewImport() {
  if (!importFile.value) return
  previewing.value = true
  try {
    const res = await previewImport(importFile.value, importExportType.value)
    importPreview.value = res.data!
    if (res.data!.errorRows === 0 && res.data!.totalRows > 0) {
      ElMessage.success('校验通过')
    }
  } catch {
    // handled by global error handler
  } finally {
    previewing.value = false
  }
}

async function handleConfirmImport() {
  if (!importFile.value) return
  importing.value = true
  try {
    await importReport(importFile.value, importExportType.value)
    ElMessage.success('导入成功')
    importPreview.value = {
      totalRows: 0,
      successRows: 0,
      errorRows: 0,
      errors: [],
      previewData: [],
    }
    importFile.value = null
  } catch {
    // handled by global error handler
  } finally {
    importing.value = false
  }
}

// ---- Template Tab ----
const templateType = ref('')
const downloadingTemplate = ref(false)

async function handleDownloadTemplate() {
  if (!templateType.value) return
  downloadingTemplate.value = true
  try {
    const blob = await downloadTemplate(templateType.value)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `${templateType.value}.xlsx`
    a.click()
    URL.revokeObjectURL(url)
  } catch {
    // handled by global error handler
  } finally {
    downloadingTemplate.value = false
  }
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.export-center {
  padding: 0;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
}
.filter-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
</style>
