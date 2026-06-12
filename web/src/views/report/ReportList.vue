<template>
  <div class="report-list">
    <div class="toolbar">
      <el-button type="primary" @click="generateDialogVisible = true">生成报表</el-button>
    </div>

    <el-table :data="reports" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="reportType" label="报表类型" width="160" :formatter="formatReportType" />
      <el-table-column prop="period" label="期间" width="140" />
      <el-table-column prop="submitStatus" label="状态" width="120" :formatter="formatStatus" />
      <el-table-column prop="version" label="版本" width="80" align="center" />
      <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="router.push(`/reports/${row.id}`)">查看</el-button>
          <el-button
            v-if="row.submitStatus === 'DRAFT'"
            link
            type="primary"
            size="small"
            @click="handleSubmit(row.id)"
          >提交</el-button>
          <el-button
            v-if="row.submitStatus === 'SUBMITTED'"
            link
            type="primary"
            size="small"
            @click="handleReview(row.id, true)"
          >审核通过</el-button>
          <el-button
            v-if="row.submitStatus === 'SUBMITTED'"
            link
            type="danger"
            size="small"
            @click="handleReview(row.id, false)"
          >驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Generate report dialog -->
    <el-dialog v-model="generateDialogVisible" title="生成报表" width="450px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="报表类型" required>
          <el-select v-model="generateForm.reportType" placeholder="请选择报表类型" style="width: 100%">
            <el-option label="资产月报" value="MONTHLY_ASSET" />
            <el-option label="资产季报" value="QUARTERLY_ASSET" />
            <el-option label="资产年报" value="ANNUAL_ASSET" />
            <el-option label="折旧分析" value="DEPRECIATION_ANALYSIS" />
            <el-option label="资产变动" value="ASSET_CHANGE" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属期间" required>
          <el-date-picker
            v-model="generateForm.period"
            type="month"
            placeholder="选择月份"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="generating" @click="handleGenerate">确定生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listReports, generateReport as apiGenerateReport, submitReport, reviewReport } from '@/api/report'
import type { Report } from '@/api/report'

const router = useRouter()

const reports = ref<Report[]>([])
const loading = ref(false)
const generateDialogVisible = ref(false)
const generating = ref(false)

const generateForm = reactive({
  reportType: '',
  period: ''
})

const reportTypeMap: Record<string, string> = {
  MONTHLY_ASSET: '资产月报',
  QUARTERLY_ASSET: '资产季报',
  ANNUAL_ASSET: '资产年报',
  DEPRECIATION_ANALYSIS: '折旧分析',
  ASSET_CHANGE: '资产变动'
}

const statusMap: Record<string, string> = {
  DRAFT: '草稿',
  SUBMITTED: '已提交',
  REVIEWED: '已审核',
  ACCEPTED: '已接收',
  REJECTED: '已驳回'
}

function formatReportType(_row: unknown, _column: unknown, value: string): string {
  return reportTypeMap[value] || value
}

function formatStatus(_row: unknown, _column: unknown, value: string): string {
  return statusMap[value] || value
}

async function fetchReports() {
  const orgIdStr = localStorage.getItem('orgId')
  if (orgIdStr === null) return
  const orgId = Number(orgIdStr)

  loading.value = true
  try {
    const response = await listReports(orgId)
    reports.value = response.data || []
  } catch {
    reports.value = []
  } finally {
    loading.value = false
  }
}

async function handleGenerate() {
  if (!generateForm.reportType || !generateForm.period) {
    ElMessage.warning('请完整填写报表信息')
    return
  }

  const orgId = Number(localStorage.getItem('orgId') || '0')
  const tenantId = Number(localStorage.getItem('tenantId') || '0')

  generating.value = true
  try {
    await apiGenerateReport(generateForm.reportType, orgId, generateForm.period, tenantId)
    ElMessage.success('报表生成成功')
    generateDialogVisible.value = false
    generateForm.reportType = ''
    generateForm.period = ''
    fetchReports()
  } catch {
    // Error handled by interceptor
  } finally {
    generating.value = false
  }
}

async function handleSubmit(id: number) {
  try {
    const operatorId = Number(localStorage.getItem('userId') || 0)
    await submitReport(String(id), operatorId)
    ElMessage.success('提交成功')
    fetchReports()
  } catch {
    // Error handled by interceptor
  }
}

async function handleReview(id: number, approved: boolean) {
  const action = approved ? '审核通过' : '驳回'
  try {
    await ElMessageBox.confirm(`确定${action}该报表吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const reviewerId = Number(localStorage.getItem('userId') || 0)
    await reviewReport(String(id), approved, reviewerId)
    ElMessage.success(`${action}成功`)
    fetchReports()
  } catch {
    // Cancelled or error handled by interceptor
  }
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.report-list {
  padding: 0;
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}
</style>
