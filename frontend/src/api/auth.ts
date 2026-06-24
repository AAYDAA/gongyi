import { request } from '@/utils/request'
import type { UserInfo } from '@/stores/user'

export interface LoginRequest {
  account: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  phone?: string
  email?: string
}

export interface AuthResponse {
  token: string
  userInfo: UserInfo
}

export const authApi = {
  login(payload: LoginRequest) {
    return request.post<unknown, AuthResponse>('/auth/login', payload)
  },
  register(payload: RegisterRequest) {
    return request.post<unknown, AuthResponse>('/auth/register', payload)
  },
}
