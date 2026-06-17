import request from '@/utils/request'

export function getPendingApprovals(params) {
  return request.get('/approval-instances/pending', params)
}

export function getInstance(id) {
  return request.get(`/approval-instances/${id}`)
}

export function approve(id, data) {
  return request.put(`/approval-instances/${id}/approve`, data)
}

export function getMyRequests(params) {
  return request.get('/approval-instances/my-requests', params)
}
