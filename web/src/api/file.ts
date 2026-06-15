import request from './client'

export interface FileAttachment {
  id: string; originalName: string; fileSize: number; contentType: string
  fileExt: string; isImage: number; bizType?: string; bizId?: number
  thumbPath?: string; createdAt: string
}

export function uploadFile(file: File, bizType?: string, bizId?: number) {
  const fd = new FormData(); fd.append('file', file)
  if (bizType) fd.append('bizType', bizType)
  if (bizId) fd.append('bizId', String(bizId))
  return request.post('/files/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
}

export function uploadFiles(files: File[], bizType?: string, bizId?: number) {
  const fd = new FormData()
  files.forEach(f => fd.append('files', f))
  if (bizType) fd.append('bizType', bizType)
  if (bizId) fd.append('bizId', String(bizId))
  return request.post('/files/upload/batch', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
}

export function getFileDetail(id: string) { return request.get(`/files/${id}`) }
export function getPreviewUrl(id: string) { return request.get(`/files/${id}/preview`) }
export function listBizFiles(bizType: string, bizId: number) { return request.get('/files/biz', { params: { bizType, bizId } }) }
export function deleteFile(id: string) { return request.delete(`/files/${id}`) }
