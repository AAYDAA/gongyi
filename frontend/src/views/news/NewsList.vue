<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { newsApi, type NewsArticle } from '@/api/news'

const router = useRouter()

const loading = ref(false)
const list = ref<NewsArticle[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  page: 1,
  size: 9,
})

const pageParams = computed(() => ({
  keyword: query.keyword || undefined,
  page: query.page - 1,
  size: query.size,
}))

const load = async () => {
  loading.value = true
  try {
    const res = await newsApi.page(pageParams.value)
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
            <div class="text-2xl font-bold text-dark mb-2">公益资讯</div>
            <div class="text-gray-600">了解公益动态、项目进展与平台公告</div>
          </div>

          <div class="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
            <el-input v-model="query.keyword" size="small" placeholder="搜索资讯标题/摘要" clearable class="sm:w-80" @keyup.enter="onSearch" />
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
            @click="router.push(`/news/${item.id}`)"
          >
            <img
              :src="item.coverImage || 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=1200&q=80'"
              class="w-full h-44 object-cover"
            />
            <div class="p-6">
              <div class="text-sm text-gray-500 mb-2">
                <i class="fa-regular fa-calendar mr-2"></i>{{ item.publishTime || item.createTime || '-' }}
              </div>
              <h3 class="text-xl font-bold text-dark mb-2 line-clamp-1">{{ item.title }}</h3>
              <p class="text-gray-600 mb-4 line-clamp-2">{{ item.summary || '暂无摘要' }}</p>
              <div class="flex justify-between items-center text-sm">
                <span class="text-gray-600">作者：{{ item.author?.username || '平台' }}</span>
                <span class="text-primary font-medium">阅读更多</span>
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
