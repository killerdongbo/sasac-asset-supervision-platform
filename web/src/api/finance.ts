import client from './client'

// ===== FinReport =====

export interface FinReport {
  id: string
  tenantId: number
  orgId: number
  reportType: string
  reportYear: number
  reportPeriod: number
  reportData: string
  status: string
  submittedAt?: string
}

export interface ReportDTO {
  tenantId: number
  orgId: number
  reportType: string
  reportYear: number
  reportPeriod: number
  reportData: string
}

export function submitReport(data: ReportDTO) {
  return client.post('/finance/reports', data) as Promise<{ data: FinReport }>
}

export function queryReports(params: {
  tenantId: number
  reportType?: string
  reportYear?: number
  status?: string
  page?: number
  limit?: number
}) {
  return client.get('/finance/reports', { params }) as Promise<{
    data: FinReport[]
    meta?: { total: number; page: number; limit: number }
  }>
}

export function getReport(id: string) {
  return client.get(`/finance/reports/${id}`) as Promise<{ data: FinReport }>
}

// ===== FinIndicator =====

export interface FinIndicator {
  id: string
  tenantId: number
  orgId: number
  indicatorCode: string
  indicatorName: string
  indicatorValue: number
  thresholdWarn: number
  thresholdAlarm: number
  periodYear: number
  periodMonth: number
  status: string
}

export interface IndicatorDTO {
  tenantId: number
  orgId: number
  periodYear: number
  periodMonth: number
}

export function calculateIndicators(data: IndicatorDTO) {
  return client.post('/finance/indicators/calculate', data) as Promise<{ data: FinIndicator[] }>
}

export function queryIndicators(params: {
  tenantId: number
  indicatorCode?: string
  periodYear?: number
  page?: number
  limit?: number
}) {
  return client.get('/finance/indicators', { params }) as Promise<{
    data: FinIndicator[]
    meta?: { total: number; page: number; limit: number }
  }>
}

// ===== FinFundMonitor =====

export interface FinFundMonitor {
  id: string
  tenantId: number
  orgId: number
  transactionNo?: string
  transactionDate?: string
  amount: number
  payer?: string
  payee?: string
  purpose?: string
  isFlagged: boolean
  flagReason?: string
}

export interface FundMonitorDTO {
  tenantId: number
  orgId: number
  transactionNo?: string
  transactionDate: string
  amount: number
  payer?: string
  payee?: string
  purpose?: string
}

export function monitorFund(data: FundMonitorDTO) {
  return client.post('/finance/funds', data) as Promise<{ data: FinFundMonitor }>
}

export function queryFunds(params: {
  tenantId: number
  isFlagged?: boolean
  page?: number
  limit?: number
}) {
  return client.get('/finance/funds', { params }) as Promise<{
    data: FinFundMonitor[]
    meta?: { total: number; page: number; limit: number }
  }>
}

// ===== FinBudget =====

export interface FinBudget {
  id: string
  tenantId: number
  orgId: number
  budgetYear: number
  budgetType?: string
  plannedAmount: number
  actualAmount: number
  executedPct: number
}

export interface BudgetDTO {
  tenantId: number
  orgId: number
  budgetYear: number
  budgetType?: string
  plannedAmount: number
}

export function createBudget(data: BudgetDTO) {
  return client.post('/finance/budgets', data) as Promise<{ data: FinBudget }>
}

export function queryBudgets(params: {
  tenantId: number
  budgetYear?: number
  page?: number
  limit?: number
}) {
  return client.get('/finance/budgets', { params }) as Promise<{
    data: FinBudget[]
    meta?: { total: number; page: number; limit: number }
  }>
}

export function checkBudgetExecution(params: { tenantId: number; budgetYear: number }) {
  return client.post('/finance/budgets/check', null, { params }) as Promise<{ data: FinBudget[] }>
}
