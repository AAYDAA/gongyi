<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { activityApi, type Activity, type MyActivityApplication } from '@/api/activity'

const userStore = useUserStore()
const router = useRouter()

const loading = ref(false)
const myActivities = ref<Activity[]>([])
const myAppliedActivities = ref<MyActivityApplication[]>([])
const myAppliedTotal = ref(0)
const myAppliedPage = ref(1)
const myAppliedSize = ref(6)
const myRecordPage = ref(1)
const myRecordSize = ref(6)
const activeTab = ref<'profile' | 'my' | 'record'>('profile')

const isOrg = computed(() => userStore.userInfo?.role === 'ORGANIZATION' || userStore.userInfo?.role === 'ADMIN')
const isUser = computed(() => userStore.userInfo?.role === 'USER')
const isVolunteer = computed(() => userStore.userInfo?.role === 'VOLUNTEER')

const load = async () => {
  loading.value = true
  try {
    const me = await userApi.me()
    if (userStore.token) userStore.setAuth(userStore.token, me)
    if (isVolunteer.value) {
      const page = await activityApi.myApplied(myAppliedPage.value - 1, myAppliedSize.value)
      myAppliedActivities.value = page.content
      myAppliedTotal.value = page.totalElements
      myActivities.value = []
      return
    }
    if (isOrg.value) {
      const page = await activityApi.my(0, 10)
      myActivities.value = page.content
      myAppliedActivities.value = []
      myAppliedTotal.value = 0
    } else {
      myActivities.value = []
      myAppliedActivities.value = []
      myAppliedTotal.value = 0
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  load().catch(() => {})
})

const statusText = (s: MyActivityApplication['status']) => {
  if (s === 'PENDING') return '待审核'
  if (s === 'APPROVED') return '已通过'
  if (s === 'REJECTED') return '已驳回'
  if (s === 'CANCELLED') return '已取消'
  return s
}

const onMyAppliedPageChange = async (page: number) => {
  myAppliedPage.value = page
  await load()
}

const myVolunteerRecords = computed(() => myAppliedActivities.value.filter((item) => item.status === 'APPROVED'))
const myVolunteerRecordTotal = computed(() => myVolunteerRecords.value.length)
const myVolunteerRecordPaged = computed(() => {
  const start = (myRecordPage.value - 1) * myRecordSize.value
  return myVolunteerRecords.value.slice(start, start + myRecordSize.value)
})

const onMyRecordPageChange = (page: number) => {
  myRecordPage.value = page
}

const cancelMyApplication = async (row: MyActivityApplication) => {
  try {
    await ElMessageBox.confirm('确认取消该活动报名吗？', '取消报名', { type: 'warning' })
    await activityApi.cancelMyApplication(row.id)
    ElMessage.success('已取消报名')
    await load()
  } catch (e: any) {
    if (e === 'cancel' || e === 'close') return
    ElMessage.error(e?.message || '取消失败')
  }
}
</script>

<template>
  <div class="bg-gray-50 py-10">
    <div class="container mx-auto px-4">
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
          <div class="flex items-center gap-4">
            <img
              :src="userStore.userInfo?.avatarUrl || 'https://via.placeholder.com/80'"
              class="w-16 h-16 rounded-full object-cover"
            />
            <div>
              <div class="text-2xl font-bold text-dark">{{ userStore.userInfo?.username }}</div>
              <div class="text-gray-600 text-sm">
                <span class="mr-3">角色：{{ userStore.userInfo?.role }}</span>
                <span>状态：{{ userStore.userInfo?.status }}</span>
              </div>
            </div>
          </div>

          <div class="flex gap-3">
            <button
              v-if="isUser"
              class="btn-secondary px-5 py-2"
              @click="router.push('/volunteer/apply')"
            >
              <i class="fa-solid fa-user-check mr-2"></i>申请成为志愿者
            </button>
            <button class="btn-primary px-5 py-2" @click="load"><i class="fa-solid fa-rotate mr-2"></i>刷新</button>
          </div>
        </div>

        <el-tabs v-model="activeTab" class="mt-6">
          <el-tab-pane label="个人资料" name="profile">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-gray-700">
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="text-sm text-gray-500 mb-1">邮箱</div>
                <div class="font-medium">{{ userStore.userInfo?.email || '-' }}</div>
              </div>
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="text-sm text-gray-500 mb-1">手机号</div>
                <div class="font-medium">{{ userStore.userInfo?.phone || '-' }}</div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane :label="isOrg ? '我发布的活动' : isVolunteer ? '我报名的活动' : '我的活动'" name="my">
            <el-skeleton v-if="loading" :rows="6" animated />
            <div v-else-if="isOrg" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div v-for="item in myActivities" :key="item.id" class="bg-white border border-gray-100 rounded-xl shadow-sm p-5 card-hover">
                <div class="font-bold text-dark mb-2 line-clamp-1">{{ item.title }}</div>
                <div class="text-sm text-gray-600 mb-3">
                  <i class="fa-solid fa-location-dot mr-2 text-secondary"></i>{{ item.location || '未填写地点' }}
                </div>
                <div class="flex justify-between items-center text-sm">
                  <span class="text-gray-600">状态：{{ item.status }}</span>
                  <RouterLink :to="`/activities/${item.id}`" class="text-primary hover:underline">查看</RouterLink>
                </div>
              </div>
            </div>
            <div v-else-if="isVolunteer">
              <el-table :data="myAppliedActivities" stripe>
                <el-table-column prop="activityTitle" label="活动" min-width="220" />
                <el-table-column prop="activityLocation" label="地点" min-width="160" />
                <el-table-column prop="applyTime" label="报名时间" min-width="180" />
                <el-table-column prop="status" label="状态" width="120">
                  <template #default="{ row }">
                    <span
                      class="px-2 py-1 rounded-full text-xs"
                      :class="
                        row.status === 'APPROVED'
                          ? 'bg-secondary/10 text-secondary'
                          : row.status === 'REJECTED'
                            ? 'bg-red-100 text-red-600'
                            : row.status === 'CANCELLED'
                              ? 'bg-gray-100 text-gray-600'
                              : 'bg-accent/10 text-accent'
                      "
                    >
                      {{ statusText(row.status) }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="留言" min-width="180" />
                <el-table-column label="操作" width="200">
                  <template #default="{ row }">
                    <div class="flex items-center gap-3">
                      <RouterLink :to="`/activities/${row.activityId}`" class="text-primary hover:underline">查看</RouterLink>
                      <el-button
                        size="small"
                        type="danger"
                        link
                        :disabled="row.status !== 'PENDING' && row.status !== 'APPROVED'"
                        @click="cancelMyApplication(row)"
                      >
                        取消报名
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div class="mt-6 flex justify-center">
                <el-pagination
                  background
                  layout="prev, pager, next, jumper"
                  :page-size="myAppliedSize"
                  :total="myAppliedTotal"
                  v-model:current-page="myAppliedPage"
                  @current-change="onMyAppliedPageChange"
                />
              </div>
            </div>
            <div v-else class="text-gray-600">你可以在活动详情页报名活动，通过“申请成为志愿者”审核后可在这里查看报名记录。</div>
          </el-tab-pane>
          <el-tab-pane v-if="isVolunteer" label="志愿活动记录" name="record">
            <el-table :data="myVolunteerRecordPaged" stripe>
              <el-table-column prop="activityTitle" label="活动" min-width="220" />
              <el-table-column prop="activityLocation" label="地点" min-width="160" />
              <el-table-column prop="activityStartTime" label="开始时间" min-width="180" />
              <el-table-column prop="activityEndTime" label="结束时间" min-width="180" />
              <el-table-column prop="applyTime" label="报名时间" min-width="180" />
              <el-table-column label="记录状态" width="120">
                <template #default>
                  <span class="px-2 py-1 rounded-full text-xs bg-secondary/10 text-secondary">已参与记录</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <RouterLink :to="`/activities/${row.activityId}`" class="text-primary hover:underline">查看</RouterLink>
                </template>
              </el-table-column>
            </el-table>
            <div class="mt-6 flex justify-center">
              <el-pagination
                background
                layout="prev, pager, next, jumper"
                :page-size="myRecordSize"
                :total="myVolunteerRecordTotal"
                v-model:current-page="myRecordPage"
                @current-change="onMyRecordPageChange"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>
