<template>
  <view class="finance-snapshot">
    <!-- 核心指标卡片 -->
    <view class="indicators">
      <view class="indicator-card" v-for="indicator in indicators" :key="indicator.id">
        <text class="indicator-label">{{ indicator.indicatorName || indicator.indicatorCode }}</text>
        <text class="indicator-value" style="color: #409eff;">{{ formatPct(indicator.indicatorValue) }}</text>
        <text class="indicator-trend">指标值: {{ indicator.indicatorValue }}</text>
      </view>
      <view v-if="indicators.length === 0 && !loading" class="empty-tip">
        <text>暂无财务指标数据</text>
      </view>
    </view>

    <!-- 财务简表 -->
    <view class="table-card">
      <text class="table-title">主要财务数据（万元）</text>
      <view class="table-row header">
        <text class="col-name">指标</text>
        <text class="col-value">本期数</text>
        <text class="col-value">上期数</text>
        <text class="col-value">同比</text>
      </view>
      <view class="table-row" v-for="row in financeData" :key="row.name">
        <text class="col-name">{{ row.name }}</text>
        <text class="col-value">{{ row.current }}</text>
        <text class="col-value">{{ row.prev }}</text>
        <text class="col-value" :style="{ color: row.change > 0 ? '#67c23a' : '#f56c6c' }">{{ row.change }}%</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/index'
import { queryIndicators } from '@/api/finance'

const authStore = useAuthStore()

const indicators = ref([])
const financeData = ref([])
const loading = ref(false)

function formatValue(val) {
  if (!val && val !== 0) return '-'
  const num = Number(val)
  if (num >= 100000000) return (num / 100000000).toFixed(1) + '亿'
  if (num >= 10000) return (num / 10000).toFixed(1) + '万'
  return num.toLocaleString()
}

function formatPct(val) {
  if (!val && val !== 0) return '-'
  return Number(val).toFixed(1) + '%'
}

async function fetchData() {
  loading.value = true
  try {
    const now = new Date()
    const res = await queryIndicators({
      tenantId: authStore.tenantId,
      periodYear: now.getFullYear()
    })

    if (res.success && res.data) {
      indicators.value = res.data.slice(0, 3)
      financeData.value = res.data.map(item => ({
        name: item.indicatorName || item.indicatorCode,
        current: formatValue(item.indicatorValue),
        prev: '-',
        change: 0
      }))
    }
  } catch (err) {
    console.error('Failed to fetch indicators:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss">
.finance-snapshot {
  padding: 16px;
}
.indicators {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}
.indicator-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}
.indicator-label {
  font-size: 13px;
  color: #999;
}
.indicator-value {
  font-size: 28px;
  font-weight: 700;
  display: block;
  margin: 6px 0;
}
.indicator-trend {
  font-size: 12px;
  color: #999;
}
.table-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}
.table-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 12px;
}
.table-row {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.table-row.header {
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #f0f0f0;
}
.table-row:last-child {
  border-bottom: none;
}
.col-name {
  flex: 2;
  font-size: 14px;
  color: #666;
}
.col-value {
  flex: 1;
  text-align: right;
  font-size: 13px;
  color: #333;
}
</style>
