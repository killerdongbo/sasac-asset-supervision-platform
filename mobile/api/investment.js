import request from '@/utils/request'

export function queryInvestmentProjects(params) {
  return request.get('/investment/projects', params)
}

export function getInvestmentProject(id) {
  return request.get(`/investment/projects/${id}`)
}

export function getPortfolioSummary(params) {
  return request.get('/investment/portfolio/summary', params)
}
