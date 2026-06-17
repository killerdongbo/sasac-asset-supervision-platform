import client from './client'

export interface PmProject {
  id: string
  tenantId: number
  orgId: number
  projectNo: string
  name: string
  projectType?: string
  category?: string
  budgetTotal?: number
  budgetApproved?: number
  startDate?: string
  plannedEndDate?: string
  actualEndDate?: string
  managerId?: number
  managerName?: string
  department?: string
  decisionItemId?: number
  status?: string
  description?: string
  remark?: string
}

export interface PmBudget {
  id: string
  tenantId: number
  projectId: number
  budgetYear: number
  category?: string
  plannedAmount?: number
  actualAmount?: number
}

export interface PmProgress {
  id: string
  tenantId: number
  projectId: number
  reportDate: string
  reporterId?: number
  reporterName?: string
  progressPct?: number
  completedWork?: string
  pendingWork?: string
  issues?: string
  nextPlan?: string
}

export interface PmAcceptance {
  id: string
  tenantId: number
  projectId: number
  acceptanceNo?: string
  acceptanceType?: string
  result?: string
  score?: number
  reviewOpinion?: string
  reviewerId?: number
  reviewerName?: string
  acceptanceDate?: string
}

export interface ProjectQuery {
  keyword?: string
  status?: string
  projectType?: string
  page?: number
  limit?: number
}

export interface ProjectCreateDTO {
  name: string
  projectType?: string
  category?: string
  budgetTotal?: number
  startDate?: string
  plannedEndDate?: string
  managerName?: string
  department?: string
  description?: string
  orgId: number
  tenantId: number
}

export interface ProgressRecordDTO {
  projectId: number
  tenantId: number
  reportDate: string
  progressPct?: number
  completedWork?: string
  pendingWork?: string
  issues?: string
  nextPlan?: string
}

export interface AcceptanceDTO {
  tenantId: number
  acceptanceNo?: string
  acceptanceType?: string
  result?: string
  score?: number
  reviewOpinion?: string
  reviewerName?: string
  acceptanceDate?: string
}

export function queryProjects(params: ProjectQuery) {
  return client.get('/projects', { params }) as Promise<{ data: PmProject[]; meta?: { total: number; page: number; limit: number } }>
}

export function getProject(id: string) {
  return client.get(`/projects/${id}`) as Promise<{ data: PmProject }>
}

export function createProject(data: ProjectCreateDTO) {
  return client.post('/projects', data) as Promise<{ data: PmProject }>
}

export function updateProject(id: string, data: Partial<PmProject>) {
  return client.put(`/projects/${id}`, data) as Promise<{ data: PmProject }>
}

export function deleteProject(id: string) {
  return client.delete(`/projects/${id}`) as Promise<{ data: null }>
}

export function recordProgress(projectId: string, data: ProgressRecordDTO) {
  return client.post(`/projects/${projectId}/progress`, data) as Promise<{ data: PmProgress }>
}

export function getProgressHistory(projectId: string, tenantId: number) {
  return client.get(`/projects/${projectId}/progress`, { params: { tenantId } }) as Promise<{ data: PmProgress[] }>
}

export function acceptProject(projectId: string, data: AcceptanceDTO) {
  return client.post(`/projects/${projectId}/acceptance`, data) as Promise<{ data: null }>
}

export function getAcceptance(projectId: string) {
  return client.get(`/projects/${projectId}/acceptance`) as Promise<{ data: PmAcceptance[] }>
}

export function getProjectBudgets(projectId: string) {
  return client.get(`/projects/${projectId}/budgets`) as Promise<{ data: PmBudget[] }>
}
