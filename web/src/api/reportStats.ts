import client from './client'

export interface OrgAssetSummary {
  orgId: string
  orgName: string
  assetCount: number
  originalValue: number
  currentValue: number
  accumulatedDepreciation: number
}

export interface CategoryStatItem {
  category: string
  count: number
  originalValue: number
  currentValue: number
  depreciation: number
}

export interface DisposalStatItem {
  disposalType: string
  count: number
  originalValue: number
}

export interface ReportQueryParams {
  orgId?: string
  category?: string
  startDate?: string
  endDate?: string
}

export function getAssetSummary(params?: ReportQueryParams) {
  return client.get('/report-stats/asset-summary', { params }) as Promise<{ data: OrgAssetSummary[] }>
}

export function getCategoryStats(params?: ReportQueryParams) {
  return client.get('/report-stats/category-stats', { params }) as Promise<{ data: CategoryStatItem[] }>
}

export function getDisposalStats(params?: ReportQueryParams) {
  return client.get('/report-stats/disposal-stats', { params }) as Promise<{ data: DisposalStatItem[] }>
}

export function getDepreciationAnalysis(params?: ReportQueryParams) {
  return client.get('/report-stats/depreciation-analysis', { params }) as Promise<{ data: OrgAssetSummary[] }>
}

export function getIdleStats(params?: { orgId?: string }) {
  return client.get('/report-stats/idle-stats', { params }) as Promise<{ data: OrgAssetSummary[] }>
}

export function getRentalStats(params?: { orgId?: string }) {
  return client.get('/report-stats/rental-stats', { params }) as Promise<{ data: OrgAssetSummary[] }>
}
