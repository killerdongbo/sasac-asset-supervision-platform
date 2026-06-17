<template>
  <div class="performance-scoreboard">
    <div class="page-header">
      <h2>考核评分看板</h2>
      <div class="header-actions">
        <el-select v-model="queryYear" placeholder="选择年份" style="width: 140px;">
          <el-option v-for="y in yearOptions" :key="y" :label="y" :value="y" />
        </el-select>
        <el-button type="primary" @click="fetchData" :loading="loading" style="margin-left: 8px;">刷新</el-button>
      </div>
    </div>

    <!-- Summary Stats -->
    <el-row :gutter="16" style="margin-bottom: 16px;">
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-card">
            <div class="stat-label">综合得分</div>
            <div class="stat-value" :style="{ color: totalScoreColor }">{{ totalScore }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-card">
            <div class="stat-label">考核等级</div>
            <div class="stat-value" :style="{ color: gradeColor }">{{ grade }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-card">
            <div class="stat-label">指标数量</div>
            <div class="stat-value" style="color: #409eff;">{{ indicators.length }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="stat-card">
            <div class="stat-label">达标率</div>
            <div class="stat-value" :style="{ color: passRateColor }">{{ passRate }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- Radar Chart -->
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>指标雷达图</template>
          <div ref="chartRef" style="height: 420px;"></div>
        </el-card>
      </el-col>

      <!-- Detail Cards -->
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>指标明细</template>
          <div class="indicator-cards">
            <div
              v-for="ind in indicators"
              :key="ind.indicatorCode"
              class="indicator-card"
              :style="{ borderLeftColor: scoreColor(ind.score) }"
            >
              <div class="card-header">
                <span class="card-name">{{ ind.indicatorName }}</span>
                <el-tag :type="scoreTag(ind.score)" size="small">{{ ind.score ?? '-' }}</el-tag>
              </div>
              <div class="card-meta">
                <span>权重: {{ ind.weight }}%</span>
                <span>目标: {{ ind.targetValue }}</span>
                <span>实际: {{ ind.actualValue ?? '-' }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { getIndicatorSummary, type PerfIndicatorDef } from '@/api/performance'
import * as echarts from 'echarts'

const loading = ref(false)
const indicators = ref<PerfIndicatorDef[]>([])
const queryYear = ref(new Date().getFullYear())
const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

const yearOptions = computed(() => {
  const year = new Date().getFullYear()
  return [year - 2, year - 1, year, year + 1]
})

const totalScore = computed(() => {
  if (indicators.value.length === 0) return '-'
  const sum = indicators.value.reduce((acc, ind) => acc + (Number(ind.score) || 0), 0)
  return sum.toFixed(1)
})

const totalScoreColor = computed(() => {
  const s = Number(totalScore.value)
  if (s >= 90) return '#67c23a'
  if (s >= 70) return '#409eff'
  if (s >= 60) return '#e6a23c'
  return '#f56c6c'
})

const grade = computed(() => {
  const s = Number(totalScore.value)
  if (s >= 90) return 'A (优秀)'
  if (s >= 80) return 'B (良好)'
  if (s >= 70) return 'C (中等)'
  if (s >= 60) return 'D (及格)'
  return 'D (不及格)'
})

const gradeColor = computed(() => totalScoreColor.value)

const passRate = computed(() => {
  if (indicators.value.length === 0) return '-'
  const passed = indicators.value.filter(ind => Number(ind.score) >= 60).length
  return ((passed / indicators.value.length) * 100).toFixed(1) + '%'
})

const passRateColor = computed(() => {
  const p = parseFloat(passRate.value)
  if (p >= 90) return '#67c23a'
  if (p >= 70) return '#409eff'
  return '#e6a23c'
})

const scoreColor = (score: number) => {
  if (score == null) return '#909399'
  if (score >= 90) return '#67c23a'
  if (score >= 70) return '#409eff'
  if (score >= 60) return '#e6a23c'
  return '#f56c6c'
}

const scoreTag = (score: number) => {
  if (score == null) return 'info'
  if (score >= 90) return 'success'
  if (score >= 70) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

function renderChart() {
  if (!chartRef.value || indicators.value.length === 0) return

  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }

  const names = indicators.value.map(ind => ind.indicatorName)
  const scores = indicators.value.map(ind => Number(ind.score) || 0)

  // Calculate max for radar: use 100 if any score could exceed, otherwise use max score * 1.2
  const maxScore = Math.max(...scores, 1) * 1.2

  chartInstance.setOption({
    radar: {
      indicator: names.map((name, i) => ({
        name,
        max: Math.max(100, Math.ceil(maxScore / 10) * 10),
      })),
      center: ['50%', '50%'],
      radius: '65%',
    },
    series: [{
      type: 'radar',
      data: [{
        value: scores,
        name: '考核得分',
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.3)',
        },
        lineStyle: {
          color: '#409eff',
          width: 2,
        },
        itemStyle: {
          color: '#409eff',
        },
      }],
    }],
  })
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getIndicatorSummary({
      tenantId: Number(localStorage.getItem('tenantId') || 1),
      orgId: Number(localStorage.getItem('orgId') || 1),
      year: queryYear.value,
    })
    indicators.value = res.data || []
    await nextTick()
    renderChart()
  } finally {
    loading.value = false
  }
}

watch(queryYear, () => {
  fetchData()
})

onMounted(fetchData)

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.performance-scoreboard {
  padding: 16px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
.header-actions {
  display: flex;
  align-items: center;
}
.stat-card {
  text-align: center;
  padding: 8px 0;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 32px;
  font-weight: 700;
}
.indicator-cards {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 420px;
  overflow-y: auto;
}
.indicator-card {
  border-left: 4px solid #409eff;
  padding: 10px 12px;
  background: #f9fafb;
  border-radius: 4px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.card-name {
  font-weight: 600;
  font-size: 13px;
}
.card-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}
</style>
