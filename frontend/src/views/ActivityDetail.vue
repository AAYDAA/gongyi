<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { activityApi, type Activity } from '@/api/activity'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const id = computed(() => Number(route.params.id))
const loading = ref(false)
const data = ref<Activity | null>(null)
const applying = ref(false)
const applied = ref(false)

const canApply = computed(() => {
  if (!userStore.token) return false
  if (userStore.userInfo?.role !== 'VOLUNTEER') return false
  if (!data.value) return false
  if (data.value.status !== 'RECRUITING') return false
  if (applied.value) return false
  return true
})

const load = async () => {
  loading.value = true
  try {
    data.value = await activityApi.detail(id.value)
  } finally {
    loading.value = false
  }
}

const onApply = async () => {
  if (!userStore.token) {
    await router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!canApply.value) return
  applying.value = true
  try {
    await activityApi.apply(id.value)
    applied.value = true
    ElMessage.success('报名已提交，等待审核')
  } catch (e: any) {
    ElMessage.error(e?.message || '报名失败')
  } finally {
    applying.value = false
  }
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<template>
  <div class="bg-gray-50 py-10">
    <div class="container mx-auto px-4">
      <el-skeleton v-if="loading" :rows="10" animated />

      <div v-else-if="data" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2">
          <div class="bg-white rounded-xl shadow-md overflow-hidden">
            <img
              :src="data.coverImage || 'https://images.unsplash.com/photo-1516534775068-ba3e7458af70?auto=format&fit=crop&w=1200&q=80'"
              class="w-full h-72 object-cover"
            />
            <div class="p-6">
              <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
                <h1 class="text-2xl md:text-3xl font-bold text-dark">{{ data.title }}</h1>
                <span class="bg-secondary text-white text-xs px-3 py-1 rounded-full w-fit">
                  {{ data.status === 'RECRUITING' ? '招募中' : data.status === 'ONGOING' ? '进行中' : data.status === 'ENDED' ? '已结束' : '草稿' }}
                </span>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-3 text-gray-600 mb-6">
                <div class="flex items-center">
                  <i class="fa-solid fa-location-dot text-secondary mr-2"></i>
                  <span>{{ data.location || '未填写地点' }}</span>
                </div>
                <div class="flex items-center">
                  <i class="fa-regular fa-user text-secondary mr-2"></i>
                  <span>创建人：{{ data.creator?.username }}</span>
                </div>
                <div class="flex items-center">
                  <i class="fa-regular fa-calendar text-secondary mr-2"></i>
                  <span>开始：{{ data.startTime || '-' }}</span>
                </div>
                <div class="flex items-center">
                  <i class="fa-regular fa-calendar-check text-secondary mr-2"></i>
                  <span>结束：{{ data.endTime || '-' }}</span>
                </div>
              </div>

              <div class="prose max-w-none" v-if="data.contentHtml" v-html="data.contentHtml"></div>
              <div v-else class="text-gray-500">暂无详情内容</div>
            </div>
          </div>
        </div>

        <div class="lg:col-span-1">
          <div class="bg-white rounded-xl shadow-md p-6 sticky top-24">
            <div class="text-lg font-bold text-dark mb-4">参与活动</div>

            <div class="text-gray-600 text-sm mb-6">
              <div class="mb-2">报名截止：{{ data.deadlineTime || '-' }}</div>
              <div>状态：{{ data.status }}</div>
            </div>

            <button
              v-if="userStore.token"
              class="btn-primary w-full"
              :disabled="!canApply || applying"
              @click="onApply"
            >
              <i class="fa-solid fa-bolt mr-2"></i>
              {{ applied ? '已报名' : applying ? '提交中...' : canApply ? '立即报名' : '不可报名' }}
            </button>

            <button v-else class="btn-primary w-full" @click="onApply">
              <i class="fa-solid fa-right-to-bracket mr-2"></i>登录后报名
            </button>

            <div class="mt-4 text-xs text-gray-500">
              仅志愿者角色可报名；报名后需审核通过才算成功。
            </div>
          </div>
        </div>
      </div>

      <div v-else class="bg-white rounded-xl shadow-md p-10 text-center text-gray-600">
        未找到活动
      </div>
    </div>
  </div>
</template>

