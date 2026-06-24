<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { newsApi, type NewsArticle, type NewsStatus } from '@/api/news'

const loading = ref(false)
const list = ref<NewsArticle[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  status: '' as '' | NewsStatus,
  page: 1,
  size: 10,
})

const load = async () => {
  loading.value = true
  try {
    const res = await newsApi.pageAdmin({
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

const setStatus = async (row: NewsArticle, status: NewsStatus) => {
  try {
    await newsApi.update(row.id, { status })
    ElMessage.success('已更新状态')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const remove = async (row: NewsArticle) => {
  try {
    await newsApi.remove(row.id)
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
  coverImage: '',
  summary: '',
  contentHtml: '',
  status: 'DRAFT' as NewsStatus,
})

const rules = {
  title: [{ required: true, message: '请输入资讯标题', trigger: 'blur' }],
}

const openCreate = () => {
  createDialog.value.open = true
  form.title = ''
  form.coverImage = ''
  form.summary = ''
  form.contentHtml = ''
  form.status = 'DRAFT'
}

const create = async () => {
  await formRef.value?.validate()
  creating.value = true
  try {
    await newsApi.create({
      title: form.title,
      coverImage: form.coverImage || undefined,
      summary: form.summary || undefined,
      contentHtml: form.contentHtml || undefined,
      status: form.status,
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
            <div class="text-2xl font-bold text-dark mb-1">资讯管理</div>
            <div class="text-gray-600 text-sm">管理员发布/维护资讯</div>
          </div>
          <div class="flex flex-col sm:flex-row gap-2">
            <el-input v-model="query.keyword" size="small" placeholder="搜索标题/摘要" clearable class="sm:w-72" @keyup.enter="load" />
            <el-select v-model="query.status" size="small" placeholder="状态" clearable class="sm:w-40" @change="load">
              <el-option label="草稿" value="DRAFT" />
              <el-option label="已发布" value="PUBLISHED" />
            </el-select>
            <button class="btn-secondary btn-toolbar" @click="openCreate"><i class="fa-solid fa-plus mr-1.5"></i>新建</button>
            <button class="btn-primary btn-toolbar" @click="load"><i class="fa-solid fa-rotate mr-1.5"></i>刷新</button>
          </div>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="title" label="标题" min-width="260" />
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="publishTime" label="发布时间" min-width="180" />
          <el-table-column label="操作" width="300">
            <template #default="{ row }">
              <div class="flex gap-2">
                <el-button size="small" type="info" @click="setStatus(row, 'DRAFT')">设为草稿</el-button>
                <el-button size="small" type="success" @click="setStatus(row, 'PUBLISHED')">发布</el-button>
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

    <el-dialog v-model="createDialog.open" title="新建资讯" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="封面图 URL">
          <el-input v-model="form.coverImage" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="正文 HTML（可选）">
          <el-input v-model="form.contentHtml" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" class="w-full">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
          </el-select>
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
