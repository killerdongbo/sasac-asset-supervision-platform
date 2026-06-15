<template>
  <div class="report-page">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="card-title">出租出借统计</span>
          <el-button type="primary" @click="fetchData">刷新</el-button>
        </div>
      </template>
      <el-row :gutter="20" class="stat-row">
        <el-col :span="8">
          <el-statistic title="出租资产总数" :value="totalCount" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="出租原值(万元)" :value="+(totalOriginal / 10000).toFixed(2)" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="涉及单位数" :value="tableData.length" />
        </el-col>
      </el-row>
      <div ref="chartRef" class="chart-container"></div>
      <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 20px;">
        <el-table-column label="单位名称" prop="orgName" min-width="180" />
        <el-table-column label="出租数量" prop="assetCount" width="120" sortable />
        <el-table-column label="出租原值(万元)" width="160" sortable>
          <template #default="{ row }">{{ (row.originalValue / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="出租净值(万元)" width="160">
          <template #default="{ row }">{{ (row.currentValue / 10000).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getRentalStats } from '@/api/reportStats'
import type { OrgAssetSummary } from '@/api/reportStats'

const loading = ref(false)
const tableData = ref<OrgAssetSummary[]>([])
const chartRef = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null

const totalCount = computed(() => tableData.value.reduce((s, d) => s + d.assetCount, 0))
const totalOriginal = computed(() => tableData.value.reduce((s, d) => s + d.originalValue, 0))

async function fetchData() {
  loading.value = true
  try {
    const res = await getRentalStats()
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
    grid: { left: 80, right: 30, top: 20, bottom: 40 },
    xAxis: { type: 'category', data: tableData.value.map(d => d.orgName), axisLabel: { rotate: 20 } },
    yAxis: { type: 'value', name: '数量' },
    series: [{ type: 'bar', data: tableData.value.map(d => d.assetCount), itemStyle: { color: '#409eff' } }]
  })
}

function handleResize() { chart?.resize() }
onMounted(() => { fetchData(); window.addEventListener('resize', handleResize) })
onBeforeUnmount(() => { window.removeEventListener('resize', handleResize); chart?.dispose() })
</script>

<style scoped>
.header-row { display: flex; align-items: center; justify-content: space-between; }
.card-title { font-size: 16px; font-weight: 600; }
.stat-row { margin-bottom: 20px; }
.chart-container { width: 100%; height: 280px; }
</style>
