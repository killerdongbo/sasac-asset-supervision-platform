<template>
  <div class="report-page">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="card-title">折旧分析报表</span>
          <el-button type="primary" @click="fetchData">刷新</el-button>
        </div>
      </template>
      <div ref="chartRef" class="chart-container"></div>
      <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 20px;">
        <el-table-column label="单位名称" prop="orgName" min-width="180" />
        <el-table-column label="资产数量" prop="assetCount" width="100" />
        <el-table-column label="原值(万元)" width="140" sortable>
          <template #default="{ row }">{{ (row.originalValue / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="净值(万元)" width="140">
          <template #default="{ row }">{{ (row.currentValue / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="累计折旧(万元)" width="150">
          <template #default="{ row }">{{ (row.accumulatedDepreciation / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="折旧率" width="100" sortable>
          <template #default="{ row }">{{ row.originalValue > 0 ? ((row.accumulatedDepreciation / row.originalValue) * 100).toFixed(1) + '%' : '-' }}</template>
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
import { getDepreciationAnalysis } from '@/api/reportStats'
import type { OrgAssetSummary } from '@/api/reportStats'

const loading = ref(false)
const tableData = ref<OrgAssetSummary[]>([])
const chartRef = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null

async function fetchData() {
  loading.value = true
  try {
    const res = await getDepreciationAnalysis()
    tableData.value = res.data || []
    await nextTick()
    renderChart()
  } finally { loading.value = false }
}

function renderChart() {
  if (!chartRef.value || tableData.value.length === 0) return
  chart?.dispose()
  chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    grid: { left: 80, right: 30, top: 20, bottom: 50 },
    xAxis: { type: 'category', data: tableData.value.map(d => d.orgName), axisLabel: { rotate: 20 } },
    yAxis: { type: 'value', name: '万元' },
    series: [
      { name: '原值', type: 'bar', stack: 'a', data: tableData.value.map(d => +(d.originalValue / 10000).toFixed(2)), itemStyle: { color: '#409eff' } },
      { name: '净值', type: 'bar', stack: 'b', data: tableData.value.map(d => +(d.currentValue / 10000).toFixed(2)), itemStyle: { color: '#67c23a' } },
      { name: '累计折旧', type: 'bar', stack: 'b', data: tableData.value.map(d => +(d.accumulatedDepreciation / 10000).toFixed(2)), itemStyle: { color: '#e6a23c' } }
    ]
  })
}

function handleResize() { chart?.resize() }
onMounted(() => { fetchData(); window.addEventListener('resize', handleResize) })
onBeforeUnmount(() => { window.removeEventListener('resize', handleResize); chart?.dispose() })
</script>

<style scoped>
.header-row { display: flex; align-items: center; justify-content: space-between; }
.card-title { font-size: 16px; font-weight: 600; }
.chart-container { width: 100%; height: 300px; }
</style>
