<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isAuthed = computed(() => !!userStore.token)
const isAdmin = computed(() => userStore.userInfo?.role === 'ADMIN')
const isOrg = computed(() => userStore.userInfo?.role === 'ORGANIZATION')
const isUser = computed(() => userStore.userInfo?.role === 'USER')

const logout = async () => {
  userStore.clearAuth()
  if (route.path !== '/') await router.push('/')
}
</script>

<template>
  <div class="min-h-full flex flex-col">
    <nav class="bg-white shadow-md sticky top-0 z-50">
      <div class="container mx-auto px-4 py-3">
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-2 cursor-pointer select-none" @click="router.push('/')">
            <i class="fa-solid fa-heart text-primary text-3xl"></i>
            <span class="text-2xl font-bold text-dark">爱心公益</span>
          </div>

          <div class="hidden md:flex items-center space-x-8">
            <RouterLink to="/" class="text-dark hover:text-primary font-medium transition-colors">首页</RouterLink>
            <RouterLink to="/activities" class="text-dark hover:text-primary font-medium transition-colors"
              >志愿者活动</RouterLink
            >
            <RouterLink to="/resources" class="text-dark hover:text-primary font-medium transition-colors"
              >资源需求</RouterLink
            >
            <RouterLink to="/projects" class="text-dark hover:text-primary font-medium transition-colors">公益项目</RouterLink>
            <RouterLink to="/news" class="text-dark hover:text-primary font-medium transition-colors">公益资讯</RouterLink>
            <RouterLink to="/organizations" class="text-dark hover:text-primary font-medium transition-colors">公益组织</RouterLink>
          </div>

          <div class="flex items-center gap-3">
            <template v-if="!isAuthed">
              <button
                class="hidden md:block btn-outline-primary"
                @click="router.push('/login')"
              >
                登录
              </button>
              <button class="hidden md:block btn-primary" @click="router.push('/register')">注册</button>
              <button class="md:hidden btn-primary px-4 py-2" @click="router.push('/login')">登录</button>
            </template>
            <template v-else>
              <el-dropdown trigger="click">
                <div class="flex items-center gap-2 cursor-pointer">
                  <img
                    :src="userStore.userInfo?.avatarUrl || 'https://via.placeholder.com/40'"
                    class="w-9 h-9 rounded-full object-cover"
                  />
                  <span class="text-dark font-medium hidden sm:block">{{ userStore.userInfo?.username }}</span>
                  <i class="fa-solid fa-caret-down text-gray-600"></i>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                    <el-dropdown-item @click="router.push('/donations/my')">我的捐款</el-dropdown-item>
                    <el-dropdown-item v-if="isUser" @click="router.push('/volunteer/apply')">申请成为志愿者</el-dropdown-item>
                    <el-dropdown-item v-if="isOrg" @click="router.push('/org/center')">组织中心</el-dropdown-item>
                    <el-dropdown-item v-if="isOrg" @click="router.push('/org/projects')">项目管理</el-dropdown-item>
                    <el-dropdown-item v-if="isOrg" @click="router.push('/org/activities')">活动管理</el-dropdown-item>
                    <el-dropdown-item v-if="isAdmin" @click="router.push('/admin')">管理中心</el-dropdown-item>
                    <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </div>
        </div>
      </div>
    </nav>

    <main class="flex-1">
      <RouterView />
    </main>

    <footer class="bg-dark text-white pt-16 pb-8">
      <div class="container mx-auto px-4">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-12">
          <div>
            <div class="flex items-center space-x-2 mb-6">
              <i class="fa-solid fa-heart text-primary text-3xl"></i>
              <span class="text-2xl font-bold">爱心公益</span>
            </div>
            <p class="text-gray-400 mb-6">
              让爱心传递，让世界更美好。我们致力于连接有爱心的人与需要帮助的人，共同创造更美好的未来。
            </p>
            <div class="flex space-x-4">
              <a href="#" class="text-gray-400 hover:text-white text-xl transition-colors"><i class="fa-brands fa-weixin"></i></a>
              <a href="#" class="text-gray-400 hover:text-white text-xl transition-colors"><i class="fa-brands fa-weibo"></i></a>
              <a href="#" class="text-gray-400 hover:text-white text-xl transition-colors"><i class="fa-brands fa-qq"></i></a>
              <a href="#" class="text-gray-400 hover:text-white text-xl transition-colors"><i class="fa-solid fa-envelope"></i></a>
            </div>
          </div>

          <div>
            <h3 class="text-lg font-bold mb-6">快速链接</h3>
            <ul class="space-y-3">
              <li><RouterLink to="/" class="text-gray-400 hover:text-white transition-colors">首页</RouterLink></li>
              <li>
                <RouterLink to="/activities" class="text-gray-400 hover:text-white transition-colors">志愿者活动</RouterLink>
              </li>
              <li><RouterLink to="/resources" class="text-gray-400 hover:text-white transition-colors">资源需求</RouterLink></li>
              <li><RouterLink to="/projects" class="text-gray-400 hover:text-white transition-colors">公益项目</RouterLink></li>
              <li><RouterLink to="/news" class="text-gray-400 hover:text-white transition-colors">公益资讯</RouterLink></li>
              <li><RouterLink to="/organizations" class="text-gray-400 hover:text-white transition-colors">公益组织</RouterLink></li>
              <li><RouterLink to="/profile" class="text-gray-400 hover:text-white transition-colors">个人中心</RouterLink></li>
            </ul>
          </div>

          <div>
            <h3 class="text-lg font-bold mb-6">帮助中心</h3>
            <ul class="space-y-3">
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">常见问题</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">捐赠指南</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">志愿者申请</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">组织入驻</a></li>
              <li><a href="#" class="text-gray-400 hover:text-white transition-colors">联系我们</a></li>
            </ul>
          </div>

          <div>
            <h3 class="text-lg font-bold mb-6">联系方式</h3>
            <ul class="space-y-3">
              <li class="flex items-start">
                <i class="fa-solid fa-location-dot text-primary mt-1 mr-3"></i>
                <span class="text-gray-400">北京市朝阳区建国路88号爱心大厦15层</span>
              </li>
              <li class="flex items-center">
                <i class="fa-solid fa-phone text-primary mr-3"></i>
                <span class="text-gray-400">400-123-4567</span>
              </li>
              <li class="flex items-center">
                <i class="fa-solid fa-envelope text-primary mr-3"></i>
                <span class="text-gray-400">contact@aixiny公益.org</span>
              </li>
              <li class="flex items-center">
                <i class="fa-regular fa-clock text-primary mr-3"></i>
                <span class="text-gray-400">周一至周五 9:00-18:00</span>
              </li>
            </ul>
          </div>
        </div>

        <hr class="border-gray-800 mb-8" />

        <div class="text-center text-gray-500 text-sm">
          <p>&copy; 2026 爱心公益平台 版权所有</p>
        </div>
      </div>
    </footer>
  </div>
</template>
