import client from './client'

export function getRequests(params?: any) { return client.get('/maintenance-requests', { params }) }
export function createRequest(data: any) { return client.post('/maintenance-requests', data) }
export function completeMaintenance(requestId: number, data: any) { return client.post(`/maintenance-requests/${requestId}/complete`, data) }
export function createFromAnomaly(anomalyId: number) { return client.post(`/maintenance-requests/from-anomaly/${anomalyId}`) }
// TODO: backend needs endpoints for getRequest(id) and getRecords(requestId)
