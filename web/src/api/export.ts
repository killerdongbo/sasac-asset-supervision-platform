import client from './client'

export interface ExportTask {
  id: string
  exportType: string
  fileName: string | null
  filePath: string | null
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  params: string | null
  reportPeriod: string | null
  totalRows: number | null
  errorMessage: string | null
  createdBy: string
  createdAt: string
  completedAt: string | null
}

export interface ExportRequest {
  exportType: string
  params?: string
}

export interface ImportErrorRow {
  row: number
  field: string
  reason: string
}

export interface ImportResult {
  totalRows: number
  successRows: number
  errorRows: number
  errors: ImportErrorRow[]
  previewData: any[]
}

export interface ReportTypeGroup {
  label: string
  options: { value: string; label: string }[]
}

export const reportTypeGroups: ReportTypeGroup[] = [
  {
    label: '综合报表',
    options: [
      { value: 'BALANCE_SHEET', label: '资产负债表' },
      { value: 'ASSET_BASE_LIST', label: '资产底数清单' },
      { value: 'PROBLEM_ASSET_LIST', label: '问题资产及整治清单' },
      { value: 'REVITALIZATION_LIST', label: '盘活利用清单' },
    ],
  },
  {
    label: '资产清查明细',
    options: [
      { value: 'LAND_ASSET_DETAIL', label: '土地类资产清查明细表' },
      { value: 'BUILDING_ASSET_DETAIL', label: '房屋建筑类清查明细表' },
      { value: 'EQUITY_INVESTMENT_DETAIL', label: '股权类（对外投资）清查明细表' },
      { value: 'CREDITOR_RIGHTS_DETAIL', label: '债权类清查明细表' },
      { value: 'PE_FUND_INVESTMENT_DETAIL', label: '企业私募基金投资情况调查表' },
      { value: 'INVENTORY_DETAIL', label: '存货清查明细表' },
      { value: 'INTANGIBLE_ASSET_DETAIL', label: '无形资产清查明细表' },
      { value: 'FRANCHISE_RIGHT_DETAIL', label: '特许经营权清查明细表' },
      { value: 'DATA_ASSET_DETAIL', label: '数据资源/资产清查明细表' },
    ],
  },
  {
    label: '实物与资金',
    options: [
      { value: 'NATURAL_RESOURCE_DETAIL', label: '自然资源资产清查明细表' },
      { value: 'MACHINERY_EQUIP_DETAIL', label: '大型机器设备清查明细表' },
      { value: 'INFRASTRUCTURE_DETAIL', label: '基础设施资产清查明细表' },
      { value: 'MONETARY_FUND_DETAIL', label: '货币资金清查明细表' },
      { value: 'OTHER_FIXED_ASSET_DETAIL', label: '其他固定资产清查明细表' },
    ],
  },
  {
    label: '稽核报表',
    options: [
      { value: 'RECONCILIATION_TABLE', label: '汇总表与明细表的勾稽关系表' },
    ],
  },
]

export function createExportTask(data: ExportRequest) {
  return client.post('/exports', data) as Promise<{ data: ExportTask }>
}

export function listExportTasks(page = 1, size = 20) {
  return client.get('/exports', { params: { page, size } }) as Promise<{
    data: { records: ExportTask[]; total: number; current: number; size: number }
  }>
}

export function getExportDownloadUrl(id: string) {
  return client.get(`/exports/${id}/download`) as Promise<{ data: string }>
}

export function downloadExportFile(id: string): Promise<Blob> {
  return client.get(`/exports/${id}/download`, {
    responseType: 'blob',
  }) as unknown as Promise<Blob>
}

export function downloadTemplate(type: string): Promise<Blob> {
  return client.get('/exports/template', {
    params: { type },
    responseType: 'blob',
  }) as unknown as Promise<Blob>
}

export function previewImport(file: File, exportType: string) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('exportType', exportType)
  return client.post('/exports/import/preview', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }) as Promise<{ data: ImportResult }>
}

export function importReport(file: File, exportType: string) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('exportType', exportType)
  return client.post('/exports/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }) as Promise<{ data: ImportResult }>
}
