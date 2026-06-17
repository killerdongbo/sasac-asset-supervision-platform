<template>
  <view class="mine-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <image class="avatar" :src="userInfo?.avatar || '/static/avatar.png'" mode="aspectFill"></image>
      <view class="user-detail">
        <text class="user-name">{{ userInfo?.realName || userInfo?.username || '用户' }}</text>
        <text class="user-org">{{ userInfo?.orgName || '湛江市国资委' }}</text>
      </view>
    </view>

    <!-- 信息列表 -->
    <view class="info-section">
      <view class="info-item">
        <text class="info-label">用户名</text>
        <text class="info-value">{{ userInfo?.username || '-' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">所属组织</text>
        <text class="info-value">{{ userInfo?.orgName || '-' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">角色</text>
        <text class="info-value">{{ userInfo?.roleName || userInfo?.roleCode || '-' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">租户ID</text>
        <text class="info-value">{{ authStore.tenantId || '-' }}</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <button class="logout-btn" @click="handleLogout">退出登录</button>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/store/index'

const authStore = useAuthStore()
const userInfo = computed(() => authStore.userInfo)

function handleLogout() {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出当前账号吗？',
    success: (res) => {
      if (res.confirm) {
        authStore.logout()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}
</script>

<style lang="scss">
.mine-page {
  padding: 16px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(135deg, #001529, #003366);
  border-radius: 12px;
  padding: 24px 20px;
  margin-bottom: 16px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: #ddd;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
}

.user-org {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.info-section {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #999;
}

.info-value {
  font-size: 14px;
  color: #333;
}

.logout-btn {
  width: 100%;
  height: 44px;
  line-height: 44px;
  background: #fff;
  color: #f56c6c;
  font-size: 16px;
  border-radius: 22px;
  border: 1px solid #f56c6c;
  text-align: center;
}
</style>
