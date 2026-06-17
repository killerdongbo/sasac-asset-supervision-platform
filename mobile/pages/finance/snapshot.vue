<template>
  <view class="finance-snapshot">
    <!-- 核心指标卡片 -->
    <view class="indicators">
      <view class="indicator-card">
        <text class="indicator-label">资产负债率</text>
        <text class="indicator-value" style="color: #409eff;">58.3%</text>
        <text class="indicator-trend">较上月 -1.2%</text>
      </view>
      <view class="indicator-card">
        <text class="indicator-label">净资产收益率(ROE)</text>
        <text class="indicator-value" style="color: #67c23a;">7.8%</text>
        <text class="indicator-trend">较上月 +0.5%</text>
      </view>
      <view class="indicator-card">
        <text class="indicator-label">经营活动现金流</text>
        <text class="indicator-value" style="color: #e6a23c;">¥1.2亿</text>
        <text class="indicator-trend">较上月 +8.3%</text>
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
const financeData = [
  { name: '营业收入', current: '45,832', prev: '41,256', change: 11.1 },
  { name: '净利润', current: '3,568', prev: '3,215', change: 11.0 },
  { name: '总资产', current: '286,450', prev: '278,900', change: 2.7 },
  { name: '净资产', current: '119,380', prev: '115,420', change: 3.4 }
]
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
