<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { resourceApi, type Resource, type ResourceStatus } from '@/api/resource'

const loading = ref(false)
const list = ref<Resource[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  status: '' as '' | ResourceStatus,
  page: 1,
  size: 10,
})

const load = async () => {
  loading.value = true
  try {
    const res = await resourceApi.page({
      keyword: query.keyword || undefined,
      status: query.status || undefined,
      page: query.page - 1,
      size: query.size,
    })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

const setStatus = async (row: Resource, status: ResourceStatus) => {
  try {
    await resourceApi.update(row.id, { status })
    ElMessage.success('已更新状态')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const remove = async (row: Resource) => {
  try {
    await resourceApi.remove(row.id)
    ElMessage.success('已删除')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

const createDialog = ref({ open: false })
const creating = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  title: '',
  description: '',
  imageUrls: '',
})

const rules = {
  title: [{ required: true, message: '请输入需求标题', trigger: 'blur' }],
}

const openCreate = () => {
  createDialog.value.open = true
  form.title = ''
  form.description = ''
  form.imageUrls = ''
}

const create = async () => {
  await formRef.value?.validate()
  creating.value = true
  try {
    await resourceApi.create({
      title: form.title.trim(),
      description: form.description.trim() || undefined,
      imageUrls: form.imageUrls.trim() || undefined,
      status: 'REQUESTING',
    })
    ElMessage.success('已创建')
    createDialog.value.open = false
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '创建失败')
  } finally {
    creating.value = false
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
            <div class="text-2xl font-bold text-dark mb-1">资源需求管理</div>
            <div class="text-gray-600 text-sm">管理员发布/维护资源需求</div>
          </div>
          <div class="flex flex-col sm:flex-row gap-2">
            <el-input v-model="query.keyword" size="small" placeholder="搜索标题/描述" clearable class="sm:w-72" @keyup.enter="load" />
            <el-select v-model="query.status" size="small" placeholder="状态" clearable class="sm:w-40" @change="load">
              <el-option label="需求中" value="REQUESTING" />
              <el-option label="已认领" value="CLAIMED" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
            <button class="btn-secondary btn-toolbar" @click="openCreate"><i class="fa-solid fa-plus mr-1.5"></i>新建</button>
            <button class="btn-primary btn-toolbar" @click="load"><i class="fa-solid fa-rotate mr-1.5"></i>刷新</button>
          </div>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="title" label="标题" min-width="240" />
          <el-table-column prop="publisher.username" label="发布人" width="160">
            <template #default="{ row }">
              <span>{{ row.publisher?.username }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="publishTime" label="发布时间" min-width="180" />
          <el-table-column label="操作" width="360">
            <template #default="{ row }">
              <div class="flex gap-2 flex-wrap">
                <el-button size="small" type="info" @click="setStatus(row, 'REQUESTING')">需求中</el-button>
                <el-button size="small" type="warning" @click="setStatus(row, 'CLAIMED')">已认领</el-button>
                <el-button size="small" type="success" @click="setStatus(row, 'COMPLETED')">已完成</el-button>
                <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
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

    <el-dialog v-model="createDialog.open" title="新建资源需求" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="描述（可选）">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="图片 URL（可选，多个用逗号分隔）">
          <el-input v-model="form.imageUrls" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="createDialog.open = false">取消</el-button>
          <el-button type="primary" :loading="creating" @click="create">创建</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
