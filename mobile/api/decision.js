import request from '@/utils/request'

export function getPendingItems(params) {
  return request.get('/decision/items', params)
}

export function submitDecisionItem(data) {
  return request.post('/decision/items', data)
}

export function approveItem(id, data) {
  return request.put(`/decision/items/${id}/approve`, data)
}

export function getPendingSupervisions(params) {
  return request.get('/decision/supervisions/pending', params)
}
