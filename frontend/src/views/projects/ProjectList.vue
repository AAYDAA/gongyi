<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { projectApi, type CharityProject, type ProjectStatus } from '@/api/project'

const router = useRouter()

const loading = ref(false)
const list = ref<CharityProject[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  status: '' as '' | ProjectStatus,
  page: 1,
  size: 9,
})

const pageParams = computed(() => ({
  keyword: query.keyword || undefined,
  status: query.status || undefined,
  page: query.page - 1,
  size: query.size,
}))

const load = async () => {
  loading.value = true
  try {
    const res = await projectApi.page(pageParams.value)
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

const onSearch = async () => {
  query.page = 1
  await load()
}

const percent = (p: CharityProject) => {
  const goal = Number(p.goalAmount || 0)
  const raised = Number(p.raisedAmount || 0)
  if (goal <= 0) return 0
  return Math.min(100, Math.round((raised / goal) * 100))
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<template>
  <div class="bg-gray-50">
    <section class="py-10 bg-white">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row md:items-center gap-4">
          <div class="flex-1">
            <div class="text-2xl font-bold text-dark mb-2">公益项目</div>
            <div class="text-gray-600">浏览公益项目并在线捐款支持</div>
          </div>

          <div class="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
            <el-input v-model="query.keyword" size="small" placeholder="搜索项目标题/简介" clearable class="sm:w-72" @keyup.enter="onSearch" />
            <el-select v-model="query.status" size="small" placeholder="状态" clearable class="sm:w-40" @change="onSearch">
              <el-option label="草稿" value="DRAFT" />
              <el-option label="进行中" value="ACTIVE" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
            <button class="btn-primary btn-toolbar" @click="onSearch"><i class="fa-solid fa-magnifying-glass mr-1.5"></i>搜索</button>
          </div>
        </div>
      </div>
    </section>

    <section class="py-12">
      <div class="container mx-auto px-4">
        <el-skeleton v-if="loading" :rows="6" animated />

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div
            v-for="item in list"
            :key="item.id"
            class="bg-white rounded-xl shadow-md overflow-hidden card-hover cursor-pointer"
            data-aos="fade-up"
            @click="router.push(`/projects/${item.id}`)"
          >
            <div class="relative">
              <img
                :src="item.coverImage || 'https://images.unsplash.com/photo-1459183885421-5cc683b8dbba?auto=format&fit=crop&w=1200&q=80'"
                class="w-full h-48 object-cover"
              />
              <span
                class="absolute top-4 right-4 text-white text-xs px-3 py-1 rounded-full"
                :class="item.status === 'ACTIVE' ? 'bg-secondary' : item.status === 'COMPLETED' ? 'bg-primary' : 'bg-gray-600'"
              >
                {{ item.status === 'ACTIVE' ? '进行中' : item.status === 'COMPLETED' ? '已完成' : '草稿' }}
              </span>
            </div>
            <div class="p-6">
              <h3 class="text-xl font-bold text-dark mb-2 line-clamp-1">{{ item.title }}</h3>
              <p class="text-gray-600 mb-4 line-clamp-2">{{ item.summary || '暂无简介' }}</p>
              <div class="mb-3">
                <div class="flex justify-between text-sm text-gray-600 mb-2">
                  <span>进度</span>
                  <span class="font-medium text-primary">{{ percent(item) }}%</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-2">
                  <div class="bg-primary h-2 rounded-full" :style="{ width: percent(item) + '%' }"></div>
                </div>
              </div>
              <div class="flex justify-between items-center text-sm text-gray-600">
                <span>发起方：{{ item.creator?.username }}</span>
                <span class="text-primary font-medium">查看详情</span>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-10 flex justify-center">
          <el-pagination
            background
            layout="prev, pager, next, jumper"
            :page-size="query.size"
            :total="total"
            v-model:current-page="query.page"
            @current-change="load"
          />
        </div>
      </div>
    </section>
  </div>
</template>
