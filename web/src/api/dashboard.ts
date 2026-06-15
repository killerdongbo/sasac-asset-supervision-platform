import client from './client'

export interface OverviewData {
  totalAssets: number
  totalOriginalValue: number
  totalCurrentValue: number
  avgDepreciationRate: number
  categoryDistribution: Array<{ name: string; value: number }>
  statusDistribution: Array<{ name: string; value: number }>
}

export interface TopOrg {
  id: number
  name: string
  totalAssets: number
  totalOriginalValue: number
}

export interface TrendItem {
  month: string
  count: number
}

export function getOverview() {
  return client.get('/dashboard/overview') as Promise<{ data: OverviewData }>
}

export function getTopOrgs(limit = 10) {
  return client.get('/dashboard/top-orgs', { params: { limit } }) as Promise<{ data: TopOrg[] }>
}

export function getMonthlyTrend(months = 12) {
  return client.get('/dashboard/trend', { params: { months } }) as Promise<{ data: TrendItem[] }>
}

export function getMonthNewCount() {
  return client.get('/dashboard/month-new') as Promise<{ data: number }>
}
