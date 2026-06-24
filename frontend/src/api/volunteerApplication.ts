import { request } from '@/utils/request'
import type { PageResp } from '@/api/activity'
import type { UserInfo } from '@/stores/user'

export type VolunteerApplicationStatus = 'DRAFT' | 'PENDING' | 'APPROVED' | 'REJECTED'

export interface VolunteerApplication {
  id: number
  userId: number
  realName: string
  contactPhone: string
  skills?: string
  experience?: string
  availableTime?: string
  motivation: string
  status: VolunteerApplicationStatus
  reviewRemark?: string | null
  submitTime: string
  reviewTime?: string | null
  user?: UserInfo
  createTime?: string
  updateTime?: string
}

export const volunteerApplicationApi = {
  mine() {
    return request.get<unknown, VolunteerApplication>('/volunteer-applications/me')
  },
  save(payload: Partial<VolunteerApplication>) {
    return request.put<unknown, VolunteerApplication>('/volunteer-applications/me', payload)
  },
  submit() {
    return request.post<unknown, VolunteerApplication>('/volunteer-applications/me/submit')
  },
  page(params: { keyword?: string; status?: VolunteerApplicationStatus; page?: number; size?: number }) {
    return request.get<unknown, PageResp<VolunteerApplication>>('/volunteer-applications', { params })
  },
  review(id: number, payload: { status: 'APPROVED' | 'REJECTED'; reviewRemark?: string }) {
    return request.put<unknown, VolunteerApplication>(`/volunteer-applications/${id}/review`, payload)
  },
}
