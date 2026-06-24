import { defineStore } from 'pinia'

export type UserRole = 'ADMIN' | 'ORGANIZATION' | 'VOLUNTEER' | 'USER'

export interface UserInfo {
  id: number
  username: string
  phone?: string
  email?: string
  avatarUrl?: string
  role: UserRole
  registerTime?: string
  status?: 'ACTIVE' | 'DISABLED'
}

const TOKEN_KEY = 'gongyi_token'
const USER_KEY = 'gongyi_user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    userInfo: (localStorage.getItem(USER_KEY) ? (JSON.parse(localStorage.getItem(USER_KEY)!) as UserInfo) : null) as
      | UserInfo
      | null,
  }),
  actions: {
    setAuth(token: string, userInfo: UserInfo) {
      this.token = token
      this.userInfo = userInfo
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
    },
    clearAuth() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
  },
})
