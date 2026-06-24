<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { resourceApi, type Resource, type ResourceStatus } from '@/api/resource'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const list = ref<Resource[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  status: '' as '' | ResourceStatus,
  page: 1,
  size: 9,
})

const pageParams = computed(() => ({
  keyword: query.keyword || undefined,
  status: query.status || undefined,
  page: query.page - 1,
  size: query.size,
}))

const load = async () => {
  loading.value = true
  try {
    const res = await resourceApi.page(pageParams.value)
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

const onSearch = async () => {
  query.page = 1
  await load()
}

const canClaim = (item: Resource) => userStore.token && userStore.userInfo?.role === 'VOLUNTEER' && item.status === 'REQUESTING'

const claimDialog = ref({
  open: false,
  resourceId: 0,
  title: '',
  quantity: 1,
  remark: '',
})

const canCreateNeed = computed(() => !!userStore.token && userStore.userInfo?.role === 'ADMIN')
const goMyClaims = async () => {
  await router.push('/resources/my-claims')
}

const createNeedDialog = ref({ open: false })
const creatingNeed = ref(false)
const needForm = reactive({
  title: '',
  description: '',
  imageUrls: '',
})

const openCreateNeed = async () => {
  if (!userStore.token) {
    await router.push({ path: '/login', query: { redirect: '/resources' } })
    return
  }
  if (!canCreateNeed.value) return
  createNeedDialog.value.open = true
  needForm.title = ''
  needForm.description = ''
  needForm.imageUrls = ''
}

const submitNeed = async () => {
  if (!needForm.title.trim()) {
    ElMessage.warning('请输入需求标题')
    return
  }
  creatingNeed.value = true
  try {
    await resourceApi.create({
      title: needForm.title.trim(),
      description: needForm.description.trim() || undefined,
      imageUrls: needForm.imageUrls.trim() || undefined,
      status: 'REQUESTING',
    })
    ElMessage.success('已发布需求')
    createNeedDialog.value.open = false
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '发布失败')
  } finally {
    creatingNeed.value = false
  }
}

const openClaim = async (item: Resource) => {
  if (!userStore.token) {
    await router.push({ path: '/login', query: { redirect: '/resources' } })
    return
  }
  if (!canClaim(item)) return
  claimDialog.value.open = true
  claimDialog.value.resourceId = item.id
  claimDialog.value.title = item.title
  claimDialog.value.quantity = 1
  claimDialog.value.remark = ''
}

const onClaim = async () => {
  try {
    await resourceApi.claim(claimDialog.value.resourceId, { quantity: claimDialog.value.quantity, remark: claimDialog.value.remark || undefined })
    ElMessage.success('申请已提交，等待管理员审核')
    claimDialog.value.open = false
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '提交失败')
  }
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<template>
  <div class="bg-gray-50">
    <section class="py-10 bg-white">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row md:items-center gap-4">
          <div class="flex-1">
            <div class="text-2xl font-bold text-dark mb-2">资源需求</div>
            <div class="text-gray-600">志愿者可认领资源需求并对接落地</div>
          </div>

          <div class="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
            <button v-if="canCreateNeed" class="btn-secondary btn-toolbar" @click="openCreateNeed">
              <i class="fa-solid fa-plus mr-1.5"></i>发布需求
            </button>
            <button v-if="userStore.userInfo?.role === 'VOLUNTEER'" class="btn-secondary btn-toolbar" @click="goMyClaims">
              <i class="fa-solid fa-list mr-1.5"></i>我的资源申请
            </button>
            <el-input v-model="query.keyword" size="small" placeholder="搜索资源标题/描述" clearable class="sm:w-72" @keyup.enter="onSearch" />
            <el-select v-model="query.status" size="small" placeholder="状态" clearable class="sm:w-40" @change="onSearch">
              <el-option label="需求中" value="REQUESTING" />
              <el-option label="已认领" value="CLAIMED" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
            <button class="btn-primary btn-toolbar" @click="onSearch"><i class="fa-solid fa-magnifying-glass mr-1.5"></i>搜索</button>
          </div>
        </div>
      </div>
    </section>

    <section class="py-12">
      <div class="container mx-auto px-4">
        <el-skeleton v-if="loading" :rows="6" animated />

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div v-for="item in list" :key="item.id" class="bg-white rounded-xl shadow-md overflow-hidden card-hover" data-aos="fade-up">
            <div class="p-6">
              <div class="flex items-start justify-between gap-3 mb-2">
                <h3 class="text-xl font-bold text-dark line-clamp-1">{{ item.title }}</h3>
                <span
                  class="text-xs px-2 py-1 rounded-full"
                  :class="
                    item.status === 'REQUESTING'
                      ? 'bg-primary/10 text-primary'
                      : item.status === 'CLAIMED'
                        ? 'bg-accent/10 text-accent'
                        : 'bg-secondary/10 text-secondary'
                  "
                >
                  {{ item.status === 'REQUESTING' ? '需求中' : item.status === 'CLAIMED' ? '已认领' : '已完成' }}
                </span>
              </div>
              <p class="text-gray-600 mb-4 line-clamp-2">{{ item.description || '暂无描述' }}</p>
              <div class="flex justify-between items-center text-sm text-gray-600">
                <span>发布人：{{ item.publisher?.username }}</span>
                <button class="text-primary hover:underline" :disabled="!canClaim(item)" @click="openClaim(item)">
                  {{ canClaim(item) ? '申请认领' : '不可申请' }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-10 flex justify-center">
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
    </section>

    <el-dialog v-model="claimDialog.open" :title="`申请认领 - ${claimDialog.title}`" width="420px">
      <div class="space-y-4">
        <div>
          <div class="text-sm text-gray-600 mb-2">数量</div>
          <el-input-number v-model="claimDialog.quantity" :min="1" :max="9999" class="w-full" />
        </div>
        <div>
          <div class="text-sm text-gray-600 mb-2">申请说明（可选）</div>
          <el-input v-model="claimDialog.remark" type="textarea" :rows="3" placeholder="描述你的对接计划/联系方式等" />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="claimDialog.open = false">取消</el-button>
          <el-button type="primary" @click="onClaim">提交申请</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="createNeedDialog.open" title="发布资源需求" width="520px">
      <div class="space-y-4">
        <div>
          <div class="text-sm text-gray-600 mb-2">标题</div>
          <el-input v-model="needForm.title" />
        </div>
        <div>
          <div class="text-sm text-gray-600 mb-2">描述（可选）</div>
          <el-input v-model="needForm.description" type="textarea" :rows="4" />
        </div>
        <div>
          <div class="text-sm text-gray-600 mb-2">图片 URL（可选，多个用逗号分隔）</div>
          <el-input v-model="needForm.imageUrls" />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="createNeedDialog.open = false">取消</el-button>
          <el-button type="primary" :loading="creatingNeed" @click="submitNeed">发布</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
