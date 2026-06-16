import client from './client'

// ── TypeScript interfaces ──

export interface CirculationRecord {
  id: number
  assetId: number
  /** Human-readable asset code (e.g. "IT-2025-008"), consistent with the asset ledger */
  assetCode: string
  /** Asset name (e.g. "台式计算机(100台)") */
  assetName: string
  changeType: string
  fromOrgId?: number
  toOrgId?: number
  fromCustodian?: string
  toCustodian?: string
  changeValue?: number
  changeDate?: string
  reason?: string
  remark?: string
  operatorId?: string
  createdAt: string
}

export interface DisposalRecord {
  id: number
  assetId: number
  /** Human-readable asset code, consistent with the asset ledger */
  assetCode: string
  /** Asset name */
  assetName: string
  disposalType: string
  disposalDate?: string
  bookValue?: number
  disposalValue?: number
  gainLoss?: number
  reason?: string
  createdAt: string
}

// ── Stock-In ──

export function getStockIns(params?: { assetId?: number }) {
  return client.get<CirculationRecord[]>('/stock-ins', { params })
}
export function createStockIn(data: Record<string, unknown>) {
  return client.post<CirculationRecord>('/stock-ins', data)
}

// ── Assignment ──

export function getAssignments(params?: { assetId?: number }) {
  return client.get<CirculationRecord[]>('/assignments', { params })
}
export function createAssignment(data: Record<string, unknown>) {
  return client.post<CirculationRecord>('/assignments', data)
}

// ── Transfer ──

export function getTransfers(params?: { assetId?: number }) {
  return client.get<CirculationRecord[]>('/transfers', { params })
}
export function createTransfer(data: Record<string, unknown>) {
  return client.post<CirculationRecord>('/transfers', data)
}

// ── Disposal ──

export function getDisposals(params?: { assetId?: number }) {
  return client.get<DisposalRecord[]>('/disposals', { params })
}
export function createDisposal(data: Record<string, unknown>) {
  return client.post<DisposalRecord>('/disposals', data)
}
