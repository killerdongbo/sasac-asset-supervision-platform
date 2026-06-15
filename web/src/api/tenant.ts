import request from './client'

export interface Tenant {
  id: string
  tenantCode: string
  tenantName: string
  contactPerson: string
  contactPhone: string
  contactEmail: string
  status: number
  expireTime: string | null
  maxUsers: number
  maxAssets: number
  edition: string
  logoUrl: string | null
  domain: string | null
  remark: string | null
  createdAt: string
}

export interface TenantConfig {
  id: string
  tenantId: number
  configKey: string
  configValue: string
  description: string
}

export interface TenantUsage {
  tenantId: number
  userCount: number
  assetCount: number
  storageUsedMb: number
  lastLoginTime: string | null
}

export function listTenants(params: { keyword?: string; status?: number; page?: number; size?: number }) {
  return request.get('/tenants', { params })
}

export function listAllTenants() {
  return request.get('/tenants/all')
}

export function getTenant(id: string) {
  return request.get(`/tenants/${id}`)
}

export function createTenant(data: Partial<Tenant>) {
  return request.post('/tenants', data)
}

export function updateTenant(id: string, data: Partial<Tenant>) {
  return request.put(`/tenants/${id}`, data)
}

export function toggleTenantStatus(id: string, status: number) {
  return request.put(`/tenants/${id}/status`, { status })
}

export function getTenantConfigs(id: string) {
  return request.get(`/tenants/${id}/configs`)
}

export function saveTenantConfig(id: string, key: string, value: string, description?: string) {
  return request.post(`/tenants/${id}/configs`, { key, value, description })
}

export function getTenantUsage(id: string) {
  return request.get(`/tenants/${id}/usage`)
}

export function refreshTenantUsage(id: string) {
  return request.post(`/tenants/${id}/usage/refresh`)
}
