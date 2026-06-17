import request from '@/utils/request'

export function queryIndicators(params) {
  return request.get('/finance/indicators', params)
}

export function calculateIndicators(data) {
  return request.post('/finance/indicators/calculate', data)
}

export function queryReports(params) {
  return request.get('/finance/reports', params)
}
