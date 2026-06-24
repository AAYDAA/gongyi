<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { organizationApi, type OrganizationProfile } from '@/api/organization'
import { projectApi, type CharityProject } from '@/api/project'

const route = useRoute()
const id = computed(() => Number(route.params.id))

const loading = ref(false)
const data = ref<OrganizationProfile | null>(null)

const projectLoading = ref(false)
const projects = ref<CharityProject[]>([])

const licenseImages = computed(() => {
  const raw = data.value?.licenseImageUrls || ''
  return raw
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
})

const load = async () => {
  loading.value = true
  try {
    data.value = await organizationApi.detail(id.value)
  } finally {
    loading.value = false
  }
}

const loadProjects = async () => {
  if (!data.value) return
  projectLoading.value = true
  try {
    const page = await projectApi.page({ creatorId: data.value.userId, page: 0, size: 6 })
    projects.value = page.content
  } finally {
    projectLoading.value = false
  }
}

onMounted(() => {
  load()
    .then(loadProjects)
    .catch(() => {})
})
</script>

<template>
  <div class="bg-gray-50 py-10">
    <div class="container mx-auto px-4">
      <el-skeleton v-if="loading" :rows="10" animated />

      <div v-else-if="data" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2 space-y-6">
          <div class="bg-white rounded-xl shadow-md p-6">
            <div class="flex items-start gap-4">
              <img :src="data.logoUrl || 'https://via.placeholder.com/96'" class="w-20 h-20 rounded-xl object-cover" />
              <div class="min-w-0 flex-1">
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                  <div class="text-2xl font-bold text-dark">{{ data.name }}</div>
                  <span
                    class="text-xs px-3 py-1 rounded-full w-fit"
                    :class="
                      data.verifyStatus === 'APPROVED'
                        ? 'bg-secondary/10 text-secondary'
                        : data.verifyStatus === 'PENDING'
                          ? 'bg-accent/10 text-accent'
                          : data.verifyStatus === 'REJECTED'
                            ? 'bg-red-100 text-red-600'
                            : 'bg-gray-100 text-gray-600'
                    "
                  >
                    {{
                      data.verifyStatus === 'APPROVED'
                        ? '已认证'
                        : data.verifyStatus === 'PENDING'
                          ? '审核中'
                          : data.verifyStatus === 'REJECTED'
                            ? '未通过'
                            : '未提交'
                    }}
                  </span>
                </div>
                <div class="text-gray-600 mt-2">{{ data.intro || '暂无简介' }}</div>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-3 mt-5 text-gray-700 text-sm">
                  <div class="flex items-center">
                    <i class="fa-solid fa-location-dot text-secondary mr-2"></i>
                    <span class="line-clamp-1">{{ data.address || '未填写地址' }}</span>
                  </div>
                  <div class="flex items-center">
                    <i class="fa-solid fa-phone text-secondary mr-2"></i>
                    <span>{{ data.contactPhone || '-' }}</span>
                  </div>
                  <div class="flex items-center md:col-span-2">
                    <i class="fa-solid fa-globe text-secondary mr-2"></i>
                    <a v-if="data.website" :href="data.website" target="_blank" class="text-primary hover:underline line-clamp-1">{{ data.website }}</a>
                    <span v-else>-</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-xl shadow-md p-6">
            <div class="text-lg font-bold text-dark mb-4">相关项目</div>
            <el-skeleton v-if="projectLoading" :rows="4" animated />
            <div v-else-if="projects.length" class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <RouterLink
                v-for="p in projects"
                :key="p.id"
                :to="`/projects/${p.id}`"
                class="border border-gray-100 rounded-xl overflow-hidden card-hover block"
              >
                <img
                  :src="p.coverImage || 'https://images.unsplash.com/photo-1459183885421-5cc683b8dbba?auto=format&fit=crop&w=1200&q=80'"
                  class="w-full h-36 object-cover"
                />
                <div class="p-4">
                  <div class="font-bold text-dark mb-1 line-clamp-1">{{ p.title }}</div>
                  <div class="text-sm text-gray-600 line-clamp-2">{{ p.summary || '暂无简介' }}</div>
                </div>
              </RouterLink>
            </div>
            <div v-else class="text-gray-500">暂无项目</div>
          </div>
        </div>

        <div class="lg:col-span-1">
          <div class="bg-white rounded-xl shadow-md p-6 sticky top-24">
            <div class="text-lg font-bold text-dark mb-4">资质材料</div>
            <div v-if="licenseImages.length" class="grid grid-cols-2 gap-3">
              <a v-for="url in licenseImages" :key="url" :href="url" target="_blank" class="block">
                <img :src="url" class="w-full h-28 rounded-lg object-cover border border-gray-100" />
              </a>
            </div>
            <div v-else class="text-gray-500 text-sm">暂无材料</div>

            <div v-if="data.verifyRemark" class="mt-6 bg-gray-50 rounded-lg p-4 text-sm text-gray-700">
              <div class="font-medium mb-2">审核备注</div>
              <div>{{ data.verifyRemark }}</div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="bg-white rounded-xl shadow-md p-10 text-center text-gray-600">未找到组织</div>
    </div>
  </div>
</template>

