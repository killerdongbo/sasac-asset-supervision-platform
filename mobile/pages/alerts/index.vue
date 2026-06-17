<template>
  <view class="alert-page">
    <!-- 预警统计 -->
    <view class="alert-stats">
      <view class="stat-item critical">
        <text class="stat-count">{{ criticalCount }}</text>
        <text class="stat-label">严重</text>
      </view>
      <view class="stat-item warning">
        <text class="stat-count">{{ warningCount }}</text>
        <text class="stat-label">警告</text>
      </view>
      <view class="stat-item info">
        <text class="stat-count">{{ infoCount }}</text>
        <text class="stat-label">提示</text>
      </view>
    </view>

    <!-- 列表 -->
    <view class="alert-list">
      <view class="alert-item" v-for="alert in alertList" :key="alert.id">
        <view class="alert-icon" :class="alert.severity.toLowerCase()">
          <text>{{ alert.severity === 'CRITICAL' ? '!!' : alert.severity === 'WARNING' ? '!' : 'i' }}</text>
        </view>
        <view class="alert-content">
          <view class="alert-top">
            <text class="alert-title">{{ alert.title }}</text>
            <uni-tag
              :text="alert.severity === 'CRITICAL' ? '严重' : alert.severity === 'WARNING' ? '警告' : '提示'"
              :type="alert.severity === 'CRITICAL' ? 'error' : alert.severity === 'WARNING' ? 'warning' : 'default'"
              size="small"
            />
          </view>
          <text class="alert-desc">{{ alert.content }}</text>
          <text class="alert-time">{{ alert.time }}</text>
        </view>
      </view>
      <view class="empty-tip" v-if="alertList.length === 0">
        <text>暂无预警消息</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const criticalCount = ref(2)
const warningCount = ref(4)
const infoCount = ref(6)

const alertList = ref([
  { id: 1, title: '关键岗位空缺预警', content: '财务总监岗位已空缺超过30天', severity: 'CRITICAL', time: '2025-06-17 10:30' },
  { id: 2, title: '项目预算执行预警', content: '智慧园区项目预算执行率达92%', severity: 'CRITICAL', time: '2025-06-17 09:15' },
  { id: 3, title: '产权交易偏差预警', content: '某资产挂牌交易价格偏差18.5%', severity: 'WARNING', time: '2025-06-16 14:00' },
  { id: 4, title: '投资ROI预警', content: '智慧城市公司ROI低于预期32%', severity: 'WARNING', time: '2025-06-16 11:20' },
  { id: 5, title: '整改超期预警', content: '审计整改任务已超期15天', severity: 'WARNING', time: '2025-06-15 16:00' },
  { id: 6, title: '项目进度预警', content: '国资云平台项目超计划工期35天', severity: 'WARNING', time: '2025-06-15 14:30' }
])
</script>

<style lang="scss">
.alert-page {
  padding: 16px;
}
.alert-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.stat-item {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
}
.stat-count {
  font-size: 28px;
  font-weight: 700;
  display: block;
}
.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.stat-item.critical .stat-count { color: #f56c6c; }
.stat-item.warning .stat-count { color: #e6a23c; }
.stat-item.info .stat-count { color: #909399; }

.alert-list {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}
.alert-item {
  display: flex;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
}
.alert-item:last-child {
  border-bottom: none;
}
.alert-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
}
.alert-icon.critical { background: #f56c6c; }
.alert-icon.warning { background: #e6a23c; }
.alert-icon.info { background: #909399; }
.alert-content {
  flex: 1;
  min-width: 0;
}
.alert-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.alert-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
.alert-desc {
  font-size: 13px;
  color: #999;
  display: block;
  line-height: 1.4;
}
.alert-time {
  font-size: 12px;
  color: #ccc;
  display: block;
  margin-top: 4px;
}
.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
}
</style>
