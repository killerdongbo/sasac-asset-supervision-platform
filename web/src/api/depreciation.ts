import client from './client'

export function getDepreciationRecords(params?: any) { return client.get('/depreciation-records', { params }) }
export function runDepreciation(assetId: number) { return client.post(`/assets/${assetId}/depreciate`) }
export function confirmDepreciation(id: number) { return client.put(`/depreciation-records/${id}/confirm`) }
export function getDepreciationRules() { return client.get('/depreciation-rules') }
export function saveDepreciationRule(data: any) { return client.post('/depreciation-rules', data) }
