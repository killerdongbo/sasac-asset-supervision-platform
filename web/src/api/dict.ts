import request from './client'

export interface DictType { id?: string; dictCode: string; dictName: string; remark?: string }
export interface DictItem { id?: string; dictCode: string; itemKey: string; itemValue: string; sortOrder?: number; remark?: string }

export function listDictTypes() { return request.get('/dicts/types') }
export function createDictType(data: DictType) { return request.post('/dicts/types', data) }
export function deleteDictType(id: string) { return request.delete(`/dicts/types/${id}`) }
export function listDictItems(dictCode: string) { return request.get(`/dicts/items/${dictCode}`) }
export function createDictItem(data: DictItem) { return request.post('/dicts/items', data) }
export function updateDictItem(id: string, data: DictItem) { return request.put(`/dicts/items/${id}`, data) }
export function deleteDictItem(id: string) { return request.delete(`/dicts/items/${id}`) }
