import { request } from '@/utils/request'
import type { UserInfo } from '@/stores/user'
import type { PageResp } from '@/api/activity'

export type ProjectStatus = 'DRAFT' | 'ACTIVE' | 'COMPLETED'

export interface CharityProject {
  id: number
  title: string
  coverImage?: string
  summary?: string
  contentHtml?: string
  goalAmount: number
  raisedAmount: number
  status: ProjectStatus
  startTime?: string
  endTime?: string
  creator: UserInfo
  createTime?: string
  updateTime?: string
}

export type DonationChannel = 'WECHAT' | 'ALIPAY' | 'BANK'
export type DonationStatus = 'PENDING' | 'SUCCESS' | 'FAILED'

export interface DonationRecord {
  id: number
  projectId: number
  projectTitle: string
  donor: UserInfo
  amount: number
  channel: DonationChannel
  status: DonationStatus
  donateTime: string
  remark?: string
}

export const projectApi = {
  page(params: { keyword?: string; status?: ProjectStatus; creatorId?: number; page?: number; size?: number }) {
    return request.get<unknown, PageResp<CharityProject>>('/projects', { params })
  },
  detail(id: number) {
    return request.get<unknown, CharityProject>(`/projects/${id}`)
  },
  create(payload: Partial<CharityProject>) {
    return request.post<unknown, CharityProject>('/projects', payload)
  },
  update(id: number, payload: Partial<CharityProject>) {
    return request.put<unknown, CharityProject>(`/projects/${id}`, payload)
  },
  donate(id: number, payload: { amount: number; channel?: DonationChannel; remark?: string }) {
    return request.post<unknown, DonationRecord>(`/projects/${id}/donate`, payload)
  },
  donations(id: number, params: { page?: number; size?: number }) {
    return request.get<unknown, PageResp<DonationRecord>>(`/projects/${id}/donations`, { params })
  },
}

