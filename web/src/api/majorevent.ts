import client from './client'

// ==================== Major Events ====================

export function getEvents(params?: { tenantId?: number; eventType?: string; status?: string; page?: number; size?: number }) {
  return client.get('/major-events/events', { params })
}

export function getEvent(id: number) {
  return client.get(`/major-events/events/${id}`)
}

export function createEvent(data: any) {
  return client.post('/major-events/events', data)
}

export function updateEvent(id: number, data: any) {
  return client.put(`/major-events/events/${id}`, data)
}

export function approveEvent(id: number) {
  return client.post(`/major-events/events/${id}/approve`)
}

export function trackEvent(id: number, progress: string) {
  return client.put(`/major-events/events/${id}/track`, null, { params: { progress } })
}

export function resolveEvent(id: number) {
  return client.put(`/major-events/events/${id}/resolve`)
}

export function deleteEvent(id: number) {
  return client.delete(`/major-events/events/${id}`)
}

// ==================== Lawsuits ====================

export function getLawsuits(params?: { tenantId?: number; eventId?: number; status?: string; court?: string; page?: number; size?: number }) {
  return client.get('/major-events/lawsuits', { params })
}

export function getLawsuit(id: number) {
  return client.get(`/major-events/lawsuits/${id}`)
}

export function createLawsuit(data: any) {
  return client.post('/major-events/lawsuits', data)
}

export function updateLawsuit(id: number, data: any) {
  return client.put(`/major-events/lawsuits/${id}`, data)
}

export function updateLawsuitStatus(id: number, status: string, progress: string) {
  return client.put(`/major-events/lawsuits/${id}/status`, null, { params: { status, progress } })
}

export function deleteLawsuit(id: number) {
  return client.delete(`/major-events/lawsuits/${id}`)
}

// ==================== Guarantees ====================

export function getGuarantees(params?: { tenantId?: number; eventId?: number; riskLevel?: string; guaranteeType?: string; page?: number; size?: number }) {
  return client.get('/major-events/guarantees', { params })
}

export function getGuarantee(id: number) {
  return client.get(`/major-events/guarantees/${id}`)
}

export function createGuarantee(data: any) {
  return client.post('/major-events/guarantees', data)
}

export function updateGuarantee(id: number, data: any) {
  return client.put(`/major-events/guarantees/${id}`, data)
}

export function getExpiringGuarantees(tenantId: number, days: number) {
  return client.get('/major-events/guarantees/expiring', { params: { tenantId, days } })
}

export function deleteGuarantee(id: number) {
  return client.delete(`/major-events/guarantees/${id}`)
}
