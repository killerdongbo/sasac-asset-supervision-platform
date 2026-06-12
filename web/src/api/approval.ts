import client from './client'

export function getPendingApprovals(params?: any) { return client.get('/approval-instances/pending', { params }) }
export function getMyRequests(submitterId: number) { return client.get('/approval-instances/my-requests', { params: { submitterId } }) }
export function getApprovalHistory(params?: any) { return client.get('/approval-instances/history', { params }) }
export function approve(id: number, data: any) { return client.put(`/approval-instances/${id}/approve`, data) }
export function reject(id: number, data: any) { return client.put(`/approval-instances/${id}/reject`, data) }

export function getDefs() { return client.get('/approval-defs') }
export function createDef(data: any) { return client.post('/approval-defs', data) }
export function updateDef(id: number, data: any) { return client.put(`/approval-defs/${id}`, data) }
export function addNode(defId: number, data: any) { return client.post(`/approval-defs/${defId}/nodes`, data) }
