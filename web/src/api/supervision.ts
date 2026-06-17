import client from './client'

export function getAuditPlans(params?: { tenantId?: number; planYear?: number }) {
  return client.get('/supervision/audit-plans', { params })
}

export function getAuditPlan(id: number) {
  return client.get(`/supervision/audit-plans/${id}`)
}

export function createAuditPlan(data: any) {
  return client.post('/supervision/audit-plans', data)
}

export function updateAuditPlan(id: number, data: any) {
  return client.put(`/supervision/audit-plans/${id}`, data)
}

export function deleteAuditPlan(id: number) {
  return client.delete(`/supervision/audit-plans/${id}`)
}

export function getFindings(params?: { tenantId?: number; planId?: number; severity?: string }) {
  return client.get('/supervision/findings', { params })
}

export function recordFinding(data: any) {
  return client.post('/supervision/findings', data)
}

export function getRectifications(params?: { tenantId?: number; findingId?: number; status?: string }) {
  return client.get('/supervision/rectifications', { params })
}

export function assignRectification(findingId: number, data: any) {
  return client.post('/supervision/rectifications', data, { params: { findingId } })
}

export function verifyRectification(id: number, result: string) {
  return client.put(`/supervision/rectifications/${id}/verify`, null, { params: { result } })
}

export function checkOverdueRectifications(tenantId: number) {
  return client.get('/supervision/rectifications/overdue', { params: { tenantId } })
}

export function getCases(params?: { tenantId?: number; status?: string; violationType?: string }) {
  return client.get('/supervision/cases', { params })
}

export function getCase(id: number) {
  return client.get(`/supervision/cases/${id}`)
}

export function openCase(data: any) {
  return client.post('/supervision/cases', data)
}

export function investigateCase(id: number, result: string, assetLoss?: number) {
  return client.put(`/supervision/cases/${id}/investigate`, null, { params: { result, assetLoss } })
}

export function decidePunishment(id: number, decision: string) {
  return client.put(`/supervision/cases/${id}/decide`, null, { params: { decision } })
}
