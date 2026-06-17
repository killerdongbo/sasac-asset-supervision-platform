<template>
  <view class="salary-page">
    <!-- 月份选择 -->
    <view class="month-picker">
      <text class="month-label">{{ currentMonth }}</text>
      <uni-dateformat :date="new Date()" format="yyyy年MM月"></uni-dateformat>
    </view>

    <!-- 工资卡片 -->
    <view class="salary-card">
      <view class="card-header">
        <text class="card-title">工资明细</text>
        <text class="card-period">2025年06月</text>
      </view>

      <view class="salary-item">
        <text class="item-label">基本工资</text>
        <text class="item-value">¥8,500.00</text>
      </view>
      <view class="salary-item">
        <text class="item-label">岗位津贴</text>
        <text class="item-value">¥2,000.00</text>
      </view>
      <view class="salary-item">
        <text class="item-label">绩效奖金</text>
        <text class="item-value">¥3,500.00</text>
      </view>
      <view class="salary-item">
        <text class="item-label">加班补贴</text>
        <text class="item-value">¥800.00</text>
      </view>

      <view class="divider"></view>

      <view class="salary-item deduction">
        <text class="item-label">养老保险</text>
        <text class="item-value">-¥850.00</text>
      </view>
      <view class="salary-item deduction">
        <text class="item-label">医疗保险</text>
        <text class="item-value">-¥212.50</text>
      </view>
      <view class="salary-item deduction">
        <text class="item-label">住房公积金</text>
        <text class="item-value">-¥1,020.00</text>
      </view>
      <view class="salary-item deduction">
        <text class="item-label">个人所得税</text>
        <text class="item-value">-¥345.60</text>
      </view>

      <view class="divider"></view>

      <view class="salary-item total">
        <text class="item-label">实发金额</text>
        <text class="item-value total-value">¥12,371.90</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const currentMonth = ref('2025-06')
</script>

<style lang="scss">
.salary-page {
  padding: 16px;
}
.month-picker {
  text-align: center;
  margin-bottom: 16px;
}
.month-label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.salary-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}
.card-period {
  font-size: 13px;
  color: #999;
}
.salary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}
.item-label {
  font-size: 14px;
  color: #666;
}
.item-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.deduction .item-value {
  color: #f56c6c;
}
.divider {
  height: 1px;
  background: #f0f0f0;
  margin: 4px 0;
}
.total {
  margin-top: 8px;
}
.total-value {
  font-size: 20px;
  font-weight: 700;
  color: #67c23a;
}
</style>
