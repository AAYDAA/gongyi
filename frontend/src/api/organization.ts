import { request } from '@/utils/request'
import type { PageResp } from '@/api/activity'
import type { UserInfo } from '@/stores/user'

export type OrganizationVerifyStatus = 'UNSUBMITTED' | 'PENDING' | 'APPROVED' | 'REJECTED'

export interface OrganizationProfile {
  id: number
  userId: number
  name: string
  logoUrl?: string
  intro?: string
  address?: string
  contactPhone?: string
  website?: string
  licenseImageUrls?: string
  verifyStatus: OrganizationVerifyStatus
  verifyRemark?: string | null
  verifyTime?: string | null
  user: UserInfo
  createTime?: string
  updateTime?: string
}

export const organizationApi = {
  page(params: { keyword?: string; verifyStatus?: OrganizationVerifyStatus; page?: number; size?: number }) {
    return request.get<unknown, PageResp<OrganizationProfile>>('/organizations', { params })
  },
  detail(id: number) {
    return request.get<unknown, OrganizationProfile>(`/organizations/${id}`)
  },
  me() {
    return request.get<unknown, OrganizationProfile>('/organizations/me')
  },
  updateMe(payload: Partial<OrganizationProfile>) {
    return request.put<unknown, OrganizationProfile>('/organizations/me', payload)
  },
  submit() {
    return request.post<unknown, OrganizationProfile>('/organizations/me/submit')
  },
  verify(id: number, payload: { verifyStatus: OrganizationVerifyStatus; verifyRemark?: string }) {
    return request.put<unknown, OrganizationProfile>(`/organizations/${id}/verify`, payload)
  },
}

