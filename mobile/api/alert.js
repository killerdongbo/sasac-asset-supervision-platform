import request from '@/utils/request'

export function getAlerts(params) {
  return request.get('/alerts', params)
}

export function getAlertCounts() {
  return request.get('/alerts/counts')
}

export function acknowledgeAlert(id) {
  return request.put(`/alerts/${id}/acknowledge`)
}

export function resolveAlert(id) {
  return request.put(`/alerts/${id}/resolve`)
}
