<template>
  <div class="report-page">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="card-title">资产总量统计</span>
          <div class="header-actions">
            <el-date-picker v-model="dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 260px;" />
            <el-button type="primary" @click="fetchData">查询</el-button>
          </div>
        </div>
      </template>
      <div ref="chartRef" class="chart-container"></div>
      <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 20px;">
        <el-table-column label="单位名称" prop="orgName" min-width="180" />
        <el-table-column label="资产数量" prop="assetCount" width="120" sortable />
        <el-table-column label="资产原值(万元)" width="160" sortable>
          <template #default="{ row }">{{ (row.originalValue / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="资产净值(万元)" width="160" sortable>
          <template #default="{ row }">{{ (row.currentValue / 10000).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="累计折旧(万元)" width="160">
          <template #default="{ row }">{{ (row.accumulatedDepreciation / 10000).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getAssetSummary } from '@/api/reportStats'
import type { OrgAssetSummary } from '@/api/reportStats'

const loading = ref(false)
const tableData = ref<OrgAssetSummary[]>([])
const dateRange = ref<[string, string] | null>(null)
const chartRef = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null

async function fetchData() {
  loading.value = true
  try {
    const params: any = {}
    if (dateRange.value) { params.startDate = dateRange.value[0]; params.endDate = dateRange.value[1] }
    const res = await getAssetSummary(params)
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
    grid: { left: 80, right: 30, top: 20, bottom: 50 },
    xAxis: { type: 'category', data: tableData.value.map(d => d.orgName), axisLabel: { rotate: 30 } },
    yAxis: [{ type: 'value', name: '数量' }, { type: 'value', name: '万元' }],
    series: [
      { name: '资产数量', type: 'bar', data: tableData.value.map(d => d.assetCount), itemStyle: { color: '#409eff' } },
      { name: '原值(万元)', type: 'line', yAxisIndex: 1, data: tableData.value.map(d => +(d.originalValue / 10000).toFixed(2)), itemStyle: { color: '#67c23a' } }
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
.header-actions { display: flex; gap: 12px; align-items: center; }
.chart-container { width: 100%; height: 300px; }
</style>
