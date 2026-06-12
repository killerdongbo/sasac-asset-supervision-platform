import client from './client'

export function getRequests(params?: any) { return client.get('/maintenance-requests', { params }) }
export function createRequest(data: any) { return client.post('/maintenance-requests', data) }
export function getRequest(id: number) { return client.get(`/maintenance-requests/${id}`) }
export function completeMaintenance(requestId: number, data: any) { return client.post(`/maintenance-requests/${requestId}/complete`, data) }
export function getRecords(requestId: number) { return client.get(`/maintenance-requests/${requestId}/records`) }
