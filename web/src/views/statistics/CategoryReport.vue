<template>
  <div class="report-page">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="card-title">资产分类统计</span>
          <el-button type="primary" @click="fetchData">刷新</el-button>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="12"><div ref="pieRef" class="chart-container"></div></el-col>
        <el-col :span="12"><div ref="barRef" class="chart-container"></div></el-col>
      </el-row>
      <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 20px;">
        <el-table-column label="资产分类" prop="category" width="140">
          <template #default="{ row }">{{ categoryLabel(row.category) }}</template>
        </el-table-column>
        <el-table-column label="数量" prop="count" width="100" sortable />
        <el-table-column label="原值(万元)" width="150" sortable>
          <template #default="{ row }">{{ (row.originalValue / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="净值(万元)" width="150" sortable>
          <template #default="{ row }">{{ (row.currentValue / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="折旧(万元)" width="150">
          <template #default="{ row }">{{ (row.depreciation / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="净值率" width="100">
          <template #default="{ row }">{{ row.originalValue > 0 ? ((row.currentValue / row.originalValue) * 100).toFixed(1) + '%' : '-' }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getCategoryStats } from '@/api/reportStats'
import type { CategoryStatItem } from '@/api/reportStats'

const loading = ref(false)
const tableData = ref<CategoryStatItem[]>([])
const pieRef = ref<HTMLDivElement | null>(null)
const barRef = ref<HTMLDivElement | null>(null)
let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

const labels: Record<string, string> = {
  LAND: '土地', REAL_ESTATE: '房产', EQUIPMENT: '设备',
  VEHICLE: '车辆', FURNITURE: '家具', IT_EQUIPMENT: 'IT设备'
}
function categoryLabel(c: string) { return labels[c] || c }

async function fetchData() {
  loading.value = true
  try {
    const res = await getCategoryStats()
    tableData.value = res.data || []
    await nextTick()
    renderCharts()
  } finally { loading.value = false }
}

function renderCharts() {
  if (!pieRef.value || !barRef.value) return
  pieChart?.dispose(); barChart?.dispose()
  pieChart = echarts.init(pieRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    series: [{ type: 'pie', radius: ['35%', '60%'], data: tableData.value.map(d => ({ name: categoryLabel(d.category), value: d.count })) }]
  })
  barChart = echarts.init(barRef.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 20, top: 20, bottom: 40 },
    xAxis: { type: 'category', data: tableData.value.map(d => categoryLabel(d.category)) },
    yAxis: { type: 'value', name: '万元' },
    series: [
      { name: '原值', type: 'bar', stack: 'val', data: tableData.value.map(d => +(d.originalValue / 10000).toFixed(2)), itemStyle: { color: '#409eff' } },
      { name: '净值', type: 'bar', stack: 'val', data: tableData.value.map(d => +(d.currentValue / 10000).toFixed(2)), itemStyle: { color: '#67c23a' } }
    ]
  })
}

function handleResize() { pieChart?.resize(); barChart?.resize() }
onMounted(() => { fetchData(); window.addEventListener('resize', handleResize) })
onBeforeUnmount(() => { window.removeEventListener('resize', handleResize); pieChart?.dispose(); barChart?.dispose() })
</script>

<style scoped>
.header-row { display: flex; align-items: center; justify-content: space-between; }
.card-title { font-size: 16px; font-weight: 600; }
.chart-container { width: 100%; height: 280px; }
</style>
