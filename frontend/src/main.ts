import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'aos/dist/aos.css'
import AOS from 'aos'
import '@fortawesome/fontawesome-free/css/all.min.css'
import { createPinia } from 'pinia'
import router from '@/router'
import { httpPlugin } from '@/plugins/http'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.use(httpPlugin)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')

AOS.init({
  duration: 800,
  easing: 'ease-in-out',
  once: true,
})
