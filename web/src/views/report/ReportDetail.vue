<template>
  <div class="report-detail">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
      <div class="header-actions" v-if="report">
        <el-button
          v-if="report.submitStatus === 'DRAFT'"
          type="primary"
          @click="handleSubmit"
        >提交</el-button>
        <el-button
          v-if="report.submitStatus === 'SUBMITTED'"
          type="success"
          @click="handleReview(true)"
        >审核通过</el-button>
        <el-button
          v-if="report.submitStatus === 'SUBMITTED'"
          type="danger"
          @click="handleReview(false)"
        >驳回</el-button>
      </div>
    </div>

    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span class="card-title">报表详情</span>
      </template>

      <template v-if="report">
        <!-- Metadata -->
        <el-descriptions :column="3" border class="meta-descriptions">
          <el-descriptions-item label="报表类型">{{ reportTypeMap[report.reportType] || report.reportType }}</el-descriptions-item>
          <el-descriptions-item label="所属期间">{{ report.period }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">{{ statusMap[report.submitStatus] || report.submitStatus }}</el-descriptions-item>
          <el-descriptions-item label="版本号">{{ report.version }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ report.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ report.updatedAt }}</el-descriptions-item>
        </el-descriptions>

        <!-- Summary stats -->
        <h3 class="section-title">汇总统计</h3>
        <el-descriptions v-if="summary" :column="3" border>
          <el-descriptions-item label="资产总数">{{ summary.totalAssets ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="资产原值">{{ formatCurrency(summary.totalOriginalValue) }}</el-descriptions-item>
          <el-descriptions-item label="资产净值">{{ formatCurrency(summary.totalCurrentValue) }}</el-descriptions-item>
          <el-descriptions-item label="累计折旧">{{ formatCurrency(summary.totalDepreciation) }}</el-descriptions-item>
          <el-descriptions-item label="平均折旧率">{{ formatPercent(summary.avgDepreciationRate) }}</el-descriptions-item>
          <el-descriptions-item label="涉及组织数">{{ summary.orgCount ?? '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- Category distribution table -->
        <h3 class="section-title">资产分类分布</h3>
        <el-table
          v-if="categoryDistribution.length > 0"
          :data="categoryDistribution"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column prop="name" label="类别" min-width="180" :formatter="formatCategoryName" />
          <el-table-column prop="value" label="数量" width="120" align="right" />
          <el-table-column prop="totalOriginalValue" label="原值" min-width="160" align="right" :formatter="formatCurrencyCol" />
          <el-table-column prop="totalCurrentValue" label="净值" min-width="160" align="right" :formatter="formatCurrencyCol" />
        </el-table>
        <el-empty v-else-if="!loading" description="暂无分类数据" />
      </template>

      <el-empty v-else-if="!loading" description="未找到报表信息" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReport, submitReport, reviewReport } from '@/api/report'
import type { Report } from '@/api/report'

interface SummaryStats {
  totalAssets?: number
  totalOriginalValue?: number
  totalCurrentValue?: number
  totalDepreciation?: number
  avgDepreciationRate?: number
  orgCount?: number
}

interface CategoryItem {
  name: string
  value: number
  totalOriginalValue?: number
  totalCurrentValue?: number
}

const route = useRoute()
const router = useRouter()

const report = ref<Report | null>(null)
const loading = ref(false)

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

const categoryMap: Record<string, string> = {
  LAND: '土地',
  REAL_ESTATE: '房产',
  EQUIPMENT: '设备',
  VEHICLE: '车辆',
  FURNITURE: '家具',
  IT_EQUIPMENT: 'IT设备'
}

const summary = computed<SummaryStats>(() => {
  return (report.value?.summaryData as SummaryStats) || {}
})

const categoryDistribution = computed<CategoryItem[]>(() => {
  const data = report.value?.summaryData as Record<string, unknown> | undefined
  return (data?.categoryDistribution as CategoryItem[]) || []
})

function formatCurrency(value: number | undefined | null): string {
  if (value == null) return '-'
  return value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY' })
}

function formatCurrencyCol(_row: unknown, _column: unknown, value: number): string {
  return formatCurrency(value)
}

function formatPercent(value: number | undefined | null): string {
  if (value == null) return '-'
  return (value * 100).toFixed(2) + '%'
}

function formatCategoryName(_row: unknown, _column: unknown, value: string): string {
  return categoryMap[value] || value
}

async function fetchReport() {
  const id = route.params.id as string
  if (!id) return

  loading.value = true
  try {
    const response = await getReport(id)
    report.value = response.data ?? null
  } catch {
    report.value = null
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  try {
    const operatorId = Number(localStorage.getItem('userId') || 0)
    await submitReport(report.value!.id, operatorId)
    ElMessage.success('提交成功')
    fetchReport()
  } catch {
    // Error handled by interceptor
  }
}

async function handleReview(approved: boolean) {
  const action = approved ? '审核通过' : '驳回'
  try {
    await ElMessageBox.confirm(`确定${action}该报表吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const reviewerId = Number(localStorage.getItem('userId') || 0)
    await reviewReport(report.value!.id, approved, reviewerId)
    ElMessage.success(`${action}成功`)
    fetchReport()
  } catch {
    // Cancelled or error handled by interceptor
  }
}

onMounted(() => {
  fetchReport()
})
</script>

<style scoped>
.report-detail {
  padding: 0;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 20px 0 12px;
}

.meta-descriptions {
  margin-bottom: 0;
}
</style>
