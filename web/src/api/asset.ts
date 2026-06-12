import client from './client'

export interface Asset {
  id: string
  name: string
  assetCode: string
  category: string
  orgId: number
  tenantId: number
  originalValue: number
  currentValue: number
  accumulatedDepreciation: number
  depreciationMethod: string
  usefulLifeMonths: number
  useStatus: string
  location: string
  purchaseDate: string
  registrationDate: string
  custodian: string
  specification?: string
  remark?: string
}

export interface AssetQuery {
  orgId?: number
  category?: string
  keyword?: string
  useStatus?: string
  page?: number
  limit?: number
}

export interface AssetCreateDTO {
  name: string
  assetCode: string
  category: string
  orgId: number
  tenantId: number
  originalValue?: number
  depreciationMethod?: string
  usefulLifeMonths?: number
  useStatus?: string
  location?: string
  custodian?: string
  purchaseDate?: string
  remark?: string
}

export interface ImportResult {
  totalCount: number
  successCount: number
  failedList?: Array<{ row: number; error: string }>
}

export function queryAssets(params: AssetQuery) {
  return client.get('/assets', { params }) as Promise<{ data: Asset[]; meta?: { total: number; page: number; limit: number } }>
}

export function getAsset(id: string) {
  return client.get(`/assets/${id}`) as Promise<{ data: Asset }>
}

export function createAsset(data: AssetCreateDTO) {
  return client.post('/assets', data) as Promise<{ data: Asset }>
}

export function updateAsset(id: string, data: Partial<Asset>) {
  return client.put(`/assets/${id}`, data) as Promise<{ data: Asset }>
}

export function deleteAsset(id: string) {
  return client.delete(`/assets/${id}`) as Promise<{ data: null }>
}

export function importAssets(file: File, orgId: number, tenantId: number) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('orgId', String(orgId))
  formData.append('tenantId', String(tenantId))
  return client.post('/assets/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }) as Promise<{ data: ImportResult }>
}
