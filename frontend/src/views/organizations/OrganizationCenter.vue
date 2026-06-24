<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { organizationApi, type OrganizationProfile } from '@/api/organization'

const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)

const profile = ref<OrganizationProfile | null>(null)

const form = reactive({
  name: '',
  logoUrl: '',
  intro: '',
  address: '',
  contactPhone: '',
  website: '',
  licenseImageUrls: '',
})

const verifyStatusText = computed(() => {
  const s = profile.value?.verifyStatus
  if (s === 'APPROVED') return '已认证'
  if (s === 'PENDING') return '审核中'
  if (s === 'REJECTED') return '未通过'
  return '未提交'
})

const rules = {
  name: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
}

const load = async () => {
  loading.value = true
  try {
    profile.value = await organizationApi.me()
    form.name = profile.value.name || ''
    form.logoUrl = profile.value.logoUrl || ''
    form.intro = profile.value.intro || ''
    form.address = profile.value.address || ''
    form.contactPhone = profile.value.contactPhone || ''
    form.website = profile.value.website || ''
    form.licenseImageUrls = profile.value.licenseImageUrls || ''
  } catch {
    profile.value = null
  } finally {
    loading.value = false
  }
}

const save = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    profile.value = await organizationApi.updateMe({ ...form })
    ElMessage.success('已保存')
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

const submitVerify = async () => {
  submitting.value = true
  try {
    profile.value = await organizationApi.submit()
    ElMessage.success('已提交审核')
  } catch (e: any) {
    ElMessage.error(e?.message || '提交失败')
  } finally {
    submitting.value = false
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
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-6 mb-6">
          <div>
            <div class="text-2xl font-bold text-dark mb-1">组织中心</div>
            <div class="text-gray-600 text-sm">
              认证状态：
              <span class="font-medium" :class="profile?.verifyStatus === 'APPROVED' ? 'text-secondary' : profile?.verifyStatus === 'PENDING' ? 'text-accent' : 'text-gray-600'">
                {{ verifyStatusText }}
              </span>
            </div>
          </div>
          <div class="flex gap-3">
            <button class="btn-primary px-5 py-2" :disabled="loading" @click="load"><i class="fa-solid fa-rotate mr-2"></i>刷新</button>
            <button class="btn-secondary px-5 py-2" :disabled="submitting || loading" @click="save"><i class="fa-solid fa-floppy-disk mr-2"></i>保存</button>
            <button
              class="btn-accent px-5 py-2"
              :disabled="submitting || loading || profile?.verifyStatus === 'PENDING' || profile?.verifyStatus === 'APPROVED'"
              @click="submitVerify"
            >
              <i class="fa-solid fa-paper-plane mr-2"></i>提交审核
            </button>
          </div>
        </div>

        <el-skeleton v-if="loading" :rows="10" animated />

        <div v-else>
          <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="max-w-3xl">
            <el-form-item label="组织名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入组织名称" />
            </el-form-item>
            <el-form-item label="Logo URL">
              <el-input v-model="form.logoUrl" placeholder="如：https://..." />
            </el-form-item>
            <el-form-item label="组织简介">
              <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="一句话介绍组织" />
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="form.address" placeholder="请输入地址" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="官网">
              <el-input v-model="form.website" placeholder="如：https://..." />
            </el-form-item>
            <el-form-item label="资质图片 URLs（逗号分隔）">
              <el-input v-model="form.licenseImageUrls" type="textarea" :rows="2" placeholder="url1,url2,..." />
            </el-form-item>
          </el-form>

          <div v-if="profile?.verifyRemark" class="mt-6 bg-gray-50 rounded-lg p-4 text-sm text-gray-700">
            <div class="font-medium mb-2">审核备注</div>
            <div>{{ profile.verifyRemark }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

