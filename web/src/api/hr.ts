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

// ===== Salary =====

export interface HrSalary {
  id: string
  tenantId: number
  employeeId: number
  salaryYear: number
  salaryMonth: number
  baseSalary?: number
  performancePay?: number
  overtimePay?: number
  allowance?: number
  deduction?: number
  socialInsurance?: number
  housingFund?: number
  tax?: number
  netSalary?: number
  status: string
}

export interface SalaryCalculateDTO {
  employeeId: number
  tenantId: number
  salaryYear: number
  salaryMonth: number
}

export interface SalaryQuery {
  employeeId?: number
  tenantId?: number
  salaryYear?: number
  salaryMonth?: number
  page?: number
  limit?: number
}

export function calculateSalary(data: SalaryCalculateDTO) {
  return client.post('/hr/salaries/calculate', data) as Promise<{ data: HrSalary }>
}

export function querySalaries(params: SalaryQuery) {
  return client.get('/hr/salaries', { params }) as Promise<{ data: HrSalary[]; meta?: { total: number; page: number; limit: number } }>
}

export function confirmSalary(id: string) {
  return client.post(`/hr/salaries/${id}/confirm`) as Promise<{ data: HrSalary }>
}

// ===== Attendance =====

export interface HrAttendance {
  id: string
  tenantId: number
  employeeId: number
  attDate: string
  checkIn?: string
  checkOut?: string
  status: string
}

export interface AttendanceRecordDTO {
  employeeId: number
  tenantId: number
  attDate: string
  checkIn?: string
  checkOut?: string
}

export function recordAttendance(data: AttendanceRecordDTO) {
  return client.post('/hr/attendances', data) as Promise<{ data: HrAttendance }>
}

export function queryAttendances(employeeId: number, tenantId: number, year: number, month: number) {
  return client.get('/hr/attendances', { params: { employeeId, tenantId, year, month } }) as Promise<{ data: HrAttendance[] }>
}

// ===== Performance =====

export interface HrPerformance {
  id: string
  tenantId: number
  employeeId: number
  cycleType?: string
  cycleYear?: number
  kpiItems?: string
  selfScore?: number
  managerScore?: number
  finalScore?: number
  grade?: string
  reviewComment?: string
  status: string
}

export interface PerformanceScoreDTO {
  selfScore: number
  managerScore: number
}

export function createPerformance(data: Partial<HrPerformance>) {
  return client.post('/hr/performances', data) as Promise<{ data: HrPerformance }>
}

export function scorePerformance(id: string, data: PerformanceScoreDTO) {
  return client.post(`/hr/performances/${id}/score`, data) as Promise<{ data: HrPerformance }>
}

export function confirmPerformance(id: string) {
  return client.post(`/hr/performances/${id}/confirm`) as Promise<{ data: HrPerformance }>
}

export function queryPerformances(params: { page?: number; limit?: number; tenantId?: number; employeeId?: number }) {
  return client.get('/hr/performances', { params }) as Promise<{ data: HrPerformance[]; meta?: { total: number; page: number; limit: number } }>
}

// ===== Recruitment =====

export interface HrRecruitment {
  id: string
  tenantId: number
  orgId: number
  positionName?: string
  headcount?: number
  pipeline?: string
  candidates?: string
  status: string
}

export function createRecruitment(data: Partial<HrRecruitment>) {
  return client.post('/hr/recruitments', data) as Promise<{ data: HrRecruitment }>
}

export function queryRecruitments(params: { page?: number; limit?: number; tenantId?: number }) {
  return client.get('/hr/recruitments', { params }) as Promise<{ data: HrRecruitment[]; meta?: { total: number; page: number; limit: number } }>
}

export function getRecruitment(id: string) {
  return client.get(`/hr/recruitments/${id}`) as Promise<{ data: HrRecruitment }>
}

export function updateRecruitment(id: string, data: Partial<HrRecruitment>) {
  return client.put(`/hr/recruitments/${id}`, data) as Promise<{ data: HrRecruitment }>
}

export function deleteRecruitment(id: string) {
  return client.delete(`/hr/recruitments/${id}`) as Promise<{ data: null }>
}

// ===== Training =====

export interface HrTraining {
  id: string
  tenantId: number
  courseName?: string
  trainer?: string
  trainingDate?: string
  durationHours?: number
  attendees?: string
  evaluationSummary?: string
  attachmentIds?: string
}

export function createTraining(data: Partial<HrTraining>) {
  return client.post('/hr/trainings', data) as Promise<{ data: HrTraining }>
}

export function queryTrainings(params: { page?: number; limit?: number; tenantId?: number }) {
  return client.get('/hr/trainings', { params }) as Promise<{ data: HrTraining[]; meta?: { total: number; page: number; limit: number } }>
}

export function getTraining(id: string) {
  return client.get(`/hr/trainings/${id}`) as Promise<{ data: HrTraining }>
}

export function updateTraining(id: string, data: Partial<HrTraining>) {
  return client.put(`/hr/trainings/${id}`, data) as Promise<{ data: HrTraining }>
}

export function deleteTraining(id: string) {
  return client.delete(`/hr/trainings/${id}`) as Promise<{ data: null }>
}
