import client from './client'

export interface LoginRequest { username: string; password: string }
export interface LoginResponse {
  token: string; userId: number; username: string
  realName: string; tenantId: number; orgId: number; roleCode: string
  permissions: string[]
  roles: string[]
}

export function login(data: LoginRequest): Promise<{ data: LoginResponse }> {
  return client.post('/auth/login', data)
}
