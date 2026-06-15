import request from './client'

export interface Inquiry {
  id: string
  tenantId: number
  inquiryNo: string
  title: string
  category: string
  specification: string
  quantity: number
  unit: string
  budgetAmount: number | null
  status: string
  deadline: string | null
  createdBy: string | null
  createdAt: string
}

export interface Quotation {
  id: string
  tenantId: number
  inquiryId: string
  supplierId: number
  supplierName: string
  unitPrice: number
  totalPrice: number
  taxRate: number | null
  deliveryDays: number | null
  warrantyMonths: number | null
  quotedAt: string
  isSelected: number
}

export interface PriceHistory {
  id: string
  category: string
  specification: string
  supplierId: number
  supplierName: string
  unitPrice: number
  sourceType: string
  recordDate: string
}

export interface PriceAnalysis {
  avgPrice: number
  minPrice: number
  maxPrice: number
  recordCount: number
}

export function listInquiries(params: { status?: string; page?: number; size?: number }) {
  return request.get('/inquiries', { params })
}

export function getInquiry(id: string) {
  return request.get(`/inquiries/${id}`)
}

export function createInquiry(data: Partial<Inquiry>) {
  return request.post('/inquiries', data)
}

export function publishInquiry(id: string) {
  return request.put(`/inquiries/${id}/publish`)
}

export function closeInquiry(id: string) {
  return request.put(`/inquiries/${id}/close`)
}

export function listQuotations(inquiryId: string) {
  return request.get(`/quotations/inquiry/${inquiryId}`)
}

export function submitQuotation(data: Partial<Quotation>) {
  return request.post('/quotations', data)
}

export function selectQuotation(id: string) {
  return request.put(`/quotations/${id}/select`)
}

export function getPriceTrend(params: { category: string; startDate?: string; endDate?: string }) {
  return request.get('/price-history/trend', { params })
}

export function getPriceAnalysis(category: string) {
  return request.get('/price-history/analysis', { params: { category } })
}
