<template>
  <view class="portfolio-page">
    <!-- 总览卡片 -->
    <view class="summary-card">
      <text class="summary-label">投资组合总市值</text>
      <text class="summary-value">¥12.8亿</text>
      <text class="summary-change">较上月 +5.2%</text>
    </view>

    <!-- 列表 -->
    <view class="list-section">
      <view class="list-title">投资项目</view>
      <view class="invest-item" v-for="item in investList" :key="item.id">
        <view class="item-top">
          <text class="item-name">{{ item.name }}</text>
          <uni-tag :text="item.status" :type="item.status === '盈利' ? 'success' : item.status === '持平' ? 'default' : 'danger'" size="small"></uni-tag>
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
const investList = [
  { id: 1, name: '湛江港务集团股权投资', amount: '¥3.5亿', equity: '15%', roi: 8.5, status: '盈利' },
  { id: 2, name: '新能源产业发展基金', amount: '¥2.0亿', equity: '20%', roi: 12.3, status: '盈利' },
  { id: 3, name: '智慧城市科技公司', amount: '¥1.5亿', equity: '25%', roi: -2.1, status: '亏损' },
  { id: 4, name: '海港经济区开发项目', amount: '¥3.0亿', equity: '10%', roi: 5.8, status: '持平' },
  { id: 5, name: '冷链物流基地建设', amount: '¥2.8亿', equity: '30%', roi: 6.2, status: '盈利' }
]
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
