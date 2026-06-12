import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, type LoginResponse } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<LoginResponse | null>(
    JSON.parse(localStorage.getItem('userInfo') || 'null')
  )

  const isLoggedIn = computed(() => !!token.value)

  async function login(username: string, password: string) {
    const response = await loginApi({ username, password })
    const data = response.data
    token.value = data.token
    userInfo.value = data
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(data))
    localStorage.setItem('tenantId', String(data.tenantId))
    localStorage.setItem('orgId', String(data.orgId))
    router.push('/')
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.clear()
    router.push('/login')
  }

  return { token, userInfo, isLoggedIn, login, logout }
})
