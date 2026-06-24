import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'home', component: () => import('@/views/HomePage.vue') },
  { path: '/activities', name: 'activities', component: () => import('@/views/ActivityList.vue') },
  { path: '/activities/:id', name: 'activityDetail', component: () => import('@/views/ActivityDetail.vue') },
  { path: '/resources', name: 'resources', component: () => import('@/views/ResourceList.vue') },
  { path: '/projects', name: 'projects', component: () => import('@/views/projects/ProjectList.vue') },
  { path: '/projects/:id', name: 'projectDetail', component: () => import('@/views/projects/ProjectDetail.vue') },
  { path: '/news', name: 'news', component: () => import('@/views/news/NewsList.vue') },
  { path: '/news/:id', name: 'newsDetail', component: () => import('@/views/news/NewsDetail.vue') },
  { path: '/organizations', name: 'organizations', component: () => import('@/views/organizations/OrganizationList.vue') },
  { path: '/organizations/:id', name: 'organizationDetail', component: () => import('@/views/organizations/OrganizationDetail.vue') },
  { path: '/login', name: 'login', component: () => import('@/views/UserLogin.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/UserRegister.vue') },
  { path: '/profile', name: 'profile', meta: { requiresAuth: true }, component: () => import('@/views/UserProfile.vue') },
  {
    path: '/volunteer/apply',
    name: 'volunteerApply',
    meta: { requiresAuth: true, roles: ['USER', 'VOLUNTEER'] },
    component: () => import('@/views/volunteers/VolunteerApplication.vue'),
  },
  {
    path: '/donations/my',
    name: 'myDonations',
    meta: { requiresAuth: true },
    component: () => import('@/views/donations/MyDonations.vue'),
  },
  {
    path: '/resources/my-claims',
    name: 'myResourceClaims',
    meta: { requiresAuth: true, roles: ['VOLUNTEER'] },
    component: () => import('@/views/resources/MyResourceClaims.vue'),
  },
  {
    path: '/org/center',
    name: 'orgCenter',
    meta: { requiresAuth: true, roles: ['ORGANIZATION'] },
    component: () => import('@/views/organizations/OrganizationCenter.vue'),
  },
  {
    path: '/org/projects',
    name: 'orgProjects',
    meta: { requiresAuth: true, roles: ['ORGANIZATION'] },
    component: () => import('@/views/organizations/OrganizationProjectManage.vue'),
  },
  {
    path: '/org/activities',
    name: 'orgActivities',
    meta: { requiresAuth: true, roles: ['ORGANIZATION'] },
    component: () => import('@/views/organizations/OrganizationActivityManage.vue'),
  },
  {
    path: '/admin',
    name: 'adminDashboard',
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    component: () => import('@/views/admin/AdminDashboard.vue'),
  },
  {
    path: '/admin/volunteer-verify',
    name: 'adminVolunteerVerify',
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    component: () => import('@/views/admin/AdminVolunteerVerify.vue'),
  },
  {
    path: '/admin/activity-manage',
    name: 'adminActivityManage',
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    component: () => import('@/views/organizations/OrganizationActivityManage.vue'),
  },
  {
    path: '/admin/org-verify',
    name: 'adminOrgVerify',
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    component: () => import('@/views/admin/AdminOrganizationVerify.vue'),
  },
  {
    path: '/admin/project-manage',
    name: 'adminProjectManage',
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    component: () => import('@/views/admin/AdminProjectManage.vue'),
  },
  {
    path: '/admin/news-manage',
    name: 'adminNewsManage',
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    component: () => import('@/views/admin/AdminNewsManage.vue'),
  },
  {
    path: '/admin/resource-claim-verify',
    name: 'adminResourceClaimVerify',
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    component: () => import('@/views/admin/AdminResourceClaimVerify.vue'),
  },
  {
    path: '/admin/resource-manage',
    name: 'adminResourceManage',
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    component: () => import('@/views/admin/AdminResourceManage.vue'),
  },
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.roles && userStore.userInfo?.role) {
    const roles = to.meta.roles as string[]
    if (!roles.includes(userStore.userInfo.role)) {
      return { path: '/' }
    }
  }
  return true
})

export default router
