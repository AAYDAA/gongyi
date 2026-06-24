<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { volunteerApplicationApi, type VolunteerApplicationStatus } from '@/api/volunteerApplication'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const submitting = ref(false)
const exists = ref(false)

const form = reactive({
  realName: '',
  contactPhone: '',
  skills: '',
  experience: '',
  availableTime: '',
  motivation: '',
})

const status = ref<VolunteerApplicationStatus>('DRAFT')
const reviewRemark = ref('')
const statusText = computed(() => {
  if (status.value === 'PENDING') return '待审核'
  if (status.value === 'APPROVED') return '已通过'
  if (status.value === 'REJECTED') return '已驳回'
  return '草稿'
})

const load = async () => {
  loading.value = true
  try {
    const mine = await volunteerApplicationApi.mine()
    exists.value = true
    form.realName = mine.realName || ''
    form.contactPhone = mine.contactPhone || ''
    form.skills = mine.skills || ''
    form.experience = mine.experience || ''
    form.availableTime = mine.availableTime || ''
    form.motivation = mine.motivation || ''
    status.value = mine.status
    reviewRemark.value = mine.reviewRemark || ''
  } catch {
    exists.value = false
  } finally {
    loading.value = false
  }
}

const save = async () => {
  saving.value = true
  try {
    const mine = await volunteerApplicationApi.save({ ...form })
    exists.value = true
    status.value = mine.status
    reviewRemark.value = mine.reviewRemark || ''
    ElMessage.success('申请信息已保存')
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const submit = async () => {
  submitting.value = true
  try {
    const mine = await volunteerApplicationApi.submit()
    exists.value = true
    status.value = mine.status
    reviewRemark.value = mine.reviewRemark || ''
    ElMessage.success('申请已提交，请等待管理员审核')
  } catch (e: any) {
    ElMessage.error(e?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

const refreshMe = async () => {
  try {
    const me = await userApi.me()
    if (userStore.token) userStore.setAuth(userStore.token, me)
  } catch {
    // ignore refresh error
  }
}

onMounted(async () => {
  await load()
  await refreshMe()
})
</script>

<template>
  <div class="bg-gray-50 py-10">
    <div class="container mx-auto px-4">
      <div class="bg-white rounded-xl shadow-md p-6 max-w-3xl mx-auto" v-loading="loading">
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
          <div>
            <div class="text-2xl font-bold text-dark mb-1">申请成为志愿者</div>
            <div class="text-gray-600 text-sm">填写申请资料并提交，等待管理员审核后生效</div>
          </div>
          <el-tag type="info">{{ statusText }}</el-tag>
        </div>

        <el-alert
          v-if="status === 'REJECTED' && reviewRemark"
          type="warning"
          show-icon
          :closable="false"
          class="mb-5"
          :title="`驳回原因：${reviewRemark}`"
        />
        <el-alert v-else-if="status === 'PENDING'" type="info" show-icon :closable="false" class="mb-5" title="申请正在审核中，请耐心等待" />
        <el-alert v-else-if="status === 'APPROVED'" type="success" show-icon :closable="false" class="mb-5" title="申请已通过，你已成为志愿者" />

        <el-form label-position="top">
          <el-form-item label="真实姓名（必填）">
            <el-input v-model="form.realName" maxlength="64" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="联系电话（必填）">
            <el-input v-model="form.contactPhone" maxlength="32" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="技能特长">
            <el-input v-model="form.skills" maxlength="128" placeholder="如：摄影、医疗急救、心理辅导等" />
          </el-form-item>
          <el-form-item label="相关经验">
            <el-input v-model="form.experience" maxlength="255" placeholder="如：参与过社区志愿活动2年" />
          </el-form-item>
          <el-form-item label="可服务时段">
            <el-input v-model="form.availableTime" maxlength="128" placeholder="如：工作日晚上、周末全天" />
          </el-form-item>
          <el-form-item label="申请动机（必填）">
            <el-input v-model="form.motivation" type="textarea" :rows="4" maxlength="512" show-word-limit placeholder="请描述你申请成为志愿者的原因" />
          </el-form-item>
        </el-form>

        <div class="flex flex-wrap gap-3">
          <button class="btn-outline-primary px-5 py-2" :disabled="saving" @click="save">
            <i class="fa-solid fa-floppy-disk mr-2"></i>{{ saving ? '保存中...' : exists ? '更新申请信息' : '保存申请信息' }}
          </button>
          <button class="btn-primary px-5 py-2" :disabled="submitting || status === 'PENDING'" @click="submit">
            <i class="fa-solid fa-paper-plane mr-2"></i>{{ submitting ? '提交中...' : '提交审核' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
