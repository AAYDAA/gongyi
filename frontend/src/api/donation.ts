import { request } from '@/utils/request'
import type { PageResp } from '@/api/activity'
import type { DonationRecord } from '@/api/project'

export const donationApi = {
  my(params: { page?: number; size?: number }) {
    return request.get<unknown, PageResp<DonationRecord>>('/donations/my', { params })
  },
}

