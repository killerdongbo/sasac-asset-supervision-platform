import client from './client'

export function getAssetStats(params?: any) { return client.get('/statistics/assets', { params }) }
export function getDepreciationStats(params?: any) { return client.get('/statistics/depreciation', { params }) }
export function getInspectionStats(params?: any) { return client.get('/statistics/inspection', { params }) }
export function getInventoryStats(params?: any) { return client.get('/statistics/inventory', { params }) }
