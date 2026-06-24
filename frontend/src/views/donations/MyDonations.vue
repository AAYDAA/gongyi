<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { donationApi } from '@/api/donation'
import type { DonationRecord } from '@/api/project'

const loading = ref(false)
const list = ref<DonationRecord[]>([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
})

const load = async () => {
  loading.value = true
  try {
    const res = await donationApi.my({ page: query.page - 1, size: query.size })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
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
            <div class="text-2xl font-bold text-dark mb-1">我的捐款</div>
            <div class="text-gray-600 text-sm">查看个人捐款记录</div>
          </div>
          <button class="btn-primary px-5 py-2" @click="load"><i class="fa-solid fa-rotate mr-2"></i>刷新</button>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="projectTitle" label="项目" min-width="220" />
          <el-table-column label="金额" width="120">
            <template #default="{ row }">
              <span class="font-medium text-primary">{{ row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="channel" label="渠道" width="120" />
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="donateTime" label="时间" min-width="200" />
          <el-table-column prop="remark" label="备注" min-width="160" />
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

