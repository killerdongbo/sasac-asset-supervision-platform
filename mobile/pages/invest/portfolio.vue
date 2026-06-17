<template>
  <view class="portfolio-page">
    <!-- 总览卡片 -->
    <view class="summary-card">
      <text class="summary-label">投资组合总市值</text>
      <text class="summary-value">{{ summary.totalValue ? formatAmount(summary.totalValue) : '-' }}</text>
      <text class="summary-change">较上月 {{ summary.change > 0 ? '+' : '' }}{{ summary.change }}%</text>
    </view>

    <!-- 列表 -->
    <view class="list-section">
      <view class="list-title">投资项目</view>
      <view class="invest-item" v-for="item in investList" :key="item.id">
        <view class="item-top">
          <text class="item-name">{{ item.name }}</text>
          <uni-tag :text="item.status" :type="item.statusType" size="small"></uni-tag>
        </view>
        <view class="item-metrics">
          <view class="metric">
            <text class="metric-label">投资金额</text>
            <text class="metric-value">{{ item.amount }}</text>
          </view>
          <view class="metric">
            <text class="metric-label">持股比例</text>
            <text class="metric-value">{{ item.equity }}</text>
          </view>
          <view class="metric">
            <text class="metric-label">年化ROI</text>
            <text class="metric-value" :style="{ color: item.roi > 0 ? '#67c23a' : '#f56c6c' }">{{ item.roi }}%</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { queryInvestmentProjects, getPortfolioSummary } from '@/api/investment'

const investList = ref([])
const summary = ref({ totalValue: 0, change: 0 })
const loading = ref(false)

function formatAmount(val) {
  if (!val && val !== 0) return '-'
  const num = Number(val)
  if (num >= 100000000) return '¥' + (num / 100000000).toFixed(1) + '亿'
  if (num >= 10000) return '¥' + (num / 10000).toFixed(1) + '万'
  return '¥' + num.toLocaleString()
}

function getStatusLabel(roi) {
  if (roi > 0) return '盈利'
  if (roi === 0) return '持平'
  return '亏损'
}

function getStatusType(roi) {
  if (roi > 0) return 'success'
  if (roi === 0) return 'default'
  return 'danger'
}

async function fetchData() {
  loading.value = true
  try {
    const [projectsRes, summaryRes] = await Promise.allSettled([
      queryInvestmentProjects({ limit: 20 }),
      getPortfolioSummary()
    ])

    if (projectsRes.status === 'fulfilled' && projectsRes.value?.success && projectsRes.value?.data) {
      investList.value = projectsRes.value.data.map(item => ({
        id: item.id,
        name: item.projectName,
        amount: formatAmount(item.investAmount),
        equity: item.equityPct ? item.equityPct + '%' : '-',
        roi: item.actualRoi || item.expectedRoi || 0,
        status: getStatusLabel(item.actualRoi || item.expectedRoi || 0),
        statusType: getStatusType(item.actualRoi || item.expectedRoi || 0)
      }))
    }

    if (summaryRes.status === 'fulfilled' && summaryRes.value?.success && summaryRes.value?.data) {
      summary.value = summaryRes.value.data
    }
  } catch (err) {
    console.error('Failed to fetch investment data:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss">
.portfolio-page {
  padding: 16px;
}
.summary-card {
  background: linear-gradient(135deg, #001529, #003366);
  border-radius: 12px;
  padding: 24px 20px;
  margin-bottom: 16px;
  text-align: center;
}
.summary-label {
  font-size: 14px;
  color: rgba(255,255,255,0.7);
}
.summary-value {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  display: block;
  margin: 8px 0;
}
.summary-change {
  font-size: 13px;
  color: #67c23a;
}
.list-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}
.list-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}
.invest-item {
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
}
.invest-item:last-child {
  border-bottom: none;
}
.item-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.item-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}
.item-metrics {
  display: flex;
  gap: 16px;
}
.metric {
  display: flex;
  flex-direction: column;
}
.metric-label {
  font-size: 12px;
  color: #999;
}
.metric-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  margin-top: 2px;
}
</style>
