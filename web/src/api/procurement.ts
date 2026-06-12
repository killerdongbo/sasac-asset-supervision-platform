import client from './client'

export function getPurchaseRequests(params?: any) { return client.get('/purchase-requests', { params }) }
export function createPurchaseRequest(data: any) { return client.post('/purchase-requests', data) }
export function getPurchaseAcceptances(params?: any) { return client.get('/purchase-acceptances', { params }) }
export function acceptPurchase(requestId: number, data: any) { return client.post(`/purchase-requests/${requestId}/accept`, data) }
export function convertToAsset(requestId: number) { return client.post(`/purchase-requests/${requestId}/convert`) }
