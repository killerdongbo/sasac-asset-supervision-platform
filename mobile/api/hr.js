import request from '@/utils/request'

export function queryEmployees(params) {
  return request.get('/hr/employees', params)
}

export function getEmployee(id) {
  return request.get(`/hr/employees/${id}`)
}

export function getSalaryRecords(params) {
  return request.get('/hr/salaries', params)
}

export function confirmSalary(id) {
  return request.post(`/hr/salaries/${id}/confirm`)
}
