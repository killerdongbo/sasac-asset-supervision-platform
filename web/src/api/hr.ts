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

// ===== Contract =====

export interface HrContract {
  id: string
  tenantId: number
  employeeId: number
  contractNo: string
  contractType: string
  signDate: string
  startDate: string
  endDate?: string
  isUnlimited?: boolean
  status: string
  termsSummary?: string
  attachmentIds?: string
}

export interface ContractQuery {
  employeeId?: number
  tenantId?: number
  page?: number
  limit?: number
}

export function queryContracts(params: ContractQuery) {
  return client.get('/hr/contracts', { params }) as Promise<{ data: HrContract[]; meta?: { total: number; page: number; limit: number } }>
}

export function getContract(id: string) {
  return client.get(`/hr/contracts/${id}`) as Promise<{ data: HrContract }>
}

export function getExpiringContracts(tenantId: number, days?: number) {
  return client.get('/hr/contracts/expiring', { params: { tenantId, days: days || 30 } }) as Promise<{ data: HrContract[] }>
}

export function createContract(data: any) {
  return client.post('/hr/contracts', data) as Promise<{ data: HrContract }>
}

export function updateContract(id: string, data: any) {
  return client.put(`/hr/contracts/${id}`, data) as Promise<{ data: HrContract }>
}

export function deleteContract(id: string) {
  return client.delete(`/hr/contracts/${id}`) as Promise<{ data: null }>
}

// ===== Employee Change =====

export interface HrEmployeeChange {
  id: string
  tenantId: number
  employeeId: number
  changeType: string
  beforeValue?: string
  afterValue?: string
  effectiveDate: string
  reason?: string
  approvalInstanceId?: number
  status: string
}

export interface EmployeeChangeQuery {
  employeeId?: number
  tenantId?: number
  page?: number
  limit?: number
}

export function queryChanges(params: EmployeeChangeQuery) {
  return client.get('/hr/changes', { params }) as Promise<{ data: HrEmployeeChange[]; meta?: { total: number; page: number; limit: number } }>
}

export function createChange(data: any) {
  return client.post('/hr/changes', data) as Promise<{ data: HrEmployeeChange }>
}
