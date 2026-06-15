<template>
  <div class="price-trend">
    <h2>历史价格趋势</h2>

    <el-card>
      <div class="filter-bar">
        <el-input v-model="category" placeholder="品类关键词" style="width: 200px" @keyup.enter="loadData" />
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" style="margin-left: 12px" />
        <el-button type="primary" style="margin-left: 12px" @click="loadData">查询</el-button>
      </div>

      <el-row :gutter="16" style="margin: 16px 0" v-if="analysis">
        <el-col :span="6"><el-statistic title="平均价格" :value="`¥${analysis.avgPrice}`" /></el-col>
        <el-col :span="6"><el-statistic title="最低价格" :value="`¥${analysis.minPrice}`" /></el-col>
        <el-col :span="6"><el-statistic title="最高价格" :value="`¥${analysis.maxPrice}`" /></el-col>
        <el-col :span="6"><el-statistic title="记录数" :value="analysis.recordCount" /></el-col>
      </el-row>

      <div ref="chartRef" style="height: 360px"></div>

      <el-table :data="records" stripe style="margin-top: 16px" max-height="400">
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="supplierName" label="供应商" width="180" />
        <el-table-column prop="specification" label="规格" min-width="160" />
        <el-table-column prop="unitPrice" label="单价" width="120" align="right">
          <template #default="{ row }">¥{{ Number(row.unitPrice).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="sourceType" label="来源" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getPriceTrend, getPriceAnalysis } from '@/api/quotation'
import type { PriceHistory, PriceAnalysis } from '@/api/quotation'

const category = ref('')
const dateRange = ref<[string, string] | null>(null)
const records = ref<PriceHistory[]>([])
const analysis = ref<PriceAnalysis | null>(null)
const chartRef = ref<HTMLElement>()

async function loadData() {
  if (!category.value) return

  const params: any = { category: category.value }
  if (dateRange.value) {
    params.startDate = dateRange.value[0]
    params.endDate = dateRange.value[1]
  }

  const [trendRes, analysisRes] = await Promise.all([
    getPriceTrend(params),
    getPriceAnalysis(category.value)
  ])
  records.value = trendRes.data || []
  analysis.value = analysisRes.data

  await nextTick()
  renderChart()
}

function renderChart() {
  if (!chartRef.value || records.value.length === 0) return
  const chart = echarts.init(chartRef.value)
  const suppliers = [...new Set(records.value.map(r => r.supplierName))]

  const series = suppliers.map(supplier => ({
    name: supplier,
    type: 'line' as const,
    smooth: true,
    data: records.value.filter(r => r.supplierName === supplier).map(r => [r.recordDate, r.unitPrice])
  }))

  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: suppliers },
    xAxis: { type: 'time' },
    yAxis: { type: 'value', name: '单价(元)' },
    series
  })
}

onMounted(() => {
  category.value = '办公设备'
  loadData()
})
</script>

<style scoped>
.filter-bar { display: flex; align-items: center; margin-bottom: 8px; }
</style>
