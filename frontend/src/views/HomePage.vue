<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { activityApi, type Activity } from '@/api/activity'
import { resourceApi, type Resource } from '@/api/resource'
import { projectApi, type CharityProject } from '@/api/project'
import { newsApi, type NewsArticle } from '@/api/news'
import { organizationApi, type OrganizationProfile } from '@/api/organization'

const hotActivities = ref<Activity[]>([])
const latestResources = ref<Resource[]>([])
const hotProjects = ref<CharityProject[]>([])
const latestNews = ref<NewsArticle[]>([])
const hotOrganizations = ref<OrganizationProfile[]>([])

const stats = ref({
  activities: 0,
  resources: 0,
  volunteers: 0,
  organizations: 0,
})

const load = async () => {
  const [actPage, resPage, projectPage, newsPage, orgPage] = await Promise.all([
    activityApi.page({ page: 0, size: 6, status: 'RECRUITING' }),
    resourceApi.page({ page: 0, size: 6, status: 'REQUESTING' }),
    projectApi.page({ page: 0, size: 6, status: 'ACTIVE' }),
    newsApi.page({ page: 0, size: 6 }),
    organizationApi.page({ page: 0, size: 6 }),
  ])
  hotActivities.value = actPage.content
  latestResources.value = resPage.content
  hotProjects.value = projectPage.content
  latestNews.value = newsPage.content
  hotOrganizations.value = orgPage.content
  stats.value.activities = actPage.totalElements
  stats.value.resources = resPage.totalElements
  stats.value.organizations = orgPage.totalElements
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<template>
  <div>
    <section class="relative bg-gradient-to-r from-blue-500 to-blue-700 text-white py-20 md:py-32 overflow-hidden">
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute -right-20 -top-20 w-96 h-96 bg-white/10 rounded-full"></div>
        <div class="absolute -left-20 -bottom-20 w-80 h-80 bg-white/10 rounded-full"></div>
        <div class="absolute right-1/4 bottom-1/4 w-40 h-40 bg-white/10 rounded-full animate-float"></div>
      </div>
      <div class="container mx-auto px-4 relative z-10">
        <div class="max-w-3xl" data-aos="fade-right" data-aos-duration="1000">
          <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 text-shadow">让爱心传递，<br />让世界更美好</h1>
          <p class="text-xl md:text-2xl mb-8 text-blue-100">
            加入我们的公益平台，一起为需要帮助的人提供支持，<br />参与志愿服务，共创美好未来。
          </p>
          <div class="flex flex-col sm:flex-row gap-4">
            <RouterLink to="/resources" class="btn-secondary text-center">
              <i class="fa-solid fa-heart mr-2"></i>支持资源需求
            </RouterLink>
            <RouterLink to="/activities" class="btn-accent text-center">
              <i class="fa-solid fa-users mr-2"></i>参与志愿活动
            </RouterLink>
          </div>
        </div>
      </div>
    </section>

    <section class="py-12 bg-white">
      <div class="container mx-auto px-4">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-6 text-center">
          <div class="p-6 rounded-lg" data-aos="fade-up" data-aos-delay="100">
            <div class="text-4xl md:text-5xl font-bold text-primary mb-2">{{ stats.activities }}</div>
            <p class="text-gray-600">志愿活动</p>
          </div>
          <div class="p-6 rounded-lg" data-aos="fade-up" data-aos-delay="200">
            <div class="text-4xl md:text-5xl font-bold text-secondary mb-2">{{ stats.resources }}</div>
            <p class="text-gray-600">资源需求</p>
          </div>
          <div class="p-6 rounded-lg" data-aos="fade-up" data-aos-delay="300">
            <div class="text-4xl md:text-5xl font-bold text-accent mb-2">{{ stats.volunteers }}</div>
            <p class="text-gray-600">志愿者</p>
          </div>
          <div class="p-6 rounded-lg" data-aos="fade-up" data-aos-delay="400">
            <div class="text-4xl md:text-5xl font-bold text-blue-600 mb-2">{{ stats.organizations }}</div>
            <p class="text-gray-600">公益组织</p>
          </div>
        </div>
      </div>
    </section>

    <section class="py-16 bg-gray-50">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl md:text-4xl font-bold text-dark mb-4" data-aos="fade-up">热门活动</h2>
          <p class="text-gray-600 max-w-2xl mx-auto" data-aos="fade-up" data-aos-delay="100">选择你感兴趣的志愿活动，在线报名参与。</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <RouterLink
            v-for="item in hotActivities"
            :key="item.id"
            :to="`/activities/${item.id}`"
            class="bg-white rounded-xl shadow-md overflow-hidden card-hover block"
            data-aos="fade-up"
          >
            <div class="relative">
              <img :src="item.coverImage || 'https://images.unsplash.com/photo-1516534775068-ba3e7458af70?auto=format&fit=crop&w=1000&q=80'" class="w-full h-48 object-cover" />
              <span class="absolute top-4 right-4 bg-secondary text-white text-xs px-2 py-1 rounded-full">招募中</span>
            </div>
            <div class="p-6">
              <h3 class="text-xl font-bold text-dark mb-2">{{ item.title }}</h3>
              <div class="flex items-center text-sm text-gray-600 mb-3">
                <i class="fa-solid fa-location-dot mr-2 text-secondary"></i>
                <span>{{ item.location || '线上/线下' }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-sm text-gray-600">发布人：{{ item.creator?.username }}</span>
                <span class="text-primary text-sm font-medium">查看详情</span>
              </div>
            </div>
          </RouterLink>
        </div>

        <div class="text-center mt-12">
          <RouterLink to="/activities" class="btn-primary"><i class="fa-solid fa-arrow-right mr-2"></i>查看更多活动</RouterLink>
        </div>
      </div>
    </section>

    <section class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl md:text-4xl font-bold text-dark mb-4" data-aos="fade-up">最新资源需求</h2>
          <p class="text-gray-600 max-w-2xl mx-auto" data-aos="fade-up" data-aos-delay="100">志愿者可认领资源需求，帮助对接落地。</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div v-for="item in latestResources" :key="item.id" class="bg-white rounded-xl shadow-md overflow-hidden card-hover" data-aos="fade-up">
            <div class="p-6">
              <div class="flex items-start justify-between gap-3">
                <h3 class="text-xl font-bold text-dark mb-2">{{ item.title }}</h3>
                <span class="bg-primary/10 text-primary text-xs px-2 py-1 rounded-full">需求中</span>
              </div>
              <p class="text-gray-600 mb-4 line-clamp-2">{{ item.description || '暂无描述' }}</p>
              <div class="flex justify-between items-center">
                <span class="text-sm text-gray-600">发布人：{{ item.publisher?.username }}</span>
                <RouterLink to="/resources" class="text-primary hover:underline text-sm">去认领</RouterLink>
              </div>
            </div>
          </div>
        </div>

        <div class="text-center mt-12">
          <RouterLink to="/resources" class="btn-primary"><i class="fa-solid fa-arrow-right mr-2"></i>查看更多资源</RouterLink>
        </div>
      </div>
    </section>

    <section class="py-16 bg-gray-50">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl md:text-4xl font-bold text-dark mb-4" data-aos="fade-up">公益项目</h2>
          <p class="text-gray-600 max-w-2xl mx-auto" data-aos="fade-up" data-aos-delay="100">选择你想支持的项目，在线捐款，记录公开透明。</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <RouterLink
            v-for="item in hotProjects"
            :key="item.id"
            :to="`/projects/${item.id}`"
            class="bg-white rounded-xl shadow-md overflow-hidden card-hover block"
            data-aos="fade-up"
          >
            <div class="relative">
              <img
                :src="item.coverImage || 'https://images.unsplash.com/photo-1459183885421-5cc683b8dbba?auto=format&fit=crop&w=1200&q=80'"
                class="w-full h-48 object-cover"
              />
              <span class="absolute top-4 right-4 bg-primary text-white text-xs px-2 py-1 rounded-full">项目</span>
            </div>
            <div class="p-6">
              <h3 class="text-xl font-bold text-dark mb-2 line-clamp-1">{{ item.title }}</h3>
              <p class="text-gray-600 mb-4 line-clamp-2">{{ item.summary || '暂无简介' }}</p>
              <div class="flex justify-between items-center text-sm text-gray-600">
                <span>发起方：{{ item.creator?.username }}</span>
                <span class="text-primary font-medium">去捐款</span>
              </div>
            </div>
          </RouterLink>
        </div>

        <div class="text-center mt-12">
          <RouterLink to="/projects" class="btn-primary"><i class="fa-solid fa-arrow-right mr-2"></i>查看更多项目</RouterLink>
        </div>
      </div>
    </section>

    <section class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl md:text-4xl font-bold text-dark mb-4" data-aos="fade-up">公益资讯</h2>
          <p class="text-gray-600 max-w-2xl mx-auto" data-aos="fade-up" data-aos-delay="100">平台公告、公益动态与项目进展，持续更新。</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <RouterLink
            v-for="item in latestNews"
            :key="item.id"
            :to="`/news/${item.id}`"
            class="bg-white rounded-xl shadow-md overflow-hidden card-hover block"
            data-aos="fade-up"
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
              <div class="flex justify-between items-center text-sm text-gray-600">
                <span>作者：{{ item.author?.username || '平台' }}</span>
                <span class="text-primary font-medium">阅读</span>
              </div>
            </div>
          </RouterLink>
        </div>

        <div class="text-center mt-12">
          <RouterLink to="/news" class="btn-primary"><i class="fa-solid fa-arrow-right mr-2"></i>查看更多资讯</RouterLink>
        </div>
      </div>
    </section>

    <section class="py-16 bg-gray-50">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl md:text-4xl font-bold text-dark mb-4" data-aos="fade-up">公益组织</h2>
          <p class="text-gray-600 max-w-2xl mx-auto" data-aos="fade-up" data-aos-delay="100">已认证的公益组织，信息公开透明。</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <RouterLink
            v-for="item in hotOrganizations"
            :key="item.id"
            :to="`/organizations/${item.id}`"
            class="bg-white rounded-xl shadow-md overflow-hidden card-hover block"
            data-aos="fade-up"
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
                <span class="text-primary font-medium">查看</span>
              </div>
            </div>
          </RouterLink>
        </div>

        <div class="text-center mt-12">
          <RouterLink to="/organizations" class="btn-primary"><i class="fa-solid fa-arrow-right mr-2"></i>查看更多组织</RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>
