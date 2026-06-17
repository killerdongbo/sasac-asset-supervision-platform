import request from '@/utils/request'

export function queryProjects(params) {
  return request.get('/projects', params)
}

export function getProject(id) {
  return request.get(`/projects/${id}`)
}

export function recordProgress(projectId, data) {
  return request.post(`/projects/${projectId}/progress`, data)
}

export function getProgressHistory(projectId, params) {
  return request.get(`/projects/${projectId}/progress`, params)
}
