<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getAssetStats } from '@/api/statistics'

const stats = ref<any>({})
const categoryChartRef = ref<HTMLElement>()
const statusChartRef = ref<HTMLElement>()

onMounted(async () => {
  const res = await getAssetStats(); stats.value = res.data || {}

  if (categoryChartRef.value) {
    const chart = echarts.init(categoryChartRef.value)
    chart.setOption({
      title: { text: '资产分类分布', left: 'center' },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: Object.entries(stats.value.byCategory || {}).map(([name, value]) => ({ name, value }))
      }]
    })
  }
  if (statusChartRef.value) {
    const chart = echarts.init(statusChartRef.value)
    chart.setOption({
      title: { text: '资产状态分布', left: 'center' },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: '65%',
        data: Object.entries(stats.value.byStatus || {}).map(([name, value]) => ({ name, value }))
      }]
    })
  }
})
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>资产统计报表</h3></div>
    <el-row :gutter="16" style="margin-bottom:20px">
      <el-col :span="6"><el-statistic title="资产总数" :value="stats.totalCount || 0" /></el-col>
      <el-col :span="6"><el-statistic title="资产原值(元)" :value="stats.totalOriginalValue || 0" :formatter="(v:number) => v.toLocaleString()" /></el-col>
      <el-col :span="6"><el-statistic title="资产净值(元)" :value="stats.totalCurrentValue || 0" :formatter="(v:number) => v.toLocaleString()" /></el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="12"><div ref="categoryChartRef" style="height:380px"></div></el-col>
      <el-col :span="12"><div ref="statusChartRef" style="height:380px"></div></el-col>
    </el-row>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
