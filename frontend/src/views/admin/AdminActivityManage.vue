<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { activityApi, type Activity, type ActivityStatus } from '@/api/activity'

const loading = ref(false)
const list = ref<Activity[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const load = async () => {
  loading.value = true
  try {
    const res = await activityApi.page({ page: page.value - 1, size: size.value })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

const review = async (id: number, status: ActivityStatus) => {
  try {
    await activityApi.review(id, status)
    ElMessage.success('已更新状态')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<template>
  <div class="bg-gray-50 py-10">
    <div class="container mx-auto px-4">
      <div class="bg-white rounded-xl shadow-md p-6">
        <div class="flex items-center justify-between mb-6">
          <div>
            <div class="text-2xl font-bold text-dark mb-1">活动审核管理</div>
            <div class="text-gray-600 text-sm">管理员可快速调整活动状态</div>
          </div>
          <button class="btn-primary btn-toolbar" @click="load"><i class="fa-solid fa-rotate mr-1.5"></i>刷新</button>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="title" label="标题" min-width="220" />
          <el-table-column label="创建人" width="160">
            <template #default="{ row }">
              <span>{{ row.creator?.username }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="140" />
          <el-table-column label="操作" width="260">
            <template #default="{ row }">
              <div class="flex gap-2">
                <el-button size="small" type="success" @click="review(row.id, 'RECRUITING')">设为招募中</el-button>
                <el-button size="small" type="warning" @click="review(row.id, 'ONGOING')">设为进行中</el-button>
                <el-button size="small" type="info" @click="review(row.id, 'ENDED')">设为已结束</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="mt-6 flex justify-center">
          <el-pagination
            background
            layout="prev, pager, next, jumper"
            :page-size="size"
            :total="total"
            v-model:current-page="page"
            @current-change="load"
          />
        </div>
      </div>
    </div>
  </div>
</template>
