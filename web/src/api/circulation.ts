import client from './client'

// TODO: 流转管理功能的后端 Controller 尚未实现，以下 API 暂不可用
export function getStockIns(params?: any) { return client.get('/stock-ins', { params }) }
export function createStockIn(data: any) { return client.post('/stock-ins', data) }

export function getAssignments(params?: any) { return client.get('/assignments', { params }) }
export function createAssignment(data: any) { return client.post('/assignments', data) }

export function getTransfers(params?: any) { return client.get('/transfers', { params }) }
export function createTransfer(data: any) { return client.post('/transfers', data) }

export function getDisposals(params?: any) { return client.get('/disposals', { params }) }
export function createDisposal(data: any) { return client.post('/disposals', data) }
