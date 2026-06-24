<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { newsApi, type NewsArticle } from '@/api/news'

const route = useRoute()
const id = computed(() => Number(route.params.id))

const loading = ref(false)
const data = ref<NewsArticle | null>(null)

const load = async () => {
  loading.value = true
  try {
    data.value = await newsApi.detail(id.value)
  } finally {
    loading.value = false
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

      <div v-else-if="data" class="bg-white rounded-xl shadow-md overflow-hidden">
        <img
          :src="data.coverImage || 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=1200&q=80'"
          class="w-full h-72 object-cover"
        />
        <div class="p-8">
          <div class="text-sm text-gray-500 mb-3">
            <span class="mr-4"><i class="fa-regular fa-calendar mr-2"></i>{{ data.publishTime || data.createTime || '-' }}</span>
            <span><i class="fa-regular fa-user mr-2"></i>{{ data.author?.username || '平台' }}</span>
          </div>
          <h1 class="text-2xl md:text-3xl font-bold text-dark mb-6">{{ data.title }}</h1>
          <div class="prose max-w-none" v-if="data.contentHtml" v-html="data.contentHtml"></div>
          <div v-else class="text-gray-500">暂无内容</div>
        </div>
      </div>

      <div v-else class="bg-white rounded-xl shadow-md p-10 text-center text-gray-600">未找到资讯</div>
    </div>
  </div>
</template>

