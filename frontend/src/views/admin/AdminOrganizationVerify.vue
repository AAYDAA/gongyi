<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { organizationApi, type OrganizationProfile, type OrganizationVerifyStatus } from '@/api/organization'

const loading = ref(false)
const list = ref<OrganizationProfile[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  verifyStatus: 'PENDING' as OrganizationVerifyStatus,
  page: 1,
  size: 10,
})

const load = async () => {
  loading.value = true
  try {
    const res = await organizationApi.page({
      keyword: query.keyword || undefined,
      verifyStatus: query.verifyStatus,
      page: query.page - 1,
      size: query.size,
    })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

const openReject = (row: OrganizationProfile) => {
  rejectDialog.value.open = true
  rejectDialog.value.id = row.id
  rejectDialog.value.remark = ''
}

const approve = async (row: OrganizationProfile) => {
  try {
    await organizationApi.verify(row.id, { verifyStatus: 'APPROVED' })
    ElMessage.success('已通过')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const rejectDialog = ref({
  open: false,
  id: 0,
  remark: '',
})

const reject = async () => {
  try {
    await organizationApi.verify(rejectDialog.value.id, { verifyStatus: 'REJECTED', verifyRemark: rejectDialog.value.remark || undefined })
    ElMessage.success('已驳回')
    rejectDialog.value.open = false
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
            <div class="text-2xl font-bold text-dark mb-1">组织认证审核</div>
            <div class="text-gray-600 text-sm">管理员审核组织入驻/认证资料</div>
          </div>
          <div class="flex flex-col sm:flex-row gap-2">
            <el-input v-model="query.keyword" size="small" placeholder="搜索组织名称/简介/地址" clearable class="sm:w-72" @keyup.enter="load" />
            <el-select v-model="query.verifyStatus" size="small" class="sm:w-44" @change="load">
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已驳回" value="REJECTED" />
              <el-option label="未提交" value="UNSUBMITTED" />
            </el-select>
            <button class="btn-primary btn-toolbar" @click="load"><i class="fa-solid fa-rotate mr-1.5"></i>刷新</button>
          </div>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="name" label="组织名称" min-width="220" />
          <el-table-column prop="user.username" label="账号" width="160">
            <template #default="{ row }">
              <span>{{ row.user?.username }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="verifyStatus" label="状态" width="140" />
          <el-table-column label="操作" width="220">
            <template #default="{ row }">
              <div class="flex gap-2">
                <el-button size="small" type="success" :disabled="row.verifyStatus !== 'PENDING'" @click="approve(row)">通过</el-button>
                <el-button size="small" type="danger" :disabled="row.verifyStatus !== 'PENDING'" @click="openReject(row)">驳回</el-button>
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

    <el-dialog v-model="rejectDialog.open" title="驳回原因" width="420px">
      <el-input v-model="rejectDialog.remark" type="textarea" :rows="3" placeholder="请输入驳回原因（可选）" />
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="rejectDialog.open = false">取消</el-button>
          <el-button type="danger" @click="reject">确认驳回</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
