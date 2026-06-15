import client from './client'

export function getMyTasks(params?: any) { return client.get('/inspection-tasks/my', { params }) }
export function createTask(data: any) { return client.post('/inspection-tasks', data) }
export function completeTask(id: number) { return client.put(`/inspection-tasks/${id}/complete`) }

export function getRecords(taskId: number) { return client.get(`/inspection-tasks/${taskId}/records`) }
export function createRecord(data: any) { return client.post('/inspection-records', data) }

export function getAnomalies(params?: { taskId?: number }) {
  if (params?.taskId) return client.get(`/inspection-tasks/${params.taskId}/anomalies`)
  return client.get('/inspection-anomalies') // TODO: backend needs generic anomaly listing endpoint
}
export function resolveAnomaly(id: number, resolution: string) { return client.put(`/inspection-anomalies/${id}/resolve`, { resolution }) }
export function transferToMaintenance(anomalyId: number) { return client.put(`/inspection-anomalies/${anomalyId}/transfer-to-maintenance`) }
