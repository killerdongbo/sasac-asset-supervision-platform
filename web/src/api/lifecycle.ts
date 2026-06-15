import client from './client'

export interface LifecycleEvent {
  id: string
  assetId: string
  eventType: string
  eventTitle: string
  eventDetail: string | null
  sourceTable: string | null
  sourceId: string | null
  operatorId: string | null
  operatorName: string | null
  eventTime: string
  createdAt: string
}

export interface LifecycleSummary {
  totalEvents: number
  firstEventTime: string | null
  lastEventTime: string | null
  daysInService: number
  transferCount: number
  maintenanceCount: number
  inspectionCount: number
}

export interface LifecycleQuery {
  page?: number
  size?: number
  eventType?: string
  ascending?: boolean
}

export function getAssetLifecycle(assetId: string, params: LifecycleQuery = {}) {
  return client.get(`/assets/${assetId}/lifecycle`, { params }) as Promise<{
    data: { records: LifecycleEvent[]; total: number; current: number; size: number }
  }>
}

export function getAssetLifecycleSummary(assetId: string) {
  return client.get(`/assets/${assetId}/lifecycle/summary`) as Promise<{
    data: LifecycleSummary
  }>
}
