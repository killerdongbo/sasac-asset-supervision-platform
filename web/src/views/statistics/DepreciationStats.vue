<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getDepreciationStats } from '@/api/statistics'

const stats = ref<any>({})
const chartRef = ref<HTMLElement>()

onMounted(async () => {
  const res = await getDepreciationStats(); stats.value = res.data || {}
  if (chartRef.value) {
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      title: { text: '月度折旧趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: stats.value.months || [] },
      yAxis: { type: 'value' },
      series: [{ data: stats.value.amounts || [], type: 'bar', itemStyle: { color: '#409EFF' } }]
    })
  }
})
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>折旧统计</h3></div>
    <el-row :gutter="16" style="margin-bottom:20px">
      <el-col :span="8"><el-statistic title="累计折旧(元)" :value="stats.totalDepreciation || 0" :formatter="(v:number) => v.toLocaleString()" /></el-col>
      <el-col :span="8"><el-statistic title="月均折旧(元)" :value="stats.avgMonthly || 0" :formatter="(v:number) => v.toLocaleString()" /></el-col>
    </el-row>
    <div ref="chartRef" style="height:400px"></div>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
