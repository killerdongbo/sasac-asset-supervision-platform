import client from './client'

export function getStockIns(params?: any) { return client.get('/stock-ins', { params }) }
export function createStockIn(data: any) { return client.post('/stock-ins', data) }

export function getAssignments(params?: any) { return client.get('/assignments', { params }) }
export function createAssignment(data: any) { return client.post('/assignments', data) }

export function getTransfers(params?: any) { return client.get('/transfers', { params }) }
export function createTransfer(data: any) { return client.post('/transfers', data) }

export function getDisposals(params?: any) { return client.get('/disposals', { params }) }
export function createDisposal(data: any) { return client.post('/disposals', data) }
