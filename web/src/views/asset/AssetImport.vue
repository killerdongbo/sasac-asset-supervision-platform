<template>
  <div class="asset-import">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
      <el-button type="success" @click="doDownloadTemplate"><el-icon><Download /></el-icon>下载模板</el-button>
    </div>

    <el-card shadow="never">
      <template #header><span class="card-title">Excel 批量导入资产</span></template>

      <el-upload ref="uploadRef" drag :auto-upload="false" :accept="acceptTypes" :limit="1" :on-change="handleFileChange" :on-exceed="handleExceed">
        <el-icon class="upload-icon" :size="48"><UploadFilled /></el-icon>
        <div class="upload-text">拖拽Excel文件到此处，或<em>点击选择文件</em></div>
        <template #tip><div class="upload-tip">支持 .xlsx / .xls，首行为表头（不导入）</div></template>
      </el-upload>

      <div class="submit-wrapper" v-if="selectedFile">
        <el-button type="primary" @click="handleImport" :loading="uploading" size="large">开始导入</el-button>
        <span class="file-name">已选择：{{ selectedFile.name }}</span>
      </div>

      <el-card v-if="result" class="result-card" shadow="never">
        <template #header><span class="card-title">导入结果</span></template>
        <div class="result-stats">
          <div class="stat-item"><span class="stat-label">总行数</span><span class="stat-value">{{ result.totalRows }}</span></div>
          <div class="stat-item success"><span class="stat-label">成功</span><span class="stat-value">{{ result.successCount }}</span></div>
          <div class="stat-item failure"><span class="stat-label">失败</span><span class="stat-value">{{ result.errorCount }}</span></div>
        </div>
        <el-table v-if="result.errors?.length" :data="result.errors" border stripe size="small" max-height="400">
          <el-table-column prop="row" label="行号" width="80" align="center" />
          <el-table-column prop="field" label="字段" width="120" />
          <el-table-column prop="message" label="错误描述" min-width="260">
            <template #default="{row}"><span class="error-msg">{{ row.message }}</span></template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { importAssets, downloadTemplate } from '@/api/asset'
import type { ImportResult } from '@/api/asset'

const router = useRouter()
const acceptTypes = '.xlsx,.xls'
const selectedFile = ref<File | null>(null)
const uploading = ref(false)
const result = ref<ImportResult | null>(null)

function handleFileChange(file: { raw: File }) { selectedFile.value = file.raw }

function handleExceed() { ElMessage.warning('每次只能上传一个文件') }

function doDownloadTemplate() {
  downloadTemplate().then((res: any) => {
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const a = document.createElement('a'); a.href = url
    a.download = 'asset_import_template.xlsx'; a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('模板下载中...')
  })
}

async function handleImport() {
  if (!selectedFile.value) return
  uploading.value = true; result.value = null
  try {
    const resp = await importAssets(selectedFile.value)
    result.value = resp.data
    if (result.value?.errorCount === 0) ElMessage.success(`成功导入${result.value?.successCount}条`)
    else ElMessage.warning(`导入完成：成功${result.value?.successCount}条，失败${result.value?.errorCount}条`)
  } catch { ElMessage.error('导入失败') }
  finally { uploading.value = false }
}
</script>

<style scoped>
.asset-import { padding: 4px; }
.page-header { margin-bottom: 16px; display: flex; gap: 8px; }
.card-title { font-size: 16px; font-weight: 600; }
.upload-icon { margin-bottom: 8px; }
.upload-text { font-size: 14px; color: #606266; }
.upload-text em { color: #409eff; font-style: normal; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 8px; }
.submit-wrapper { margin-top: 20px; display: flex; align-items: center; gap: 12px; }
.file-name { font-size: 14px; color: #606266; }
.result-card { margin-top: 24px; }
.result-stats { display: flex; gap: 32px; margin-bottom: 16px; }
.stat-item { display: flex; flex-direction: column; align-items: center; }
.stat-label { font-size: 14px; color: #909399; margin-bottom: 4px; }
.stat-value { font-size: 28px; font-weight: 700; }
.stat-item.success .stat-value { color: #67c23a; }
.stat-item.failure .stat-value { color: #f56c6c; }
.error-msg { color: #f56c6c; font-size: 13px; }
</style>
