<template>
  <div class="report-page">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="card-title">资产处置统计</span>
          <div class="header-actions">
            <el-date-picker v-model="dateRange" type="daterange" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 260px;" />
            <el-button type="primary" @click="fetchData">查询</el-button>
          </div>
        </div>
      </template>
      <div ref="chartRef" class="chart-container"></div>
      <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 20px;">
        <el-table-column label="处置方式" width="140">
          <template #default="{ row }">{{ disposalLabel(row.disposalType) }}</template>
        </el-table-column>
        <el-table-column label="处置数量" prop="count" width="120" sortable />
        <el-table-column label="原值合计(万元)" width="160">
          <template #default="{ row }">{{ (row.originalValue / 10000).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDisposalStats } from '@/api/reportStats'
import type { DisposalStatItem } from '@/api/reportStats'

const loading = ref(false)
const tableData = ref<DisposalStatItem[]>([])
const dateRange = ref<[string, string] | null>(null)
const chartRef = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null

const labels: Record<string, string> = { SCRAP: '报废', TRANSFER: '转让', AUCTION: '拍卖', DONATE: '捐赠', OTHER: '其他' }
function disposalLabel(t: string) { return labels[t] || t }

async function fetchData() {
  loading.value = true
  try {
    const params: any = {}
    if (dateRange.value) { params.startDate = dateRange.value[0]; params.endDate = dateRange.value[1] }
    const res = await getDisposalStats(params)
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
    tooltip: { trigger: 'item', formatter: '{b}: {c}项' },
    series: [{ type: 'pie', radius: ['35%', '60%'], data: tableData.value.map(d => ({ name: disposalLabel(d.disposalType), value: d.count })) }]
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
