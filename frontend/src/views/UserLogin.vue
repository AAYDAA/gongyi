<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  account: '',
  password: '',
})

const rules = {
  account: [{ required: true, message: '请输入邮箱/手机号/用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const submit = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await authApi.login(form)
    userStore.setAuth(res.token, res.userInfo)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    await router.push(redirect)
  } catch (e: any) {
    ElMessage.error(e?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-260px)] flex items-center justify-center px-4 py-12 bg-gray-50">
    <div class="bg-white rounded-lg w-full max-w-md p-6 shadow-md" data-aos="zoom-in">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-xl font-bold text-dark">用户登录</h3>
        <RouterLink to="/" class="text-gray-500 hover:text-gray-700 text-xl"><i class="fa-solid fa-xmark"></i></RouterLink>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
        <el-form-item label="邮箱/手机号/用户名" prop="account">
          <el-input v-model="form.account" placeholder="请输入邮箱或手机号或用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <button class="btn-primary w-full" :disabled="loading" @click.prevent="submit">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </el-form>
      <div class="text-center text-gray-700 mt-4">
        还没有账号？<RouterLink to="/register" class="text-primary hover:underline">立即注册</RouterLink>
      </div>
    </div>
  </div>
</template>

