import request from './client'

export interface Role {
  id: string
  tenantId: number
  roleCode: string
  roleName: string
  roleType: string
  dataScope: string
  description: string
  status: number
}

export interface Permission {
  id: string
  parentId: number
  permCode: string
  permName: string
  permType: string
  path: string | null
  icon: string | null
  sortOrder: number
}

export function listRoles() {
  return request.get('/permissions/roles')
}

export function getRole(id: string) {
  return request.get(`/permissions/roles/${id}`)
}

export function createRole(data: Partial<Role>) {
  return request.post('/permissions/roles', data)
}

export function updateRole(id: string, data: Partial<Role>) {
  return request.put(`/permissions/roles/${id}`, data)
}

export function deleteRole(id: string) {
  return request.delete(`/permissions/roles/${id}`)
}

export function listPermissions() {
  return request.get('/permissions')
}

export function getRolePermissions(roleId: string) {
  return request.get(`/permissions/roles/${roleId}/permissions`)
}

export function assignRolePermissions(roleId: string, permIds: string[]) {
  return request.put(`/permissions/roles/${roleId}/permissions`, permIds)
}

export function getUserRoles(userId: string) {
  return request.get(`/permissions/users/${userId}/roles`)
}

export function assignUserRoles(userId: string, roleIds: string[]) {
  return request.put(`/permissions/users/${userId}/roles`, roleIds)
}

export function getUserPermissionCodes(userId: string) {
  return request.get(`/permissions/users/${userId}/codes`)
}
