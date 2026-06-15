import client from './client'

export function getOperationLogs(params?: any) { return client.get('/audit/logs', { params }) }
export function getAssetLifecycle(assetId: number) { return client.get(`/audit/asset-lifecycle/${assetId}`) }
