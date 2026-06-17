import client from './client'

export interface InvestPlan {
  id: string
  tenantId: number
  orgId: number
  planYear: number
  planName?: string
  totalBudget?: number
  approvedBy?: string
  status?: string
}

export interface InvestProject {
  id: string
  tenantId: number
  orgId: number
  planId?: number
  projectNo: string
  projectName: string
  investType?: string
  industry?: string
  region?: string
  investAmount?: number
  equityPct?: number
  targetCompany?: string
  targetDescription?: string
  decisionItemId?: number
  approvalInstanceId?: number
  phase?: string
  expectedRoi?: number
  actualRoi?: number
  status?: string
}

export interface InvestDD {
  id: string
  tenantId: number
  projectId: number
  ddType?: string
  lawFirm?: string
  accountingFirm?: string
  reportSummary?: string
  riskFindings?: string
  attachmentIds?: string
  status?: string
}

export interface InvestDeal {
  id: string
  tenantId: number
  projectId: number
  dealDate?: string
  dealAmount?: number
  equityAcquired?: number
  paymentTerms?: string
  agreementNo?: string
  status?: string
}

export interface InvestPost {
  id: string
  tenantId: number
  projectId: number
  reportDate?: string
  revenue?: number
  netProfit?: number
  netAssets?: number
  debtRatio?: number
  majorEvents?: string
  riskLevel?: string
}

export interface InvestExit {
  id: string
  tenantId: number
  projectId: number
  exitDate?: string
  exitAmount?: number
  exitMethod?: string
  returnRate?: number
  profitLoss?: number
  approvalInstanceId?: number
  status?: string
}

export interface InvestEquityNode {
  id?: string
  tenantId?: number
  projectId?: number
  companyName: string
  parentId?: number
  equityPct?: number
  level?: number
  isActualController?: boolean
  children?: InvestEquityNode[]
}

export interface InvestProjectQuery {
  keyword?: string
  investType?: string
  status?: string
  phase?: string
  page?: number
  limit?: number
}

export interface InvestProjectCreateDTO {
  planId?: number
  projectName: string
  investType?: string
  industry?: string
  region?: string
  investAmount?: number
  equityPct?: number
  targetCompany?: string
  targetDescription?: string
  expectedRoi?: number
  orgId: number
  tenantId: number
}

export interface InvestPlanCreateDTO {
  planYear: number
  planName?: string
  totalBudget?: number
  orgId: number
  tenantId: number
}

export interface InvestDealDTO {
  projectId: number
  tenantId: number
  dealDate?: string
  dealAmount?: number
  equityAcquired?: number
  agreementNo?: string
}

export interface InvestPostDTO {
  projectId: number
  tenantId: number
  reportDate?: string
  revenue?: number
  netProfit?: number
  netAssets?: number
  debtRatio?: number
  majorEvents?: string
  riskLevel?: string
}

export interface InvestExitDTO {
  projectId: number
  tenantId: number
  exitDate?: string
  exitAmount?: number
  exitMethod?: string
}

// Plan APIs
export function createPlan(data: InvestPlanCreateDTO) {
  return client.post('/investment/plans', data) as Promise<{ data: InvestPlan }>
}

// Project APIs
export function createInvestmentProject(data: InvestProjectCreateDTO) {
  return client.post('/investment/projects', data) as Promise<{ data: InvestProject }>
}

export function queryInvestmentProjects(params: InvestProjectQuery) {
  return client.get('/investment/projects', { params }) as Promise<{ data: InvestProject[]; meta?: { total: number; page: number; limit: number } }>
}

export function getInvestmentProject(id: string) {
  return client.get(`/investment/projects/${id}`) as Promise<{ data: InvestProject }>
}

export function updateInvestmentProject(id: string, data: Partial<InvestProject>) {
  return client.put(`/investment/projects/${id}`, data) as Promise<{ data: InvestProject }>
}

export function deleteInvestmentProject(id: string) {
  return client.delete(`/investment/projects/${id}`) as Promise<{ data: null }>
}

// DD APIs
export function recordDD(projectId: string, data: Partial<InvestDD>) {
  return client.post(`/investment/projects/${projectId}/dd`, data) as Promise<{ data: InvestDD }>
}

export function getDDRecords(projectId: string, tenantId: number) {
  return client.get(`/investment/projects/${projectId}/dd`, { params: { tenantId } }) as Promise<{ data: InvestDD[] }>
}

// Deal APIs
export function recordDeal(projectId: string, data: InvestDealDTO) {
  return client.post(`/investment/projects/${projectId}/deals`, data) as Promise<{ data: InvestDeal }>
}

export function getDealRecords(projectId: string, tenantId: number) {
  return client.get(`/investment/projects/${projectId}/deals`, { params: { tenantId } }) as Promise<{ data: InvestDeal[] }>
}

// Post-investment APIs
export function recordPost(projectId: string, data: InvestPostDTO) {
  return client.post(`/investment/projects/${projectId}/post`, data) as Promise<{ data: InvestPost }>
}

export function getPostRecords(projectId: string, tenantId: number) {
  return client.get(`/investment/projects/${projectId}/post`, { params: { tenantId } }) as Promise<{ data: InvestPost[] }>
}

// Exit APIs
export function recordExit(projectId: string, data: InvestExitDTO) {
  return client.post(`/investment/projects/${projectId}/exit`, data) as Promise<{ data: InvestExit }>
}

export function getExitRecords(projectId: string, tenantId: number) {
  return client.get(`/investment/projects/${projectId}/exit`, { params: { tenantId } }) as Promise<{ data: InvestExit[] }>
}

// Equity tree API
export function getEquityTree(projectId: string, tenantId: number) {
  return client.get(`/investment/projects/${projectId}/equity-tree`, { params: { tenantId } }) as Promise<{ data: InvestEquityNode }>
}
