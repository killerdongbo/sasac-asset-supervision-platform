import client from './client'

export function getTasks(params?: any) { return client.get('/inspection-tasks', { params }) }
export function getMyTasks() { return client.get('/inspection-tasks/my') }
export function createTask(data: any) { return client.post('/inspection-tasks', data) }
export function getTask(id: number) { return client.get(`/inspection-tasks/${id}`) }
export function completeTask(id: number) { return client.put(`/inspection-tasks/${id}/complete`) }

export function getRecords(taskId: number) { return client.get(`/inspection-tasks/${taskId}/records`) }
export function createRecord(data: any) { return client.post('/inspection-records', data) }

export function getAnomalies(params?: any) { return client.get('/inspection-anomalies', { params }) }
export function resolveAnomaly(id: number, resolution: string) { return client.put(`/inspection-anomalies/${id}/resolve`, { resolution }) }
export function transferToMaintenance(anomalyId: number) { return client.post(`/inspection-anomalies/${anomalyId}/transfer-maintenance`) }
