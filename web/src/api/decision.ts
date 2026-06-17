import client from './client'

// ===== Decision Item =====

export interface DecisionItem {
  id: string
  tenantId: number
  orgId: number
  itemNo: string
  itemType: string
  title: string
  description?: string
  amount?: number
  proposerId?: number
  proposerName?: string
  department?: string
  urgency?: string
  approvalInstanceId?: number
  status?: string
}

export interface DecisionItemQuery {
  tenantId: number
  itemType?: string
  status?: string
  page?: number
  limit?: number
}

export function submitDecisionItem(data: {
  tenantId: number
  orgId: number
  itemType: string
  title: string
  description?: string
  amount?: number
  proposerName?: string
  department?: string
}) {
  return client.post('/decision/items', data) as Promise<{ data: DecisionItem }>
}

export function queryDecisionItems(params: DecisionItemQuery) {
  return client.get('/decision/items', { params }) as Promise<{
    data: DecisionItem[]
    meta?: { total: number; page: number; limit: number }
  }>
}

// ===== Decision Meeting =====

export interface DecisionMeeting {
  id: string
  tenantId: number
  orgId: number
  meetingNo: string
  meetingType?: string
  title: string
  agenda?: string
  scheduledAt: string
  location?: string
  host?: string
  attendees?: string
  minutes?: string
  status?: string
}

export function createDecisionMeeting(data: {
  tenantId: number
  orgId: number
  title: string
  agenda?: string
  scheduledAt: string
  location?: string
  host?: string
  attendees?: string
}) {
  return client.post('/decision/meetings', data) as Promise<{ data: DecisionMeeting }>
}

// ===== Decision Resolution =====

export interface DecisionResolution {
  id: string
  tenantId: number
  meetingId: number
  itemId?: number
  resolutionNo: string
  title: string
  content: string
  voteResult?: string
  status?: string
}

export function createDecisionResolution(meetingId: number, data: {
  title: string
  content: string
  voteResult?: string
  itemId?: number
}) {
  return client.post(`/decision/meetings/${meetingId}/resolutions`, data) as Promise<{ data: DecisionResolution }>
}

// ===== Decision Supervision =====

export interface DecisionSupervision {
  id: string
  tenantId: number
  resolutionId: number
  taskTitle: string
  description?: string
  assigneeId?: number
  assigneeName?: string
  deadline?: string
  completedAt?: string
  progressNote?: string
  status: string
}

export function createSupervision(resolutionId: number, data: {
  tenantId: number
  taskTitle: string
  assigneeId?: number
  assigneeName?: string
  deadline?: string
}) {
  return client.post(`/decision/resolutions/${resolutionId}/supervisions`, data) as Promise<{ data: DecisionSupervision }>
}

export function getPendingSupervisions(tenantId: number) {
  return client.get('/decision/supervisions/pending', { params: { tenantId } }) as Promise<{ data: DecisionSupervision[] }>
}
