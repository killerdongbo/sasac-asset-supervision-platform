import client from './client'

export interface HrEmployee {
  id: string
  tenantId: number
  orgId: number
  employeeNo: string
  name: string
  gender?: string
  idCard?: string
  phone?: string
  email?: string
  education?: string
  entryDate?: string
  deptId?: number
  position?: string
  title?: string
  employmentType?: string
  status?: string
}

export interface EmployeeQuery {
  orgId?: number
  deptId?: number
  keyword?: string
  status?: string
  employmentType?: string
  page?: number
  limit?: number
}

export interface EmployeeCreateDTO {
  orgId: number
  tenantId: number
  employeeNo: string
  name: string
  gender?: string
  phone?: string
  email?: string
  entryDate?: string
  deptId?: number
  position?: string
  title?: string
  employmentType?: string
}

export function queryEmployees(params: EmployeeQuery) {
  return client.get('/hr/employees', { params }) as Promise<{ data: HrEmployee[]; meta?: { total: number; page: number; limit: number } }>
}

export function getEmployee(id: string) {
  return client.get(`/hr/employees/${id}`) as Promise<{ data: HrEmployee }>
}

export function createEmployee(data: EmployeeCreateDTO) {
  return client.post('/hr/employees', data) as Promise<{ data: HrEmployee }>
}

export function updateEmployee(id: string, data: Partial<HrEmployee>) {
  return client.put(`/hr/employees/${id}`, data) as Promise<{ data: HrEmployee }>
}

export function deleteEmployee(id: string) {
  return client.delete(`/hr/employees/${id}`) as Promise<{ data: null }>
}
