import request from './client'

export interface User {
  id: string; username: string; realName: string; phone: string
  tenantId: number; orgId: number; roleCode: string
  status: number; createdAt: string
}

export function listUsers(params: { keyword?: string; status?: number; page?: number; size?: number }) {
  return request.get('/users', { params })
}
export function getUser(id: string) { return request.get(`/users/${id}`) }
export function createUser(data: Partial<User> & { password?: string }) { return request.post('/users', data) }
export function updateUser(id: string, data: Partial<User>) { return request.put(`/users/${id}`, data) }
export function resetPassword(id: string, password: string) { return request.put(`/users/${id}/reset-password`, { password }) }
export function toggleUserStatus(id: string, status: number) { return request.put(`/users/${id}/status`, { status }) }
export function assignUserRoles(id: string, roleIds: string[]) { return request.put(`/users/${id}/roles`, roleIds) }
