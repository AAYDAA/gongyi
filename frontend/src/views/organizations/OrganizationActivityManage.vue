<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { activityApi, type Activity, type ActivityApplication, type ActivityStatus } from '@/api/activity'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.userInfo?.role === 'ADMIN')

const loading = ref(false)
const list = ref<Activity[]>([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
})

const load = async () => {
  loading.value = true
  try {
    const res = isAdmin.value
      ? await activityApi.page({ page: query.page - 1, size: query.size })
      : await activityApi.my(query.page - 1, query.size)
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

const createDialog = ref({ open: false })
const creating = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  title: '',
  coverImage: '',
  location: '',
  contentHtml: '',
  startTime: '',
  endTime: '',
  deadlineTime: '',
  status: 'DRAFT' as ActivityStatus,
})

const rules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
}

const openCreate = () => {
  createDialog.value.open = true
  form.title = ''
  form.coverImage = ''
  form.location = ''
  form.contentHtml = ''
  form.startTime = ''
  form.endTime = ''
  form.deadlineTime = ''
  form.status = 'DRAFT'
}

const create = async () => {
  await formRef.value?.validate()
  creating.value = true
  try {
    await activityApi.create({
      title: form.title,
      coverImage: form.coverImage || undefined,
      location: form.location || undefined,
      contentHtml: form.contentHtml || undefined,
      startTime: form.startTime || undefined,
      endTime: form.endTime || undefined,
      deadlineTime: form.deadlineTime || undefined,
      status: form.status,
    })
    ElMessage.success('已创建')
    createDialog.value.open = false
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '创建失败')
  } finally {
    creating.value = false
  }
}

const setStatus = async (row: Activity, status: ActivityStatus) => {
  try {
    await activityApi.update(row.id, { status })
    ElMessage.success('已更新状态')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const appsDialog = ref({
  open: false,
  activityId: 0,
  title: '',
  page: 1,
  size: 10,
})
const appsLoading = ref(false)
const appsList = ref<ActivityApplication[]>([])
const appsTotal = ref(0)

const loadApps = async () => {
  appsLoading.value = true
  try {
    const res = await activityApi.applications(appsDialog.value.activityId, { page: appsDialog.value.page - 1, size: appsDialog.value.size })
    appsList.value = res.content
    appsTotal.value = res.totalElements
  } finally {
    appsLoading.value = false
  }
}

const reviewApplication = async (row: ActivityApplication, status: 'APPROVED' | 'REJECTED') => {
  try {
    await activityApi.reviewApplication(row.id, status)
    ElMessage.success(status === 'APPROVED' ? '已通过报名' : '已驳回报名')
    await loadApps()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const openApps = async (row: Activity) => {
  appsDialog.value.open = true
  appsDialog.value.activityId = row.id
  appsDialog.value.title = row.title
  appsDialog.value.page = 1
  await loadApps()
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<template>
  <div class="bg-gray-50 py-10">
    <div class="container mx-auto px-4">
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-4 mb-6">
          <div>
            <div class="text-2xl font-bold text-dark mb-1">{{ isAdmin ? '志愿活动管理' : '我发布的志愿活动' }}</div>
            <div class="text-gray-600 text-sm">{{ isAdmin ? '管理所有活动并查看志愿者报名资料' : '创建活动并查看志愿者报名资料' }}</div>
          </div>
          <div class="flex flex-col sm:flex-row gap-3">
            <button class="btn-secondary px-5 py-2" @click="openCreate"><i class="fa-solid fa-plus mr-2"></i>新建活动</button>
            <button class="btn-primary px-5 py-2" @click="load"><i class="fa-solid fa-rotate mr-2"></i>刷新</button>
          </div>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="title" label="标题" min-width="240" />
          <el-table-column prop="location" label="地点" min-width="160" />
          <el-table-column prop="status" label="状态" width="130" />
          <el-table-column prop="startTime" label="开始时间" min-width="180" />
          <el-table-column prop="deadlineTime" label="报名截止" min-width="180" />
          <el-table-column label="操作" width="420">
            <template #default="{ row }">
              <div class="flex gap-2 flex-wrap">
                <el-button size="small" type="info" @click="setStatus(row, 'DRAFT')">草稿</el-button>
                <el-button size="small" type="success" @click="setStatus(row, 'RECRUITING')">招募中</el-button>
                <el-button size="small" type="warning" @click="setStatus(row, 'ONGOING')">进行中</el-button>
                <el-button size="small" type="primary" @click="setStatus(row, 'ENDED')">已结束</el-button>
                <el-button size="small" @click="openApps(row)">报名资料</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="mt-6 flex justify-center">
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
    </div>

    <el-dialog v-model="createDialog.open" title="新建志愿活动" width="620px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="封面图 URL">
          <el-input v-model="form.coverImage" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="form.location" placeholder="如：北京市朝阳区/线上" />
        </el-form-item>
        <el-form-item label="开始时间（ISO 字符串，可选）">
          <el-input v-model="form.startTime" placeholder="如：2026-03-11T09:00:00" />
        </el-form-item>
        <el-form-item label="结束时间（ISO 字符串，可选）">
          <el-input v-model="form.endTime" placeholder="如：2026-03-11T18:00:00" />
        </el-form-item>
        <el-form-item label="报名截止（ISO 字符串，可选）">
          <el-input v-model="form.deadlineTime" placeholder="如：2026-03-10T18:00:00" />
        </el-form-item>
        <el-form-item label="详情 HTML（可选）">
          <el-input v-model="form.contentHtml" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" class="w-full">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="招募中" value="RECRUITING" />
            <el-option label="进行中" value="ONGOING" />
            <el-option label="已结束" value="ENDED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="createDialog.open = false">取消</el-button>
          <el-button type="primary" :loading="creating" @click="create">创建</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="appsDialog.open" :title="`报名资料 - ${appsDialog.title}`" width="760px">
      <el-table :data="appsList" v-loading="appsLoading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="volunteer.username" label="志愿者" width="160">
          <template #default="{ row }">
            <span>{{ row.volunteer?.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="140" />
        <el-table-column prop="applyTime" label="报名时间" min-width="200" />
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="审核" width="180">
          <template #default="{ row }">
            <div class="flex gap-2">
              <el-button size="small" type="success" :disabled="row.status !== 'PENDING'" @click="reviewApplication(row, 'APPROVED')">通过</el-button>
              <el-button size="small" type="danger" :disabled="row.status !== 'PENDING'" @click="reviewApplication(row, 'REJECTED')">驳回</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-5 flex justify-center">
        <el-pagination
          background
          layout="prev, pager, next"
          :page-size="appsDialog.size"
          :total="appsTotal"
          v-model:current-page="appsDialog.page"
          @current-change="loadApps"
        />
      </div>
    </el-dialog>
  </div>
</template>
