import client from './client'

// ===== PerfIndicatorDef =====

export interface PerfIndicatorDef {
  id: string
  tenantId: number
  orgId: number
  indicatorCode: string
  indicatorName: string
  category?: string
  weight: number
  targetValue: number
  actualValue?: number
  score?: number
  cycleYear: number
}

export interface IndicatorDefDTO {
  tenantId: number
  orgId: number
  indicatorCode: string
  indicatorName: string
  category?: string
  weight: number
  targetValue: number
  actualValue?: number
  cycleYear: number
}

export function defineIndicators(data: IndicatorDefDTO[]) {
  return client.post('/performance/indicators/batch', data) as Promise<{ data: PerfIndicatorDef[] }>
}

export function recordActual(id: string, actualValue: number) {
  return client.put(`/performance/indicators/${id}/actual`, { actualValue }) as Promise<{ data: PerfIndicatorDef }>
}

export function calculateScores(params: { tenantId: number; orgId: number; year: number }) {
  return client.post('/performance/indicators/calculate', null, { params }) as Promise<{ data: PerfIndicatorDef[] }>
}

export function getIndicatorSummary(params: { tenantId: number; orgId: number; year: number }) {
  return client.get('/performance/indicators', { params }) as Promise<{ data: PerfIndicatorDef[] }>
}

// ===== PerfSalaryBudget =====

export interface PerfSalaryBudget {
  id: string
  tenantId: number
  orgId: number
  budgetYear: number
  totalBudget: number
  approvedBudget: number
  actualPaid: number
  adjustmentReason?: string
  status: string
}

export interface SalaryBudgetDTO {
  id?: string
  tenantId: number
  orgId: number
  budgetYear: number
  totalBudget: number
  approvedBudget?: number
  actualPaid?: number
  adjustmentReason?: string
  status?: string
}

export function manageSalaryBudget(data: SalaryBudgetDTO) {
  return client.post('/performance/salary-budgets', data) as Promise<{ data: PerfSalaryBudget }>
}

export function adjustBudget(id: string, adjustment: number, reason: string) {
  return client.post(`/performance/salary-budgets/${id}/adjust`, { adjustment, reason }) as Promise<{ data: PerfSalaryBudget }>
}

export function querySalaryBudgets(params: {
  tenantId: number
  budgetYear?: number
  page?: number
  limit?: number
}) {
  return client.get('/performance/salary-budgets', { params }) as Promise<{
    data: PerfSalaryBudget[]
    meta?: { total: number; page: number; limit: number }
  }>
}

export function getSalaryBudget(id: string) {
  return client.get(`/performance/salary-budgets/${id}`) as Promise<{ data: PerfSalaryBudget }>
}

export function deleteSalaryBudget(id: string) {
  return client.delete(`/performance/salary-budgets/${id}`) as Promise<{ data: null }>
}

// ===== PerfIncentive =====

export interface PerfIncentive {
  id: string
  tenantId: number
  incentiveType: string
  employeeId: number
  grantDate: string
  vestingPeriod?: number
  amount: number
  status: string
}

export interface IncentiveDTO {
  tenantId: number
  incentiveType: string
  employeeId: number
  grantDate: string
  vestingPeriod?: number
  amount: number
  status?: string
}

export function createIncentive(data: IncentiveDTO) {
  return client.post('/performance/incentives', data) as Promise<{ data: PerfIncentive }>
}

export function queryIncentives(params: {
  tenantId: number
  incentiveType?: string
  page?: number
  limit?: number
}) {
  return client.get('/performance/incentives', { params }) as Promise<{
    data: PerfIncentive[]
    meta?: { total: number; page: number; limit: number }
  }>
}

export function getIncentive(id: string) {
  return client.get(`/performance/incentives/${id}`) as Promise<{ data: PerfIncentive }>
}

export function deleteIncentive(id: string) {
  return client.delete(`/performance/incentives/${id}`) as Promise<{ data: null }>
}
