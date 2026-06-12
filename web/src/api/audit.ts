import client from './client'

export function getOperationLogs(params?: any) { return client.get('/operation-logs', { params }) }
export function getAssetLifecycle(assetId: number) { return client.get(`/assets/${assetId}/lifecycle`) }
