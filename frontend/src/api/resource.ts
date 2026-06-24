import { request } from '@/utils/request'
import type { UserInfo } from '@/stores/user'
import type { PageResp } from '@/api/activity'

export type ResourceStatus = 'REQUESTING' | 'CLAIMED' | 'COMPLETED'
export type ResourceClaimStatus = 'SUBMITTED' | 'CONFIRMED' | 'REJECTED'

export interface Resource {
  id: number
  title: string
  description?: string
  imageUrls?: string
  status: ResourceStatus
  publisher: UserInfo
  publishTime?: string
  createTime?: string
  updateTime?: string
}

export interface ResourceClaim {
  id: number
  resourceId: number
  resourceTitle: string
  donor: UserInfo
  quantity: number
  status: ResourceClaimStatus
  remark?: string
  donateTime: string
}

export const resourceApi = {
  page(params: { keyword?: string; status?: ResourceStatus; publisherId?: number; page?: number; size?: number }) {
    return request.get<unknown, PageResp<Resource>>('/resources', { params })
  },
  detail(id: number) {
    return request.get<unknown, Resource>(`/resources/${id}`)
  },
  create(payload: Partial<Resource>) {
    return request.post<unknown, Resource>('/resources', payload)
  },
  update(id: number, payload: Partial<Resource>) {
    return request.put<unknown, Resource>(`/resources/${id}`, payload)
  },
  remove(id: number) {
    return request.delete<unknown, unknown>(`/resources/${id}`)
  },
  claim(id: number, payload: { quantity?: number; remark?: string }) {
    return request.post<unknown, ResourceClaim>(`/resources/${id}/claim`, payload)
  },
  myClaims(params: { page?: number; size?: number }) {
    return request.get<unknown, PageResp<ResourceClaim>>('/resources/claims/my', { params })
  },
  adminClaims(params: { status?: ResourceClaimStatus; page?: number; size?: number }) {
    return request.get<unknown, PageResp<ResourceClaim>>('/resources/claims', { params })
  },
  reviewClaim(id: number, status: ResourceClaimStatus) {
    return request.put<unknown, ResourceClaim>(`/resources/claims/${id}/review`, undefined, { params: { status } })
  },
}
