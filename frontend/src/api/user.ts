import { request } from '@/utils/request'
import type { UserInfo } from '@/stores/user'

export const userApi = {
  me() {
    return request.get<unknown, UserInfo>('/users/me')
  },
  updateMe(payload: Partial<UserInfo>) {
    return request.put<unknown, UserInfo>('/users/me', payload)
  },
  applyVolunteer() {
    return request.post<unknown, UserInfo>('/users/me/apply-volunteer')
  },
}
