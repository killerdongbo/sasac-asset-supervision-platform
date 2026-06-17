import client from './client'

// ===== Registration =====

export interface PrRegistration {
  id: string
  tenantId: number
  orgId: number
  regNo: string
  regType: string
  enterpriseName: string
  propertyType?: string
  propertyOwner?: string
  equityPct?: number
  registeredCapital?: number
  paidCapital?: number
  registrationDate?: string
  certNo?: string
  certFileIds?: string
  status: string
}

export interface RegistrationDTO {
  tenantId: number
  orgId: number
  regType: string
  enterpriseName: string
  propertyType?: string
  propertyOwner?: string
  equityPct?: number
  registeredCapital?: number
  paidCapital?: number
  registrationDate?: string
  certNo?: string
  certFileIds?: string
}

export function queryRegistrations(params: {
  tenantId: number
  regType?: string
  status?: string
  keyword?: string
  page?: number
  limit?: number
}) {
  return client.get('/property/registrations', { params }) as Promise<{ data: PrRegistration[]; meta?: { total: number; page: number; limit: number } }>
}

export function getRegistration(id: string) {
  return client.get(`/property/registrations/${id}`) as Promise<{ data: PrRegistration }>
}

export function createRegistration(data: RegistrationDTO) {
  return client.post('/property/registrations', data) as Promise<{ data: PrRegistration }>
}

// ===== Change =====

export interface PrChange {
  id: string
  tenantId: number
  registrationId: number
  changeType: string
  beforeData?: string
  afterData?: string
  changeReason?: string
  approvalFileIds?: string
  effectiveDate?: string
}

export interface ChangeDTO {
  tenantId: number
  changeType: string
  beforeData?: string
  afterData?: string
  changeReason?: string
  approvalFileIds?: string
  effectiveDate?: string
}

export function recordChange(regId: string, data: ChangeDTO) {
  return client.post(`/property/registrations/${regId}/changes`, data) as Promise<{ data: PrChange }>
}

// ===== Assessment =====

export interface PrAssessment {
  id: string
  tenantId: number
  registrationId?: number
  assessNo: string
  assessPurpose?: string
  assessAgency?: string
  assessMethod?: string
  bookValue?: number
  assessedValue?: number
  deviationPct?: number
  assessReportIds?: string
  approvalStatus: string
  assessDate?: string
}

export interface AssessmentDTO {
  tenantId: number
  registrationId?: number
  assessNo: string
  assessPurpose?: string
  assessAgency?: string
  assessMethod?: string
  bookValue?: number
  assessedValue?: number
  assessReportIds?: string
  assessDate?: string
}

export function createAssessment(data: AssessmentDTO) {
  return client.post('/property/assessments', data) as Promise<{ data: PrAssessment }>
}

export function queryAssessments(params: {
  tenantId: number
  page?: number
  limit?: number
}) {
  return client.get('/property/assessments', { params }) as Promise<{ data: PrAssessment[]; meta?: { total: number; page: number; limit: number } }>
}

// ===== Transaction Monitor =====

export interface PrTransactionMonitor {
  id: string
  tenantId: number
  exchangeName?: string
  listingNo?: string
  projectName?: string
  listingPrice?: number
  bidStartDate?: string
  bidEndDate?: string
  transactionPrice?: number
  buyerName?: string
  priceDeviationPct?: number
  isAbnormal: boolean
}

export interface TransactionDTO {
  tenantId: number
  exchangeName?: string
  listingNo?: string
  projectName?: string
  listingPrice?: number
  bidStartDate?: string
  bidEndDate?: string
  transactionPrice?: number
  buyerName?: string
}

export function createTransaction(data: TransactionDTO) {
  return client.post('/property/transactions', data) as Promise<{ data: PrTransactionMonitor }>
}

export function queryTransactions(params: {
  tenantId: number
  isAbnormal?: boolean
  page?: number
  limit?: number
}) {
  return client.get('/property/transactions', { params }) as Promise<{ data: PrTransactionMonitor[]; meta?: { total: number; page: number; limit: number } }>
}

// ===== Property Tree =====

export interface PropertyTreeNode {
  orgId: number
  label: string
  children?: PropertyTreeChildNode[]
}

export interface PropertyTreeChildNode {
  id: string
  label: string
  regNo: string
  regType: string
  equityPct?: number
  isLeaf: boolean
}

export function buildPropertyTree(tenantId: number) {
  return client.get('/property/tree', { params: { tenantId } }) as Promise<{ data: PropertyTreeNode[] }>
}
