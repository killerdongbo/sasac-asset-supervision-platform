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

// Response interceptor: unwrap ApiResponse, handle 401 -> redirect login
client.interceptors.response.use(
  (response) => {
    const data = response.data
    if (!data.success) {
      ElMessage.error(data.error || '请求失败')
      return Promise.reject(new Error(data.error))
    }
    return data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.response?.data?.error || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default client
