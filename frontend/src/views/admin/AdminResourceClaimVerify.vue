<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { resourceApi, type ResourceClaim, type ResourceClaimStatus } from '@/api/resource'

const loading = ref(false)
const list = ref<ResourceClaim[]>([])
const total = ref(0)

const query = reactive({
  status: 'SUBMITTED' as ResourceClaimStatus,
  page: 1,
  size: 10,
})

const load = async () => {
  loading.value = true
  try {
    const res = await resourceApi.adminClaims({ status: query.status, page: query.page - 1, size: query.size })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

const approve = async (row: ResourceClaim) => {
  try {
    await resourceApi.reviewClaim(row.id, 'CONFIRMED')
    ElMessage.success('已通过')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const reject = async (row: ResourceClaim) => {
  try {
    await resourceApi.reviewClaim(row.id, 'REJECTED')
    ElMessage.success('已驳回')
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
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
          <div>
            <div class="text-2xl font-bold text-dark mb-1">资源需求申请审核</div>
            <div class="text-gray-600 text-sm">管理员审核志愿者对资源需求的认领申请</div>
          </div>
          <div class="flex flex-col sm:flex-row gap-2">
            <el-select v-model="query.status" size="small" class="sm:w-44" @change="load">
              <el-option label="待审核" value="SUBMITTED" />
              <el-option label="已通过" value="CONFIRMED" />
              <el-option label="已驳回" value="REJECTED" />
            </el-select>
            <button class="btn-primary btn-toolbar" @click="load"><i class="fa-solid fa-rotate mr-1.5"></i>刷新</button>
          </div>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="resourceTitle" label="资源" min-width="220" />
          <el-table-column prop="donor.username" label="申请人" width="160">
            <template #default="{ row }">
              <span>{{ row.donor?.username }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="90" />
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="donateTime" label="申请时间" min-width="180" />
          <el-table-column prop="remark" label="申请说明" min-width="220" />
          <el-table-column label="操作" width="220">
            <template #default="{ row }">
              <div class="flex gap-2">
                <el-button size="small" type="success" :disabled="row.status !== 'SUBMITTED'" @click="approve(row)">通过</el-button>
                <el-button size="small" type="danger" :disabled="row.status !== 'SUBMITTED'" @click="reject(row)">驳回</el-button>
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
  </div>
</template>
