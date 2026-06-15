import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, type LoginResponse } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<LoginResponse | null>(
    JSON.parse(localStorage.getItem('userInfo') || 'null')
  )
  const permissions = ref(new Set<string>(userInfo.value?.permissions || []))
  const roles = ref(new Set<string>(userInfo.value?.roles || []))

  const isLoggedIn = computed(() => !!token.value)

  function hasPermission(code: string): boolean {
    return roles.value.has('SYSTEM_ADMIN') || permissions.value.has(code)
  }

  function hasRole(roleCode: string): boolean {
    return roles.value.has(roleCode)
  }

  async function login(username: string, password: string) {
    const response = await loginApi({ username, password })
    const data = response.data
    token.value = data.token
    userInfo.value = data
    permissions.value = new Set(data.permissions || [])
    roles.value = new Set(data.roles || [])
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(data))
    localStorage.setItem('tenantId', String(data.tenantId))
    localStorage.setItem('orgId', String(data.orgId))
    router.push('/')
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    permissions.value = new Set()
    roles.value = new Set()
    ;['token', 'tenantId', 'orgId', 'userInfo'].forEach(k => localStorage.removeItem(k))
    router.push('/login')
  }

  return { token, userInfo, isLoggedIn, permissions, roles, hasPermission, hasRole, login, logout }
})
