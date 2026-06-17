<template>
  <view class="workbench">
    <!-- 用户信息栏 -->
    <view class="user-header">
      <view class="user-info">
        <image class="avatar" src="/static/avatar.png" mode="aspectFill"></image>
        <view class="user-text">
          <text class="user-name">{{ userName }}</text>
          <text class="user-org">{{ userOrg }}</text>
        </view>
      </view>
      <view class="header-right">
        <uni-badge :text="pendingCount" type="error" size="small" v-if="pendingCount > 0">
          <text class="iconfont icon-bell">🔔</text>
        </uni-badge>
      </view>
    </view>

    <!-- 快捷功能卡片网格 -->
    <view class="grid-section">
      <view class="grid-title">快捷功能</view>
      <view class="grid-container">
        <view class="grid-item" v-for="item in menuItems" :key="item.key" @click="navigateTo(item.route)">
          <view class="grid-icon" :style="{ backgroundColor: item.bgColor }">
            <text class="icon-emoji">{{ item.icon }}</text>
            <uni-badge :text="item.badge" type="error" size="small" v-if="item.badge && item.badge > 0"></uni-badge>
          </view>
          <text class="grid-label">{{ item.label }}</text>
        </view>
      </view>
    </view>

    <!-- 待办提醒 -->
    <view class="todo-section">
      <view class="section-header">
        <text class="section-title">待办提醒</text>
        <text class="section-more" @click="navigateTo('/pages/approval/list')">查看更多</text>
      </view>
      <view class="todo-list">
        <view class="todo-item" v-for="(todo, idx) in pendingList" :key="idx">
          <view class="todo-dot" :style="{ backgroundColor: todo.color }"></view>
          <text class="todo-text">{{ todo.title }}</text>
          <text class="todo-time">{{ todo.time }}</text>
        </view>
        <view class="empty-tip" v-if="pendingList.length === 0">暂无待办事项</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const userName = ref('管理员')
const userOrg = ref('湛江市国资委')
const pendingCount = ref(3)
const pendingList = ref([
  { title: '资产入库审批 - 服务器采购', time: '10:30', color: '#f56c6c' },
  { title: '项目进度填报 - 智慧园区', time: '09:15', color: '#e6a23c' },
  { title: '工资确认 - 2025年6月', time: '昨日', color: '#409eff' }
])

const menuItems = [
  { key: 'pending', label: '待审批', icon: '📋', route: '/pages/approval/list', bgColor: '#fef0f0', badge: 3 },
  { key: 'asset', label: '资产查询', icon: '🏢', route: '', bgColor: '#ecf5ff', badge: 0 },
  { key: 'salary', label: '工资条', icon: '💰', route: '/pages/hr/salary', bgColor: '#f0f9eb', badge: 0 },
  { key: 'project', label: '项目进度', icon: '📊', route: '/pages/project/progress', bgColor: '#fdf6ec', badge: 0 },
  { key: 'invest', label: '投资组合', icon: '📈', route: '/pages/invest/portfolio', bgColor: '#fef0f0', badge: 0 },
  { key: 'finance', label: '财务快报', icon: '📉', route: '/pages/finance/snapshot', bgColor: '#ecf5ff', badge: 0 },
  { key: 'alert', label: '预警消息', icon: '⚠️', route: '/pages/alerts/index', bgColor: '#fef0f0', badge: 2 },
  { key: 'doc', label: '制度文件', icon: '📄', route: '', bgColor: '#f0f9eb', badge: 0 }
]

function navigateTo(route) {
  if (route) {
    uni.navigateTo({ url: route })
  }
}

onMounted(() => {
  // TODO: fetch user info and pending tasks from API
})
</script>

<style lang="scss">
.workbench {
  padding: 16px;
}
.user-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #ddd;
}
.user-text {
  display: flex;
  flex-direction: column;
}
.user-name {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}
.user-org {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}
.grid-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}
.grid-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.grid-item {
  width: calc(25% - 9px);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 0;
}
.grid-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  position: relative;
}
.grid-label {
  font-size: 12px;
  color: #666;
}
.todo-section {
  margin-top: 24px;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.section-more {
  font-size: 13px;
  color: #409eff;
}
.todo-list {
  display: flex;
  flex-direction: column;
}
.todo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}
.todo-item:last-child {
  border-bottom: none;
}
.todo-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.todo-text {
  flex: 1;
  font-size: 14px;
  color: #333;
}
.todo-time {
  font-size: 12px;
  color: #999;
}
.empty-tip {
  text-align: center;
  color: #999;
  padding: 24px;
  font-size: 14px;
}
</style>
