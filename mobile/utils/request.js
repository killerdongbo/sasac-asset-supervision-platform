import { useAuthStore } from '@/store/index'

const BASE_URL = 'http://localhost:8082/api'

const request = (options) => {
  return new Promise((resolve, reject) => {
    const authStore = useAuthStore()
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Authorization': authStore.token ? `Bearer ${authStore.token}` : '',
        'X-Tenant-Id': authStore.tenantId || '',
        'Content-Type': 'application/json'
      },
      timeout: 30000,
      success: (res) => {
        if (res.statusCode === 401 || res.statusCode === 403) {
          authStore.logout()
          uni.reLaunch({ url: '/pages/login/login' })
          return
        }
        const data = res.data
        if (!data.success) {
          uni.showToast({ title: data.error || '请求失败', icon: 'none' })
          reject(new Error(data.error))
          return
        }
        resolve(data)
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

export default {
  get: (url, params) => request({ url, method: 'GET', data: params }),
  post: (url, data) => request({ url, method: 'POST', data }),
  put: (url, data) => request({ url, method: 'PUT', data }),
  delete: (url) => request({ url, method: 'DELETE' })
}
