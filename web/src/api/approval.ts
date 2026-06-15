import request from './client'

export interface ApprovalDef {
  id?: string; defName: string; bizType: string; status: string
  description?: string; tenantId?: number
}
export interface ApprovalNode {
  id?: string; defId?: number; nodeOrder: number; approverRole: string
  conditionExpr?: string; canReject?: boolean; timeoutHours?: number
  approveMode?: string; allowAddSign?: boolean; timeoutAction?: string
  conditionType?: string; conditionValue?: string; escalateRole?: string
}
export interface ApprovalInstance {
  id: string; defId: number; bizId: number; bizType: string
  status: string; currentNode: number; nodeResults: string
  submitterId: number; createdAt: string
}
export interface ApprovalAddSign {
  id: string; instanceId: number; nodeOrder: number
  addSignUserId: number; addSignUserName: string; reason: string
  approved?: boolean; remark?: string
}

// Defs
export function getDefs() { return request.get('/approval-defs') }
export function getDef(id: string) { return request.get(`/approval-defs/${id}`) }
export function createDef(data: ApprovalDef) { return request.post('/approval-defs', data) }
export function getNodes(defId: string) { return request.get(`/approval-defs/${defId}/nodes`) }
export function addNode(defId: string, data: ApprovalNode) { return request.post(`/approval-defs/${defId}/nodes`, data) }
export function updateNode(defId: string, nodeId: string, data: ApprovalNode) { return request.put(`/approval-defs/${defId}/nodes/${nodeId}`, data) }
export function deleteNode(defId: string, nodeId: string) { return request.delete(`/approval-defs/${defId}/nodes/${nodeId}`) }

// Instances
export function startInstance(data: { defId: number; bizId: number; bizType: string; submitterId: number }) { return request.post('/approval-instances', data) }
export function approve(id: string, data: { approverId: number; approved: boolean; remark?: string }) { return request.put(`/approval-instances/${id}/approve`, data) }
export function getInstance(id: string) { return request.get(`/approval-instances/${id}`) }
export function getPendingApprovals(tenantId?: number, roleCode?: string) { return request.get('/approval-instances/pending', { params: { tenantId, roleCode } }) }
export function getMyRequests(submitterId: number) { return request.get('/approval-instances/my-requests', { params: { submitterId } }) }

// Add sign
export function addSign(instanceId: string, data: { approverId: number; addSignUserId: number; addSignUserName: string; reason: string }) { return request.post(`/approval-instances/${instanceId}/add-sign`, data) }
export function addSignApprove(addSignId: string, data: { approved: boolean; remark?: string }) { return request.put(`/approval-add-signs/${addSignId}/approve`, data) }
export function cancelAddSign(addSignId: string) { return request.delete(`/approval-add-signs/${addSignId}`) }
export function getAddSigns(instanceId: string, nodeOrder: number) { return request.get(`/approval-instances/${instanceId}/add-signs`, { params: { nodeOrder } }) }
