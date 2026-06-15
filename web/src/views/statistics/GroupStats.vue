<template>
  <div class="group-stats">
    <el-card shadow="never" class="filter-card">
      <el-form inline>
        <el-form-item label="选择组织">
          <el-tree-select
            v-model="selectedOrgId"
            :data="orgTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择集团/公司"
            filterable
            check-strictly
            style="width: 280px;"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :disabled="!selectedOrgId" @click="fetchStats">查询统计</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <template v-if="stats">
      <!-- Summary cards -->
      <el-row :gutter="20" class="stat-row">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-label">资产总数</div>
            <div class="stat-value">{{ stats.totalAssets }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-label">资产原值</div>
            <div class="stat-value">{{ formatCurrency(stats.totalOriginalValue) }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-label">资产净值</div>
            <div class="stat-value">{{ formatCurrency(stats.totalCurrentValue) }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-label">下属单位数</div>
            <div class="stat-value">{{ stats.subsidiaryCount }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Charts -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span class="card-title">下属单位资产对比</span></template>
            <div ref="compareChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span class="card-title">资产分类分布</span></template>
            <div ref="categoryChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Subsidiary table -->
      <el-card shadow="never" style="margin-top: 20px;">
        <template #header><span class="card-title">下属单位明细</span></template>
        <el-table :data="stats.subsidiaries" stripe>
          <el-table-column label="单位名称" prop="orgName" min-width="180" />
          <el-table-column label="资产数量" prop="totalAssets" width="120" />
          <el-table-column label="资产原值" width="160">
            <template #default="{ row }">{{ formatCurrency(row.totalOriginalValue) }}</template>
          </el-table-column>
          <el-table-column label="资产净值" width="160">
            <template #default="{ row }">{{ formatCurrency(row.totalCurrentValue) }}</template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <el-empty v-else-if="!loading" description="请选择组织后查看穿透统计" />
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getGroupStatistics } from '@/api/groupStats'
import type { GroupStatistics } from '@/api/groupStats'
import client from '@/api/client'

const selectedOrgId = ref<string>('')
const stats = ref<GroupStatistics | null>(null)
const loading = ref(false)
const orgTree = ref<any[]>([])

const compareChartRef = ref<HTMLDivElement | null>(null)
const categoryChartRef = ref<HTMLDivElement | null>(null)
let compareChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null

function formatCurrency(value: number | null): string {
  if (value == null) return '-'
  if (value >= 1e8) return (value / 1e8).toFixed(2) + '亿'
  if (value >= 1e4) return (value / 1e4).toFixed(2) + '万'
  return value.toFixed(2) + '元'
}

async function fetchOrgTree() {
  try {
    const res = await client.get('/organizations/tree') as any
    orgTree.value = res.data || []
  } catch { orgTree.value = [] }
}

async function fetchStats() {
  if (!selectedOrgId.value) return
  loading.value = true
  try {
    const res = await getGroupStatistics(selectedOrgId.value)
    stats.value = res.data
    await nextTick()
    renderCharts()
  } catch {
    stats.value = null
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  if (!stats.value) return
  renderCompareChart()
  renderCategoryChart()
}

function renderCompareChart() {
  if (!compareChartRef.value || !stats.value) return
  compareChart?.dispose()
  compareChart = echarts.init(compareChartRef.value)
  const subs = stats.value.subsidiaries
  compareChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 80, right: 30, top: 20, bottom: 40 },
    xAxis: { type: 'category', data: subs.map(s => s.orgName), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', name: '资产数量' },
    series: [
      { name: '资产数量', type: 'bar', data: subs.map(s => s.totalAssets), itemStyle: { color: '#409eff' } }
    ]
  })
}

function renderCategoryChart() {
  if (!categoryChartRef.value || !stats.value) return
  categoryChart?.dispose()
  categoryChart = echarts.init(categoryChartRef.value)
  const cats = stats.value.categoryDistribution
  categoryChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: ['35%', '60%'],
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
      data: cats.map(c => ({ name: c.name, value: c.count }))
    }]
  })
}

function handleResize() { compareChart?.resize(); categoryChart?.resize() }

onMounted(() => {
  fetchOrgTree()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  compareChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.filter-card { margin-bottom: 20px; }
.stat-row { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.chart-row { margin-bottom: 20px; }
.chart-container { width: 100%; height: 320px; }
.card-title { font-size: 15px; font-weight: 600; }
</style>
