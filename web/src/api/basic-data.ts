import client from './client'

// 资产分类
export function getCategories() { return client.get('/categories') }
export function createCategory(data: any) { return client.post('/categories', data) }
export function updateCategory(id: number, data: any) { return client.put(`/categories/${id}`, data) }
export function deleteCategory(id: number) { return client.delete(`/categories/${id}`) }

// 存放位置
export function getLocations() { return client.get('/locations') }
export function createLocation(data: any) { return client.post('/locations', data) }
export function updateLocation(id: number, data: any) { return client.put(`/locations/${id}`, data) }
export function deleteLocation(id: number) { return client.delete(`/locations/${id}`) }

// 供应商
export function getSuppliers() { return client.get('/suppliers') }
export function createSupplier(data: any) { return client.post('/suppliers', data) }
export function updateSupplier(id: number, data: any) { return client.put(`/suppliers/${id}`, data) }
export function deleteSupplier(id: number) { return client.delete(`/suppliers/${id}`) }

// 维保商
export function getProviders() { return client.get('/maintenance-providers') }
export function createProvider(data: any) { return client.post('/maintenance-providers', data) }
export function updateProvider(id: number, data: any) { return client.put(`/maintenance-providers/${id}`, data) }
export function deleteProvider(id: number) { return client.delete(`/maintenance-providers/${id}`) }
