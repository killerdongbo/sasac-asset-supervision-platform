import request from './client'

export interface ScreenData {
  overview: {
    totalAssets: number
    totalOriginalValue: number
    totalCurrentValue: number
    monthNewAssets: number
    totalOrgs: number
    totalUsers: number
    alertCount: number
  }
  regionDistribution: { name: string; value: number; amount: number }[]
  statusDistribution: { name: string; value: number }[]
  monthlyTrend: { month: string; count: number; value: number }[]
  topOrgs: { orgName: string; assetCount: number; totalValue: number }[]
  recentAlerts: { title: string; level: string; time: string }[]
}

export function getScreenData() {
  return request.get('/screen/data')
}
