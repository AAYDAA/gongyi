<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { organizationApi, type OrganizationProfile } from '@/api/organization'

const router = useRouter()

const loading = ref(false)
const list = ref<OrganizationProfile[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  page: 1,
  size: 12,
})

const pageParams = computed(() => ({
  keyword: query.keyword || undefined,
  page: query.page - 1,
  size: query.size,
}))

const load = async () => {
  loading.value = true
  try {
    const res = await organizationApi.page(pageParams.value)
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
            <div class="text-2xl font-bold text-dark mb-2">公益组织</div>
            <div class="text-gray-600">查看已认证的公益组织并了解其项目</div>
          </div>

          <div class="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
            <el-input v-model="query.keyword" size="small" placeholder="搜索组织名称/简介/地址" clearable class="sm:w-80" @keyup.enter="onSearch" />
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
            @click="router.push(`/organizations/${item.id}`)"
          >
            <div class="p-6">
              <div class="flex items-center gap-4 mb-4">
                <img :src="item.logoUrl || 'https://via.placeholder.com/64'" class="w-14 h-14 rounded-lg object-cover" />
                <div class="min-w-0">
                  <div class="text-lg font-bold text-dark line-clamp-1">{{ item.name }}</div>
                  <div class="text-sm text-gray-600 line-clamp-1">{{ item.address || '未填写地址' }}</div>
                </div>
              </div>
              <p class="text-gray-600 mb-4 line-clamp-2">{{ item.intro || '暂无简介' }}</p>
              <div class="flex justify-between items-center text-sm text-gray-600">
                <span><i class="fa-solid fa-phone mr-2 text-secondary"></i>{{ item.contactPhone || '-' }}</span>
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
