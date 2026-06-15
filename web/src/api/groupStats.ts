import client from './client'

export interface CategoryStat {
  name: string
  count: number
  value: number
}

export interface StatusStat {
  name: string
  count: number
}

export interface SubsidiaryStat {
  orgId: string
  orgName: string
  totalAssets: number
  totalOriginalValue: number
  totalCurrentValue: number
}

export interface GroupStatistics {
  orgId: string
  orgName: string
  totalAssets: number
  totalOriginalValue: number
  totalCurrentValue: number
  subsidiaryCount: number
  categoryDistribution: CategoryStat[]
  statusDistribution: StatusStat[]
  subsidiaries: SubsidiaryStat[]
}

export function getGroupStatistics(orgId: string) {
  return client.get(`/statistics/group/${orgId}`) as Promise<{ data: GroupStatistics }>
}
