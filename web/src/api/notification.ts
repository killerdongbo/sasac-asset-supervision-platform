import request from './client'

export interface Notification {
  id: string; title: string; content: string; type: string
  level: string; isRead: number; bizType?: string; bizId?: number
  createdAt: string
}

export function listNotifications(params: { userId: number; isRead?: number; page?: number; size?: number }) {
  return request.get('/notifications', { params })
}
export function getUnreadCount(userId: number) {
  return request.get('/notifications/unread-count', { params: { userId } })
}
export function markRead(id: string, userId: number) {
  return request.put(`/notifications/${id}/read`, null, { params: { userId } })
}
export function markAllRead(userId: number) {
  return request.put('/notifications/read-all', null, { params: { userId } })
}
