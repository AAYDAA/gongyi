<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { resourceApi, type ResourceClaim } from '@/api/resource'

const loading = ref(false)
const list = ref<ResourceClaim[]>([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
})

const load = async () => {
  loading.value = true
  try {
    const res = await resourceApi.myClaims({ page: query.page - 1, size: query.size })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

const statusText = (s: ResourceClaim['status']) => {
  if (s === 'SUBMITTED') return '待审核'
  if (s === 'CONFIRMED') return '已通过'
  if (s === 'REJECTED') return '已驳回'
  return s
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
            <div class="text-2xl font-bold text-dark mb-1">我的资源申请</div>
            <div class="text-gray-600 text-sm">查看我提交的资源需求认领申请与审核状态</div>
          </div>
          <button class="btn-primary px-5 py-2" @click="load"><i class="fa-solid fa-rotate mr-2"></i>刷新</button>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="resourceTitle" label="资源" min-width="220" />
          <el-table-column prop="quantity" label="数量" width="90" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <span
                class="px-2 py-1 rounded-full text-xs"
                :class="row.status === 'CONFIRMED' ? 'bg-secondary/10 text-secondary' : row.status === 'REJECTED' ? 'bg-red-100 text-red-600' : 'bg-accent/10 text-accent'"
              >
                {{ statusText(row.status) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="donateTime" label="申请时间" min-width="180" />
          <el-table-column prop="remark" label="申请说明" min-width="220" />
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
  </div>
</template>

