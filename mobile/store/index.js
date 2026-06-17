import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: uni.getStorageSync('token') || '',
    userInfo: uni.getStorageSync('userInfo') ? JSON.parse(uni.getStorageSync('userInfo')) : null,
    tenantId: uni.getStorageSync('tenantId') || '',
    orgId: uni.getStorageSync('orgId') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async login(username, password) {
      const res = await uni.request({
        url: 'http://localhost:8082/api/auth/login',
        method: 'POST',
        data: { username, password }
      })
      const data = res.data
      if (data.success) {
        this.token = data.data.token
        this.userInfo = data.data.userInfo
        this.tenantId = String(data.data.tenantId)
        this.orgId = String(data.data.orgId)
        uni.setStorageSync('token', this.token)
        uni.setStorageSync('userInfo', JSON.stringify(this.userInfo))
        uni.setStorageSync('tenantId', this.tenantId)
        uni.setStorageSync('orgId', this.orgId)
      }
      return data
    },
    logout() {
      this.token = ''
      this.userInfo = null
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.removeStorageSync('tenantId')
      uni.removeStorageSync('orgId')
    }
  }
})
