import client from './client'

export interface WorkflowDef {
  id?: string
  name: string
  bizType: string
  graphJson: string
  status?: string
  description?: string
  tenantId?: number
  createdAt?: string
}

export interface WorkflowInstance {
  id?: string
  defId: string
  bizId: number
  bizType: string
  status: string
  currentNodeIds: string
  contextJson: string
  submitterId: number
  createdAt?: string
}

export interface WorkflowTask {
  id?: string
  instanceId: string
  nodeId: string
  approverRole: string
  approverId: number | null
  action: string
  remark: string | null
  completedAt: string | null
}

export function listWorkflows() {
  return client.get('/workflows') as Promise<{ data: WorkflowDef[] }>
}

export function getWorkflow(id: string) {
  return client.get(`/workflows/${id}`) as Promise<{ data: WorkflowDef }>
}

export function createWorkflow(data: WorkflowDef) {
  return client.post('/workflows', data) as Promise<{ data: WorkflowDef }>
}

export function updateWorkflow(id: string, data: WorkflowDef) {
  return client.put(`/workflows/${id}`, data) as Promise<{ data: WorkflowDef }>
}

export function deleteWorkflow(id: string) {
  return client.delete(`/workflows/${id}`) as Promise<{ data: null }>
}

export function startInstance(defId: string, data: { bizId: number; bizType: string; contextJson?: string }) {
  return client.post(`/workflows/${defId}/start`, data) as Promise<{ data: WorkflowInstance }>
}

export function approveTask(taskId: string, data: { approverId: number; approved: boolean; remark?: string }) {
  return client.put(`/workflows/tasks/${taskId}/approve`, data) as Promise<{ data: null }>
}

export function getPendingTasks(roleCode: string) {
  return client.get('/workflows/tasks/pending', { params: { roleCode } }) as Promise<{ data: WorkflowTask[] }>
}

export function getMyRequests(submitterId: number) {
  return client.get('/workflows/tasks/my-requests', { params: { submitterId } }) as Promise<{ data: WorkflowInstance[] }>
}
