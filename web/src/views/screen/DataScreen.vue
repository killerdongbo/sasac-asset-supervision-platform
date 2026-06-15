<template>
  <div class="data-screen" ref="screenRef">
    <div class="screen-header">
      <h1>湛江市国资国企数字监管大屏</h1>
      <div class="header-time">{{ currentTime }}</div>
    </div>

    <div class="screen-body">
      <!-- 左列 -->
      <div class="col col-left">
        <div class="panel">
          <div class="panel-title">资产状态分布</div>
          <div ref="pieRef" class="chart-box"></div>
        </div>
        <div class="panel">
          <div class="panel-title">Top10 单位资产排名</div>
          <div ref="rankRef" class="chart-box"></div>
        </div>
      </div>

      <!-- 中列 -->
      <div class="col col-center">
        <div class="overview-cards">
          <div class="ov-card" v-for="item in overviewCards" :key="item.label">
            <div class="ov-value">{{ item.value }}</div>
            <div class="ov-label">{{ item.label }}</div>
          </div>
        </div>
        <div class="panel" style="flex: 1">
          <div class="panel-title">资产区域分布</div>
          <div ref="mapRef" class="chart-box"></div>
        </div>
      </div>

      <!-- 右列 -->
      <div class="col col-right">
        <div class="panel">
          <div class="panel-title">月度新增趋势</div>
          <div ref="trendRef" class="chart-box"></div>
        </div>
        <div class="panel">
          <div class="panel-title">实时预警</div>
          <div class="alert-list">
            <div v-for="(alert, i) in data?.recentAlerts" :key="i" class="alert-item" :class="'level-' + alert.level">
              <span class="alert-dot"></span>
              <span class="alert-text">{{ alert.title }}</span>
              <span class="alert-time">{{ alert.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-button class="fullscreen-btn" circle :icon="'FullScreen'" @click="toggleFullscreen" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getScreenData } from '@/api/screen'
import type { ScreenData } from '@/api/screen'

const screenRef = ref<HTMLElement>()
const pieRef = ref<HTMLElement>()
const rankRef = ref<HTMLElement>()
const mapRef = ref<HTMLElement>()
const trendRef = ref<HTMLElement>()
const data = ref<ScreenData | null>(null)
const currentTime = ref('')

let timer: ReturnType<typeof setInterval>

const overviewCards = computed(() => {
  if (!data.value) return []
  const o = data.value.overview
  return [
    { label: '资产总数', value: o.totalAssets.toLocaleString() },
    { label: '资产原值(万)', value: (o.totalOriginalValue / 10000).toFixed(1) },
    { label: '资产净值(万)', value: (o.totalCurrentValue / 10000).toFixed(1) },
    { label: '本月新增', value: o.monthNewAssets },
    { label: '下属单位', value: o.totalOrgs },
    { label: '系统用户', value: o.totalUsers },
  ]
})

function updateTime() {
  currentTime.value = new Date().toLocaleString('zh-CN', { hour12: false })
}

async function loadData() {
  const res = await getScreenData()
  data.value = res.data
  await nextTick()
  renderCharts()
}

function renderCharts() {
  if (!data.value) return
  renderPie()
  renderRank()
  renderMap()
  renderTrend()
}

function renderPie() {
  if (!pieRef.value) return
  const chart = echarts.init(pieRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'],
    series: [{ type: 'pie', radius: ['40%', '70%'], label: { color: '#fff' },
      data: data.value!.statusDistribution.map(s => ({ name: s.name, value: s.value }))
    }]
  })
}

