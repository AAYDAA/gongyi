<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { projectApi, type CharityProject, type DonationChannel, type DonationRecord } from '@/api/project'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const id = computed(() => Number(route.params.id))
const loading = ref(false)
const data = ref<CharityProject | null>(null)

const donateOpen = ref(false)
const donating = ref(false)
const donateForm = reactive({
  amount: 50,
  channel: 'WECHAT' as DonationChannel,
  remark: '',
})

const donationLoading = ref(false)
const donationList = ref<DonationRecord[]>([])
const donationTotal = ref(0)
const donationPage = ref(1)
const donationSize = ref(10)

const isOrgOrAdmin = computed(() => userStore.userInfo?.role === 'ORGANIZATION' || userStore.userInfo?.role === 'ADMIN')
const canDonate = computed(() => data.value?.status === 'ACTIVE')

const percent = computed(() => {
  if (!data.value) return 0
  const goal = Number(data.value.goalAmount || 0)
  const raised = Number(data.value.raisedAmount || 0)
  if (goal <= 0) return 0
  return Math.min(100, Math.round((raised / goal) * 100))
})

const load = async () => {
  loading.value = true
  try {
    data.value = await projectApi.detail(id.value)
  } finally {
    loading.value = false
  }
}

const loadDonations = async () => {
  if (!isOrgOrAdmin.value) return
  donationLoading.value = true
  try {
    const res = await projectApi.donations(id.value, { page: donationPage.value - 1, size: donationSize.value })
    donationList.value = res.content
    donationTotal.value = res.totalElements
  } finally {
    donationLoading.value = false
  }
}

const openDonate = async () => {
  if (!userStore.token) {
    await router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!canDonate.value) return
  donateOpen.value = true
}

const submitDonate = async () => {
  if (!userStore.token) return
  if (!canDonate.value) return
  donating.value = true
  try {
    await projectApi.donate(id.value, { amount: donateForm.amount, channel: donateForm.channel, remark: donateForm.remark || undefined })
    ElMessage.success('捐款成功，感谢你的支持')
    donateOpen.value = false
    await Promise.all([load(), loadDonations()])
  } catch (e: any) {
    ElMessage.error(e?.message || '捐款失败')
  } finally {
    donating.value = false
  }
}

onMounted(() => {
  load()
    .then(loadDonations)
    .catch(() => {})
})
</script>

<template>
  <div class="bg-gray-50 py-10">
    <div class="container mx-auto px-4">
      <el-skeleton v-if="loading" :rows="10" animated />

      <div v-else-if="data" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2">
          <div class="bg-white rounded-xl shadow-md overflow-hidden">
            <img
              :src="data.coverImage || 'https://images.unsplash.com/photo-1459183885421-5cc683b8dbba?auto=format&fit=crop&w=1200&q=80'"
              class="w-full h-72 object-cover"
            />
            <div class="p-6">
              <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
                <h1 class="text-2xl md:text-3xl font-bold text-dark">{{ data.title }}</h1>
                <span
                  class="text-white text-xs px-3 py-1 rounded-full w-fit"
                  :class="data.status === 'ACTIVE' ? 'bg-secondary' : data.status === 'COMPLETED' ? 'bg-primary' : 'bg-gray-600'"
                >
                  {{ data.status === 'ACTIVE' ? '进行中' : data.status === 'COMPLETED' ? '已完成' : '草稿' }}
                </span>
              </div>

              <p class="text-gray-600 mb-6">{{ data.summary || '暂无简介' }}</p>

              <div class="mb-6">
                <div class="flex justify-between text-sm text-gray-600 mb-2">
                  <span>已筹 {{ data.raisedAmount }} / 目标 {{ data.goalAmount }}</span>
                  <span class="font-medium text-primary">{{ percent }}%</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-3">
                  <div class="bg-primary h-3 rounded-full" :style="{ width: percent + '%' }"></div>
                </div>
              </div>

              <div class="prose max-w-none" v-if="data.contentHtml" v-html="data.contentHtml"></div>
              <div v-else class="text-gray-500">暂无项目详情</div>
            </div>
          </div>
        </div>

        <div class="lg:col-span-1">
          <div class="bg-white rounded-xl shadow-md p-6 sticky top-24">
            <div class="text-lg font-bold text-dark mb-4">捐款支持</div>
            <div class="text-gray-600 text-sm mb-6">
              <div class="mb-2">发起方：{{ data.creator?.username }}</div>
              <div>项目状态：{{ data.status }}</div>
            </div>
            <button class="btn-primary w-full" :disabled="!canDonate" @click="openDonate">
              <i class="fa-solid fa-hand-holding-heart mr-2"></i>
              {{ canDonate ? '立即捐款' : '暂不可捐款' }}
            </button>
            <div class="mt-4 text-xs text-gray-500">
              系统内为演示流程：默认捐款成功并记入捐款记录。
            </div>
          </div>

          <div v-if="isOrgOrAdmin" class="bg-white rounded-xl shadow-md p-6 mt-6">
            <div class="flex items-center justify-between mb-4">
              <div class="text-lg font-bold text-dark">捐款记录</div>
              <button class="text-primary text-sm hover:underline" @click="loadDonations">刷新</button>
            </div>

            <el-table :data="donationList" v-loading="donationLoading" size="small">
              <el-table-column prop="donor.username" label="捐款人" width="120">
                <template #default="{ row }">
                  <span>{{ row.donor?.username }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="amount" label="金额" width="90" />
              <el-table-column prop="channel" label="渠道" width="90" />
              <el-table-column prop="donateTime" label="时间" min-width="160" />
            </el-table>

            <div class="mt-4 flex justify-center">
              <el-pagination
                background
                layout="prev, pager, next"
                :page-size="donationSize"
                :total="donationTotal"
                v-model:current-page="donationPage"
                @current-change="loadDonations"
              />
            </div>
          </div>
        </div>
      </div>

      <div v-else class="bg-white rounded-xl shadow-md p-10 text-center text-gray-600">未找到项目</div>

      <el-dialog v-model="donateOpen" title="在线捐款" width="420px">
        <div class="space-y-4">
          <div>
            <div class="text-sm text-gray-600 mb-2">捐款金额（元）</div>
            <el-input-number v-model="donateForm.amount" :min="1" :max="999999" class="w-full" />
          </div>
          <div>
            <div class="text-sm text-gray-600 mb-2">支付渠道</div>
            <el-select v-model="donateForm.channel" class="w-full">
              <el-option label="微信" value="WECHAT" />
              <el-option label="支付宝" value="ALIPAY" />
              <el-option label="银行卡" value="BANK" />
            </el-select>
          </div>
          <div>
            <div class="text-sm text-gray-600 mb-2">备注（可选）</div>
            <el-input v-model="donateForm.remark" placeholder="给发起方的一句话" />
          </div>
        </div>
        <template #footer>
          <div class="flex justify-end gap-3">
            <el-button @click="donateOpen = false">取消</el-button>
            <el-button type="primary" :loading="donating" @click="submitDonate">确认捐款</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

