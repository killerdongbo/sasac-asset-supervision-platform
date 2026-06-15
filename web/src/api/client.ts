import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const client = axios.create({ baseURL: 'http://localhost:8082/api', timeout: 30000 })

// Request interceptor: add Bearer token and X-Tenant-Id header
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  const tenantId = localStorage.getItem('tenantId')
  if (tenantId) config.headers['X-Tenant-Id'] = tenantId
  return config
})

// Response interceptor: unwrap ApiResponse, handle 401/403 -> redirect login
client.interceptors.response.use(
  (response) => {
    // Bypass unwrapping for blob downloads (responseType: 'blob')
    if (response.config.responseType === 'blob') {
      return response.data
    }
    const data = response.data
    if (!data.success) {
      ElMessage.error(data.error || '请求失败')
      return Promise.reject(new Error(data.error))
    }
    return data
  },
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      ;['token', 'tenantId', 'orgId', 'userInfo'].forEach(k => localStorage.removeItem(k))
      router.push('/login')
      const msg = error.response?.status === 401 ? '登录已过期，请重新登录' : '无访问权限，请重新登录'
      ElMessage.error(msg)
    } else {
      ElMessage.error(error.response?.data?.error || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default client
