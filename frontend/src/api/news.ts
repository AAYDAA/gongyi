import { request } from '@/utils/request'
import type { UserInfo } from '@/stores/user'
import type { PageResp } from '@/api/activity'

export type NewsStatus = 'DRAFT' | 'PUBLISHED'

export interface NewsArticle {
  id: number
  title: string
  coverImage?: string
  summary?: string
  contentHtml?: string
  status: NewsStatus
  publishTime?: string
  author?: UserInfo | null
  createTime?: string
  updateTime?: string
}

export const newsApi = {
  page(params: { keyword?: string; status?: NewsStatus; page?: number; size?: number }) {
    return request.get<unknown, PageResp<NewsArticle>>('/news', { params })
  },
  pageAdmin(params: { keyword?: string; status?: NewsStatus; page?: number; size?: number }) {
    return request.get<unknown, PageResp<NewsArticle>>('/news/admin-page', { params })
  },
  detail(id: number) {
    return request.get<unknown, NewsArticle>(`/news/${id}`)
  },
  create(payload: Partial<NewsArticle>) {
    return request.post<unknown, NewsArticle>('/news', payload)
  },
  update(id: number, payload: Partial<NewsArticle>) {
    return request.put<unknown, NewsArticle>(`/news/${id}`, payload)
  },
  remove(id: number) {
    return request.delete<unknown, unknown>(`/news/${id}`)
  },
}
