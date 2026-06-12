import client from './client'

export function getTasks(params?: any) { return client.get('/inventory-tasks', { params }) }
export function getMyTasks() { return client.get('/inventory-tasks/my') }
export function createTask(data: any) { return client.post('/inventory-tasks', data) }
export function getTask(id: number) { return client.get(`/inventory-tasks/${id}`) }

export function getRecords(taskId: number) { return client.get(`/inventory-tasks/${taskId}/records`) }
export function createRecord(data: any) { return client.post('/inventory-records', data) }

export function getDiffs(taskId: number) { return client.get(`/inventory-tasks/${taskId}/diffs`) }
export function getAllDiffs(params?: any) { return client.get('/inventory-diffs', { params }) }
export function adjustDiff(id: number, data: any) { return client.put(`/inventory-diffs/${id}/adjust`, data) }
