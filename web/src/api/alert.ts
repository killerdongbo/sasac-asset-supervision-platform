import client from './client'

export function getAlerts(params?: any) { return client.get('/alerts', { params }) }
export function acknowledgeAlert(id: number) { return client.put(`/alerts/${id}/acknowledge`) }
export function resolveAlert(id: number) { return client.put(`/alerts/${id}/resolve`) }
export function getAlertRules() { return client.get('/alert-rules') }
export function saveAlertRule(data: any) { return client.post('/alert-rules', data) }
