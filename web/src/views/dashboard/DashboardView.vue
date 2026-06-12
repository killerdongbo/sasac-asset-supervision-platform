<template>
  <div class="dashboard">
    <!-- Stat cards row -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">资产总数</div>
          <div class="stat-value">{{ overview.totalAssets ?? '-' }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">资产原值</div>
          <div class="stat-value">{{ formatLargeCurrency(overview.totalOriginalValue) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">资产净值</div>
          <div class="stat-value">{{ formatLargeCurrency(overview.totalCurrentValue) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">平均折旧率</div>
          <div class="stat-value">{{ formatPercent(overview.avgDepreciationRate) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts row -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">资产分类分布</span>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">资产状态分布</span>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getOverview } from '@/api/dashboard'
import type { OverviewData } from '@/api/dashboard'

const overview = reactive<OverviewData>({
  totalAssets: 0,
  totalOriginalValue: 0,
  totalCurrentValue: 0,
  avgDepreciationRate: 0,
  categoryDistribution: [],
  statusDistribution: []
})

const categoryChartRef = ref<HTMLDivElement | null>(null)
const statusChartRef = ref<HTMLDivElement | null>(null)

let categoryChart: echarts.ECharts | null = null
let statusChart: echarts.ECharts | null = null

const categoryColorMap: Record<string, string> = {
  LAND: '#5470c6',
  REAL_ESTATE: '#91cc75',
  EQUIPMENT: '#fac858',
  VEHICLE: '#ee6666',
  FURNITURE: '#73c0de',
  IT_EQUIPMENT: '#3ba272'
}

const statusColorMap: Record<string, string> = {
  IN_USE: '#5470c6',
  IDLE: '#91cc75',
  RENTED: '#fac858',
  MORTGAGED: '#ee6666',
  DISPOSED: '#73c0de'
}

function formatLargeCurrency(value: number | undefined | null): string {
  if (value == null) return '-'
  if (value >= 1e8) {
    return (value / 1e8).toFixed(2) + '亿'
  }
  if (value >= 1e4) {
    return (value / 1e4).toFixed(2) + '万'
  }
  return value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY' })
}

function formatPercent(value: number | undefined | null): string {
  if (value == null) return '-'
  return (value * 100).toFixed(2) + '%'
}

function buildCategoryMap(items: Array<{ name: string; value: number }>): Record<string, string> {
  const labels: Record<string, string> = {
    LAND: '土地',
    REAL_ESTATE: '房产',
    EQUIPMENT: '设备',
    VEHICLE: '车辆',
    FURNITURE: '家具',
    IT_EQUIPMENT: 'IT设备'
  }
  const map: Record<string, string> = {}
  for (const item of items) {
    map[item.name] = labels[item.name] || item.name
  }
  return map
}

function buildStatusMap(items: Array<{ name: string; value: number }>): Record<string, string> {
  const labels: Record<string, string> = {
    IN_USE: '使用中',
    IDLE: '闲置',
    RENTED: '已出租',
    MORTGAGED: '已抵押',
    DISPOSED: '已处置'
  }
  const map: Record<string, string> = {}
  for (const item of items) {
    map[item.name] = labels[item.name] || item.name
  }
  return map
}

function initCharts() {
  if (!categoryChartRef.value || !statusChartRef.value) return

  const categoryMap = buildCategoryMap(overview.categoryDistribution)
  const statusMap = buildStatusMap(overview.statusDistribution)

  // Category distribution pie chart
  categoryChart = echarts.init(categoryChartRef.value)
  categoryChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: 0,
      data: overview.categoryDistribution.map((item) => ({
        name: categoryMap[item.name] || item.name
      }))
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '65%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: (params: { name: string; percent: number }) =>
            `${params.name}\n${params.percent.toFixed(1)}%`
        },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        data: overview.categoryDistribution.map((item) => ({
          value: item.value,
          name: categoryMap[item.name] || item.name,
          itemStyle: { color: categoryColorMap[item.name] || '#999' }
        }))
      }
    ]
  })

  // Status distribution pie chart
  statusChart = echarts.init(statusChartRef.value)
  statusChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: 0,
      data: overview.statusDistribution.map((item) => ({
        name: statusMap[item.name] || item.name
      }))
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '65%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: (params: { name: string; percent: number }) =>
            `${params.name}\n${params.percent.toFixed(1)}%`
        },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        data: overview.statusDistribution.map((item) => ({
          value: item.value,
          name: statusMap[item.name] || item.name,
          itemStyle: { color: statusColorMap[item.name] || '#999' }
        }))
      }
    ]
  })
}

function handleResize() {
  categoryChart?.resize()
  statusChart?.resize()
}

async function fetchData() {
  try {
    const response = await getOverview()
    if (response.data) {
      Object.assign(overview, response.data)
    }
  } catch {
    // Error handled by interceptor
  }
}

onMounted(async () => {
  await fetchData()
  await nextTick()
  initCharts()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  categoryChart?.dispose()
  statusChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  cursor: default;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.chart-row {
  margin-bottom: 0;
}

.chart-card {
  min-height: 400px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 350px;
}
</style>
