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
          <div class="stat-label">本月新增</div>
          <div class="stat-value highlight">{{ monthNew ?? '-' }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">资产净值</div>
          <div class="stat-value">{{ formatLargeCurrency(overview.totalCurrentValue) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts row 1: category + status -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header><span class="card-title">资产分类分布</span></template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header><span class="card-title">资产状态分布</span></template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts row 2: trend -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header><span class="card-title">近12月资产新增趋势</span></template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== M11: 综合驾驶舱 ===== -->
    <h3 class="section-title">综合驾驶舱</h3>

    <!-- 指标卡片行 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="12" :sm="8" :md="6" v-for="card in enhancedCards" :key="card.key">
        <el-card shadow="hover" class="stat-card enhanced-card" :style="{ borderTop: '3px solid ' + card.color }" @click="navigateTo(card.route)">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
          <div class="stat-unit">{{ card.unit }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预警红灯区 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">预警红灯区</span>
            <el-tag size="small" type="danger" style="margin-left:8px">{{ recentAlerts.length }}条待处理</el-tag>
          </template>
          <div v-if="recentAlerts.length === 0" class="no-alert">暂无预警</div>
          <div v-else class="alert-list">
            <div v-for="alert in recentAlerts" :key="alert.id" class="alert-item" @click="$router.push('/alerts')">
              <el-tag :type="getSeverityTag(alert.severity)" size="small" class="alert-tag">
                {{ alert.severity === 'CRITICAL' ? '严重' : alert.severity === 'WARNING' ? '警告' : '提示' }}
              </el-tag>
              <span class="alert-title">{{ alert.title }}</span>
              <span class="alert-time">{{ alert.createdAt?.slice(0, 16) }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getOverview, getMonthlyTrend, getMonthNewCount } from '@/api/dashboard'
import { getEnhancedOverview, getRecentAlerts } from '@/api/dashboard-enhanced'
import type { OverviewData, TrendItem } from '@/api/dashboard'
import type { EnhancedOverview, AlertItem } from '@/api/dashboard-enhanced'
import { ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()

const overview = reactive<OverviewData>({
  totalAssets: 0,
  totalOriginalValue: 0,
  totalCurrentValue: 0,
  avgDepreciationRate: 0,
  categoryDistribution: [],
  statusDistribution: []
})

const monthNew = ref<number | null>(null)
const trendData = ref<TrendItem[]>([])

// ===== M11: 综合驾驶舱数据 =====
const enhancedData = reactive<EnhancedOverview>({
  totalAssets: 0,
  activeEmployees: 0,
  activeProjects: 0,
  investPortfolioValue: 0,
  monthlyAlertCount: 0
})

const recentAlerts = ref<AlertItem[]>([])

const enhancedCards = computed(() => [
  { key: 'totalAssets', label: '资产总额', value: formatLargeCurrency(enhancedData.totalAssets), unit: '', color: '#409eff', route: '/assets' },
  { key: 'activeEmployees', label: '在岗人数', value: enhancedData.activeEmployees.toLocaleString(), unit: '人', color: '#67c23a', route: '/hr' },
  { key: 'activeProjects', label: '在建项目数', value: enhancedData.activeProjects.toLocaleString(), unit: '个', color: '#e6a23c', route: '/projects' },
  { key: 'investPortfolioValue', label: '投资组合市值', value: formatLargeCurrency(enhancedData.investPortfolioValue), unit: '', color: '#f56c6c', route: '/investment' },
  { key: 'monthlyAlertCount', label: '本月预警数', value: enhancedData.monthlyAlertCount.toLocaleString(), unit: '条', color: '#909399', route: '/alerts' }
])

function getSeverityTag(severity: string): string {
  if (severity === 'CRITICAL') return 'danger'
  if (severity === 'WARNING') return 'warning'
  return 'info'
}

function navigateTo(route: string) {
  router.push(route).catch(() => {})
}

const categoryChartRef = ref<HTMLDivElement | null>(null)
const statusChartRef = ref<HTMLDivElement | null>(null)
const trendChartRef = ref<HTMLDivElement | null>(null)

let categoryChart: echarts.ECharts | null = null
let statusChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null

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
  if (value >= 1e8) return (value / 1e8).toFixed(2) + '亿'
  if (value >= 1e4) return (value / 1e4).toFixed(2) + '万'
  return value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY' })
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

  categoryChart = echarts.init(categoryChartRef.value)
  categoryChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'],
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: (p: any) => `${p.name}\n${p.percent.toFixed(1)}%` },
      data: overview.categoryDistribution.map((item) => ({
        value: item.value,
        name: categoryMap[item.name] || item.name,
        itemStyle: { color: categoryColorMap[item.name] || '#999' }
      }))
    }]
  })

  statusChart = echarts.init(statusChartRef.value)
  statusChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'],
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: (p: any) => `${p.name}\n${p.percent.toFixed(1)}%` },
      data: overview.statusDistribution.map((item) => ({
        value: item.value,
        name: statusMap[item.name] || item.name,
        itemStyle: { color: statusColorMap[item.name] || '#999' }
      }))
    }]
  })

  initTrendChart()
}

function initTrendChart() {
  if (!trendChartRef.value || trendData.value.length === 0) return

  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 30, top: 20, bottom: 40 },
    xAxis: {
      type: 'category',
      data: trendData.value.map(t => t.month),
      axisLabel: { formatter: (v: string) => v.substring(5) }
    },
    yAxis: { type: 'value', name: '新增数量', minInterval: 1 },
    series: [{
      type: 'line', smooth: true,
      data: trendData.value.map(t => t.count),
      areaStyle: { opacity: 0.15 },
      itemStyle: { color: '#409eff' }
    }]
  })
}

function handleResize() {
  categoryChart?.resize()
  statusChart?.resize()
  trendChart?.resize()
}

async function fetchData() {
  try {
    const [overviewRes, trendRes, monthRes, enhancedRes, alertsRes] = await Promise.all([
      getOverview(),
      getMonthlyTrend(12),
      getMonthNewCount(),
      getEnhancedOverview().catch(() => ({ data: null })),
      getRecentAlerts().catch(() => ({ data: [] }))
    ])
    if (overviewRes.data) Object.assign(overview, overviewRes.data)
    trendData.value = trendRes.data || []
    monthNew.value = monthRes.data ?? 0
    if (enhancedRes.data) Object.assign(enhancedData, enhancedRes.data)
    recentAlerts.value = (alertsRes.data || []).slice(0, 5)
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
  trendChart?.dispose()
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

.stat-value.highlight {
  color: #409eff;
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

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
  margin: 24px 0 16px;
  padding-left: 12px;
  border-left: 3px solid #409eff;
}

.enhanced-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.enhanced-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.stat-unit {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.no-alert {
  text-align: center;
  color: #909399;
  padding: 32px 0;
}

.alert-list {
  max-height: 320px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s;
}
.alert-item:hover {
  background: #f5f7fa;
}
.alert-item:last-child {
  border-bottom: none;
}

.alert-tag {
  flex-shrink: 0;
}

.alert-title {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alert-time {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
}
</style>
