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
        <view class="alert-icon" :class="getSeverityIcon(alert.severity)">
          <text>{{ alert.severity === 'CRITICAL' ? '!!' : alert.severity === 'WARNING' ? '!' : 'i' }}</text>
        </view>
        <view class="alert-content">
          <view class="alert-top">
            <text class="alert-title">{{ alert.title }}</text>
            <uni-tag
              :text="getSeverityText(alert.severity)"
              :type="getSeverityType(alert.severity)"
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
import { ref, onMounted } from 'vue'
import { getAlerts, getAlertCounts } from '@/api/alert'

const criticalCount = ref(0)
const warningCount = ref(0)
const infoCount = ref(0)
const alertList = ref([])
const loading = ref(false)

function getSeverityText(severity) {
  const map = { CRITICAL: '严重', WARNING: '警告', INFO: '提示' }
  return map[severity] || severity
}

function getSeverityType(severity) {
  const map = { CRITICAL: 'error', WARNING: 'warning', INFO: 'default' }
  return map[severity] || 'default'
}

function getSeverityIcon(severity) {
  const map = { CRITICAL: 'critical', WARNING: 'warning', INFO: 'info' }
  return map[severity] || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const [alertsRes, countsRes] = await Promise.allSettled([
      getAlerts({ limit: 50 }),
      getAlertCounts()
    ])

    if (alertsRes.status === 'fulfilled' && alertsRes.value?.success && alertsRes.value?.data) {
      alertList.value = alertsRes.value.data.map(item => ({
        id: item.id,
        title: item.title || item.alertName || '预警',
        content: item.content || item.description || '',
        severity: item.severity || item.level || 'INFO',
        time: item.createdAt || item.alertTime || '-'
      }))
    }

    if (countsRes.status === 'fulfilled' && countsRes.value?.success && countsRes.value?.data) {
      const counts = countsRes.value.data
      criticalCount.value = counts.critical || 0
      warningCount.value = counts.warning || 0
      infoCount.value = counts.info || 0
    }
  } catch (err) {
    console.error('Failed to fetch alerts:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
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
