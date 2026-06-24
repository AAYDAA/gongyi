<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { activityApi, type Activity, type ActivityStatus } from '@/api/activity'

const router = useRouter()

const loading = ref(false)
const list = ref<Activity[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  location: '',
  status: '' as '' | ActivityStatus,
  page: 1,
  size: 9,
})

const pageParams = computed(() => ({
  keyword: query.keyword || undefined,
  location: query.location || undefined,
  status: query.status || undefined,
  page: query.page - 1,
  size: query.size,
}))

const load = async () => {
  loading.value = true
  try {
    const res = await activityApi.page(pageParams.value)
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
            <div class="text-2xl font-bold text-dark mb-2">志愿者活动</div>
            <div class="text-gray-600">按标题/地点搜索，支持状态筛选与分页</div>
          </div>

          <div class="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
            <el-input v-model="query.keyword" size="small" placeholder="搜索标题/内容关键词" clearable class="sm:w-64" @keyup.enter="onSearch" />
            <el-input v-model="query.location" size="small" placeholder="搜索地点" clearable class="sm:w-48" @keyup.enter="onSearch" />
            <el-select v-model="query.status" size="small" placeholder="状态" clearable class="sm:w-40" @change="onSearch">
              <el-option label="草稿" value="DRAFT" />
              <el-option label="招募中" value="RECRUITING" />
              <el-option label="进行中" value="ONGOING" />
              <el-option label="已结束" value="ENDED" />
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
            @click="router.push(`/activities/${item.id}`)"
          >
            <div class="relative">
              <img :src="item.coverImage || 'https://images.unsplash.com/photo-1516534775068-ba3e7458af70?auto=format&fit=crop&w=1000&q=80'" class="w-full h-48 object-cover" />
              <span class="absolute top-4 right-4 bg-secondary text-white text-xs px-2 py-1 rounded-full">
                {{ item.status === 'RECRUITING' ? '招募中' : item.status === 'ONGOING' ? '进行中' : item.status === 'ENDED' ? '已结束' : '草稿' }}
              </span>
            </div>
            <div class="p-6">
              <h3 class="text-xl font-bold text-dark mb-2 line-clamp-1">{{ item.title }}</h3>
              <div class="flex items-center text-sm text-gray-600 mb-2">
                <i class="fa-solid fa-location-dot mr-2 text-secondary"></i>
                <span class="line-clamp-1">{{ item.location || '未填写地点' }}</span>
              </div>
              <div class="flex justify-between items-center text-sm text-gray-600">
                <span>发布人：{{ item.creator?.username }}</span>
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