function renderRank() {
  if (!rankRef.value) return
  const chart = echarts.init(rankRef.value)
  const orgs = data.value!.topOrgs.slice().reverse()
  chart.setOption({
    tooltip: {},
    grid: { left: 100, right: 30, top: 10, bottom: 20 },
    xAxis: { type: 'value', axisLabel: { color: '#aaa' }, splitLine: { lineStyle: { color: '#333' } } },
    yAxis: { type: 'category', data: orgs.map(o => o.orgName), axisLabel: { color: '#ccc', fontSize: 11 } },
    series: [{ type: 'bar', data: orgs.map(o => o.assetCount), itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#1890ff' }, { offset: 1, color: '#36cfc9' }]) }, barWidth: 14 }]
  })
}

function renderMap() {
  if (!mapRef.value) return
  const chart = echarts.init(mapRef.value)
  const regions = data.value!.regionDistribution
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 30, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: regions.map(r => r.name), axisLabel: { color: '#aaa', rotate: 30 } },
    yAxis: { type: 'value', axisLabel: { color: '#aaa' }, splitLine: { lineStyle: { color: '#333' } } },
    series: [{ type: 'bar', data: regions.map(r => r.value), itemStyle: { color: '#409EFF' }, barWidth: 20 }]
  })
}

function renderTrend() {
  if (!trendRef.value) return
  const chart = echarts.init(trendRef.value)
  const trend = data.value!.monthlyTrend
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: trend.map(t => t.month.slice(5)), axisLabel: { color: '#aaa' }, boundaryGap: false },
    yAxis: { type: 'value', axisLabel: { color: '#aaa' }, splitLine: { lineStyle: { color: '#333' } } },
    series: [{ type: 'line', smooth: true, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(64,158,255,0.5)' }, { offset: 1, color: 'rgba(64,158,255,0.05)' }]) }, lineStyle: { color: '#409EFF' }, data: trend.map(t => t.count) }]
  })
}

function toggleFullscreen() {
  if (!document.fullscreenElement) {
    screenRef.value?.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadData()
})

onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.data-screen { width: 100vw; height: 100vh; background: linear-gradient(135deg, #0a1628 0%, #1a2a4a 100%); color: #fff; display: flex; flex-direction: column; overflow: hidden; position: fixed; top: 0; left: 0; z-index: 9999; }
.screen-header { text-align: center; padding: 12px 0 8px; border-bottom: 1px solid rgba(64,158,255,0.3); }
.screen-header h1 { font-size: 26px; margin: 0; letter-spacing: 4px; background: linear-gradient(to right, #409EFF, #36cfc9); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.header-time { font-size: 13px; color: #8899aa; margin-top: 4px; }
.screen-body { flex: 1; display: flex; gap: 12px; padding: 12px; min-height: 0; }
.col { display: flex; flex-direction: column; gap: 12px; }
.col-left, .col-right { flex: 1; }
.col-center { flex: 1.4; }
.panel { flex: 1; background: rgba(255,255,255,0.04); border: 1px solid rgba(64,158,255,0.2); border-radius: 8px; padding: 12px; display: flex; flex-direction: column; }
.panel-title { font-size: 14px; font-weight: 600; color: #8ecdf7; margin-bottom: 8px; padding-left: 8px; border-left: 3px solid #409EFF; }
.chart-box { flex: 1; min-height: 0; }
.overview-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin-bottom: 12px; }
.ov-card { background: rgba(64,158,255,0.1); border: 1px solid rgba(64,158,255,0.25); border-radius: 6px; padding: 12px 8px; text-align: center; }
.ov-value { font-size: 22px; font-weight: 700; color: #36cfc9; }
.ov-label { font-size: 12px; color: #8899aa; margin-top: 4px; }
.alert-list { flex: 1; overflow-y: auto; }
.alert-item { display: flex; align-items: center; gap: 8px; padding: 8px; border-bottom: 1px solid rgba(255,255,255,0.05); font-size: 13px; }
.alert-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.level-WARNING .alert-dot { background: #E6A23C; }
.level-INFO .alert-dot { background: #409EFF; }
.level-CRITICAL .alert-dot { background: #F56C6C; }
.alert-text { flex: 1; color: #ddd; }
.alert-time { color: #666; font-size: 11px; }
.fullscreen-btn { position: fixed; bottom: 20px; right: 20px; z-index: 10000; }
</style>
