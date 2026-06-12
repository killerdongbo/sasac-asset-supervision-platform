<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/basic-data'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ name: '', code: '', parentId: null as number | null, depreciationMethod: 'STRAIGHT_LINE', defaultUsefulLife: 120, defaultResidualRate: 0.05 })
const editingId = ref<number | null>(null)

const methods = ['STRAIGHT_LINE', 'ACCELERATED', 'UNITS_OF_PRODUCTION']

async function fetch() {
  loading.value = true
  try { const res = await getCategories(); list.value = res.data } finally { loading.value = false }
}

function openCreate() {
  editingId.value = null
  form.value = { name: '', code: '', parentId: null, depreciationMethod: 'STRAIGHT_LINE', defaultUsefulLife: 120, defaultResidualRate: 0.05 }
  dialogVisible.value = true
}

function openEdit(row: any) {
  editingId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleSave() {
  if (editingId.value) {
    await updateCategory(editingId.value, form.value)
    ElMessage.success('更新成功')
  } else {
    await createCategory(form.value)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  fetch()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该分类吗？', '确认', { type: 'warning' })
  await deleteCategory(id)
  ElMessage.success('已删除')
  fetch()
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h3>资产分类</h3>
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="code" label="分类编码" width="140" />
      <el-table-column prop="name" label="分类名称" min-width="160" />
      <el-table-column prop="level" label="层级" width="80" />
      <el-table-column prop="depreciationMethod" label="默认折旧方法" width="140" />
      <el-table-column prop="defaultUsefulLife" label="默认使用年限(月)" width="150" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="editingId ? '编辑分类' : '新增分类'" v-model="dialogVisible" width="520px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="分类编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="分类名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="折旧方法">
          <el-select v-model="form.depreciationMethod">
            <el-option v-for="m in methods" :key="m" :label="m" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item label="使用年限(月)"><el-input-number v-model="form.defaultUsefulLife" :min="1" /></el-form-item>
        <el-form-item label="残值率"><el-input-number v-model="form.defaultResidualRate" :min="0" :max="1" :step="0.01" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
