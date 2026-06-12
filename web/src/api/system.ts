import client from './client'

export function getUsers(params?: any) { return client.get('/users', { params }) }
export function createUser(data: any) { return client.post('/users', data) }
export function updateUser(id: number, data: any) { return client.put(`/users/${id}`, data) }
export function deleteUser(id: number) { return client.delete(`/users/${id}`) }

export function getRoles() { return client.get('/roles') }
export function createRole(data: any) { return client.post('/roles', data) }

export function getOrganizations() { return client.get('/organizations') }
export function createOrganization(data: any) { return client.post('/organizations', data) }
export function updateOrganization(id: number, data: any) { return client.put(`/organizations/${id}`, data) }
export function deleteOrganization(id: number) { return client.delete(`/organizations/${id}`) }

export function getDicts(type?: string) { return client.get('/dicts', { params: { type } }) }
export function saveDict(data: any) { return client.post('/dicts', data) }
