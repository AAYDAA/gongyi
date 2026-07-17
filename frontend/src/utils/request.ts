import axios, { type AxiosError, type AxiosInstance } from 'axios'
import router from '@/router'
import { useUserStore } from '@/stores/user'

export interface ApiResult<T> {
  code: number
  msg: string
  data: T
}

export const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,
})

request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResult<unknown>
    if (typeof result?.code === 'number') {
      if (result.code === 0) return result.data
      return Promise.reject(new Error(result.msg || '请求失败'))
    }
    return response.data
  },
  (error: AxiosError) => {
    const userStore = useUserStore()
    const status = error.response?.status
    if (status === 401) {
      userStore.clearAuth()
      if (router.currentRoute.value.path !== '/login') {
        router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
      }
    }
    return Promise.reject(error)
  },
)
