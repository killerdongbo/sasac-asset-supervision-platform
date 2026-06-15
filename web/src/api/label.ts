import client from './client'

export interface LabelData {
  assetId: string
  assetCode: string
  assetName: string
  category: string
  orgName: string | null
  location: string | null
  custodian: string | null
  qrContent: string
  barcode: string
}

export function getAssetLabelData(assetId: string) {
  return client.get(`/assets/labels/${assetId}`) as Promise<{ data: LabelData }>
}

export function batchGetLabelData(assetIds: string[]) {
  return client.post('/assets/labels/batch', assetIds) as Promise<{ data: LabelData[] }>
}

export function markLabelsPrinted(assetIds: string[]) {
  return client.post('/assets/labels/mark-printed', assetIds) as Promise<{ data: null }>
}

export function listAssetsByLabelStatus(labelStatus?: string, page = 1, size = 20) {
  return client.get('/assets/labels', { params: { labelStatus, page, size } }) as Promise<{
    data: { records: any[]; total: number }
  }>
}
