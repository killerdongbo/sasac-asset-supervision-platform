import client from './client'

export interface Report {
  id: string
  reportType: string
  period: string
  orgId: number
  tenantId: number
  submitStatus: string
  version: number
  summaryData?: Record<string, unknown>
  createdAt: string
  updatedAt: string
}

export function generateReport(reportType: string, orgId: number, period: string, tenantId: number) {
  return client.post('/reports/generate', null, { params: { reportType, orgId, period, tenantId } }) as Promise<{ data: Report }>
}

export function getReport(id: string) {
  return client.get(`/reports/${id}`) as Promise<{ data: Report }>
}

export function listReports(orgId: number) {
  return client.get('/reports/list', { params: { orgId } }) as Promise<{ data: Report[] }>
}

export function submitReport(id: string, operatorId: number) {
  return client.post(`/reports/${id}/submit`, null, { params: { operatorId } }) as Promise<{ data: Report }>
}

export function reviewReport(id: string, approved: boolean, reviewerId: number, remark?: string) {
  return client.post(`/reports/${id}/review`, null, { params: { approved, reviewerId, remark } }) as Promise<{ data: Report }>
}
