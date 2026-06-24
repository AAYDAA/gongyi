<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const adminMenus = [
  {
    title: '活动管理',
    desc: '查看并维护活动状态，管理活动报名信息。',
    icon: 'fa-calendar-check',
    path: '/admin/activity-manage',
  },
  {
    title: '项目管理',
    desc: '维护公益项目，调整项目进度与状态。',
    icon: 'fa-diagram-project',
    path: '/admin/project-manage',
  },
  {
    title: '资源管理',
    desc: '发布并维护资源需求，跟踪资源处理进度。',
    icon: 'fa-box-open',
    path: '/admin/resource-manage',
  },
  {
    title: '资讯管理',
    desc: '维护平台资讯内容，统一发布公告与动态。',
    icon: 'fa-newspaper',
    path: '/admin/news-manage',
  },
  {
    title: '资源审核',
    desc: '审核志愿者资源认领申请并执行审批。',
    icon: 'fa-clipboard-check',
    path: '/admin/resource-claim-verify',
  },
  {
    title: '志愿者审核',
    desc: '审核普通用户提交的志愿者申请资料。',
    icon: 'fa-user-check',
    path: '/admin/volunteer-verify',
  },
  {
    title: '组织审核',
    desc: '审核组织入驻资质与认证状态。',
    icon: 'fa-building-shield',
    path: '/admin/org-verify',
  },
]
</script>

<template>
  <div class="bg-gray-50 py-10">
    <div class="container mx-auto px-4">
      <div class="bg-white rounded-xl shadow-md p-6 md:p-8">
        <div class="mb-8">
          <div class="text-2xl font-bold text-dark mb-2">管理员管理中心</div>
          <div class="text-gray-600">将管理员高频操作统一收口到一个页面，点击卡片进入对应模块。</div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          <div
            v-for="item in adminMenus"
            :key="item.path"
            class="bg-gray-50 border border-gray-100 rounded-xl p-5 card-hover cursor-pointer"
            @click="router.push(item.path)"
          >
            <div class="flex items-center justify-between mb-3">
              <div class="text-lg font-bold text-dark">{{ item.title }}</div>
              <i :class="`fa-solid ${item.icon} text-primary text-lg`"></i>
            </div>
            <div class="text-gray-600 text-sm leading-6 mb-4">{{ item.desc }}</div>
            <button class="btn-primary btn-toolbar">
              进入模块 <i class="fa-solid fa-arrow-right ml-1.5"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
