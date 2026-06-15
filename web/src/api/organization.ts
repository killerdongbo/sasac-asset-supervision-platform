import request from './client'

export interface Organization {
  id?: string; parentId: number | null; name: string
  orgType: string; orgCode: string; tenantId?: number
}

export function getOrganizations() { return request.get('/organizations') }
export function getOrganization(id: string) { return request.get(`/organizations/${id}`) }
export function getChildren(id: string) { return request.get(`/organizations/${id}/children`) }
export function getTree(id: string) { return request.get(`/organizations/${id}/tree`) }
export function createOrganization(data: Organization) { return request.post('/organizations', data) }
export function updateOrganization(id: string, data: Organization) { return request.put(`/organizations/${id}`, data) }
export function deleteOrganization(id: string) { return request.delete(`/organizations/${id}`) }
