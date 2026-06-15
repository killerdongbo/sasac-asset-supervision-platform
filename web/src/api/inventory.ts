import client from './client'

export function getMyTasks(params?: any) { return client.get('/inventory-tasks/my', { params }) }
export function createTask(data: any) { return client.post('/inventory-tasks', data) }

export function getRecords(taskId: number) { return client.get(`/inventory-tasks/${taskId}/records`) }
export function createRecord(data: any) { return client.post('/inventory-records', data) }

export function getDiffs(taskId: number) { return client.get(`/inventory-tasks/${taskId}/diffs`) }
// TODO: backend needs endpoint for getAllDiffs and adjustDiff
