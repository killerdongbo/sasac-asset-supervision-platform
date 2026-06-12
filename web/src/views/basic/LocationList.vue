<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getLocations, createLocation, updateLocation, deleteLocation } from '@/api/basic-data'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ name: '', address: '', parentId: null as number | null })
const editingId = ref<number | null>(null)

async function fetch() {
  loading.value = true
  try { const res = await getLocations(); list.value = res.data } finally { loading.value = false }
}

function openCreate() {
  editingId.value = null; form.value = { name: '', address: '', parentId: null }; dialogVisible.value = true
}

function openEdit(row: any) {
  editingId.value = row.id; form.value = { name: row.name, address: row.address || '', parentId: row.parentId }; dialogVisible.value = true
}

async function handleSave() {
  if (editingId.value) {
    await updateLocation(editingId.value, form.value); ElMessage.success('更新成功')
  } else {
    await createLocation(form.value); ElMessage.success('创建成功')
  }
  dialogVisible.value = false; fetch()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该位置吗？', '确认', { type: 'warning' })
  await deleteLocation(id); ElMessage.success('已删除'); fetch()
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>存放位置</h3><el-button type="primary" @click="openCreate">新增位置</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="name" label="位置名称" min-width="200" />
      <el-table-column prop="address" label="详细地址" min-width="300" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :title="editingId ? '编辑位置' : '新增位置'" v-model="dialogVisible" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
