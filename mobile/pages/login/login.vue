<template>
  <view class="login-page">
    <view class="login-header">
      <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
      <text class="app-title">湛江国资</text>
      <text class="app-subtitle">国资国企一体化数字平台</text>
    </view>

    <view class="login-form">
      <view class="input-group">
        <view class="input-item">
          <text class="input-icon">👤</text>
          <input
            class="input-field"
            v-model="username"
            type="text"
            placeholder="请输入用户名"
            placeholder-class="placeholder"
          />
        </view>
        <view class="input-item">
          <text class="input-icon">🔒</text>
          <input
            class="input-field"
            v-model="password"
            type="password"
            placeholder="请输入密码"
            placeholder-class="placeholder"
          />
        </view>
      </view>

      <button class="login-btn" :loading="loading" :disabled="loading" @click="handleLogin">
        {{ loading ? '登录中...' : '登 录' }}
      </button>

      <view class="login-error" v-if="errorMsg">
        <text>{{ errorMsg }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/store/index'

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

const authStore = useAuthStore()

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  errorMsg.value = ''
  loading.value = true
  try {
    const res = await authStore.login(username.value, password.value)
    if (res.success) {
      uni.showToast({ title: '登录成功', icon: 'success' })
      uni.reLaunch({ url: '/pages/index/index' })
    }
  } catch (err) {
    errorMsg.value = err.errorMsg || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #001529 0%, #003366 50%, #f5f5f5 50%);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
  padding-bottom: 40px;
}

.logo {
  width: 72px;
  height: 72px;
  border-radius: 16px;
  background: #fff;
  margin-bottom: 16px;
}

.app-title {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 6px;
}

.app-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.login-form {
  width: 85%;
  max-width: 360px;
}

.input-group {
  background: #fff;
  border-radius: 12px;
  padding: 4px 0;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.input-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.input-item:last-child {
  border-bottom: none;
}

.input-icon {
  font-size: 18px;
  margin-right: 12px;
}

.input-field {
  flex: 1;
  font-size: 15px;
  color: #333;
  height: 24px;
}

.placeholder {
  color: #ccc;
  font-size: 15px;
}

.login-btn {
  width: 100%;
  height: 48px;
  line-height: 48px;
  background: #001529;
  color: #fff;
  font-size: 17px;
  border-radius: 24px;
  text-align: center;
}

.login-btn[disabled] {
  opacity: 0.7;
}

.login-error {
  text-align: center;
  margin-top: 16px;
  color: #f56c6c;
  font-size: 14px;
}
</style>
