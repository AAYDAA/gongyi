import { request } from '@/utils/request'
import type { UserInfo } from '@/stores/user'

export type ActivityStatus = 'DRAFT' | 'RECRUITING' | 'ONGOING' | 'ENDED'
export type ActivityApplicationStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'

export interface Activity {
  id: number
  title: string
  coverImage?: string
  contentHtml?: string
  location?: string
  startTime?: string
  endTime?: string
  deadlineTime?: string
  status: ActivityStatus
  creator: UserInfo
  createTime?: string
  updateTime?: string
}

export interface PageResp<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface ActivityApplication {
  id: number
  activityId: number
  volunteer: UserInfo
  applyTime: string
  status: ActivityApplicationStatus
  remark?: string
}

export interface MyActivityApplication {
  id: number
  activityId: number
  activityTitle: string
  activityLocation?: string
  activityStartTime?: string
  activityEndTime?: string
  applyTime: string
  status: ActivityApplicationStatus
  remark?: string
}

export const activityApi = {
  page(params: {
    keyword?: string
    location?: string
    status?: ActivityStatus
    creatorId?: number
    page?: number
    size?: number
  }) {
    return request.get<unknown, PageResp<Activity>>('/activities', { params })
  },
  detail(id: number) {
    return request.get<unknown, Activity>(`/activities/${id}`)
  },
  create(payload: Partial<Activity>) {
    return request.post<unknown, Activity>('/activities', payload)
  },
  update(id: number, payload: Partial<Activity>) {
    return request.put<unknown, Activity>(`/activities/${id}`, payload)
  },
  apply(id: number, remark?: string) {
    return request.post<unknown, unknown>(`/activities/${id}/apply`, { remark })
  },
  my(page = 0, size = 10) {
    return request.get<unknown, PageResp<Activity>>('/activities/my', { params: { page, size } })
  },
  review(id: number, status: ActivityStatus) {
    return request.put<unknown, Activity>(`/activities/${id}/review`, undefined, { params: { status } })
  },
  applications(id: number, params: { page?: number; size?: number }) {
    return request.get<unknown, PageResp<ActivityApplication>>(`/activities/${id}/applications`, { params })
  },
  reviewApplication(id: number, status: 'APPROVED' | 'REJECTED') {
    return request.put<unknown, ActivityApplication>(`/activities/applications/${id}/review`, undefined, { params: { status } })
  },
  cancelMyApplication(id: number) {
    return request.put<unknown, ActivityApplication>(`/activities/applications/${id}/cancel`)
  },
  myApplied(page = 0, size = 10) {
    return request.get<unknown, PageResp<MyActivityApplication>>('/activities/applied/my', { params: { page, size } })
  },
}
