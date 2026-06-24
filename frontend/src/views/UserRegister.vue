<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirm: '',
  email: '',
  phone: '',
  agree: true,
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirm: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_: any, value: string, cb: (err?: Error) => void) => {
        if (value !== form.password) return cb(new Error('两次密码不一致'))
        cb()
      },
      trigger: 'blur',
    },
  ],
}

const submit = async () => {
  if (!form.agree) {
    ElMessage.warning('请先同意用户协议与隐私政策')
    return
  }
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await authApi.register({
      username: form.username,
      password: form.password,
      phone: form.phone || undefined,
      email: form.email || undefined,
    })
    userStore.setAuth(res.token, res.userInfo)
    ElMessage.success('注册成功')
    await router.push('/')
  } catch (e: any) {
    ElMessage.error(e?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-[calc(100vh-260px)] flex items-center justify-center px-4 py-12 bg-gray-50">
    <div class="bg-white rounded-lg w-full max-w-md p-6 shadow-md" data-aos="zoom-in">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-xl font-bold text-dark">用户注册</h3>
        <RouterLink to="/" class="text-gray-500 hover:text-gray-700 text-xl"><i class="fa-solid fa-xmark"></i></RouterLink>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirm">
          <el-input v-model="form.confirm" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <div class="flex items-center text-sm text-gray-700 mb-4">
          <el-checkbox v-model="form.agree" />
          <span class="ml-2">我已阅读并同意</span>
          <a href="#" class="text-primary hover:underline mx-1">用户协议</a>
          <span>和</span>
          <a href="#" class="text-primary hover:underline mx-1">隐私政策</a>
        </div>
        <button class="btn-primary w-full" :disabled="loading" @click.prevent="submit">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </el-form>
      <div class="text-center text-gray-700 mt-4">
        已有账号？<RouterLink to="/login" class="text-primary hover:underline">立即登录</RouterLink>
      </div>
    </div>
  </div>
</template>
