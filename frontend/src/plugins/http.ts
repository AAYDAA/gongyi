import type { App } from 'vue'
import { request } from '@/utils/request'

export const httpPlugin = {
  install(app: App) {
    app.config.globalProperties.$http = request
    app.provide('http', request)
  },
}
