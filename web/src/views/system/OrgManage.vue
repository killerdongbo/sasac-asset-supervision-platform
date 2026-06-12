<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getOrganizations, createOrganization, updateOrganization, deleteOrganization } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false); const dialogVisible = ref(false)
const form = ref({ name: '', orgType: 'ENTERPRISE', orgCode: '', parentId: null as number | null, tenantId: 100 })
const editingId = ref<number | null>(null)

async function fetch() { loading.value = true; try { const res = await getOrganizations(); list.value = res.data } finally { loading.value = false } }
function openCreate() { editingId.value = null; form.value = { name: '', orgType: 'ENTERPRISE', orgCode: '', parentId: null, tenantId: 100 }; dialogVisible.value = true }
function openEdit(row: any) { editingId.value = row.id; form.value = { ...row }; dialogVisible.value = true }
async function handleSave() {
  if (editingId.value) { await updateOrganization(editingId.value, form.value); ElMessage.success('更新成功') }
  else { await createOrganization(form.value); ElMessage.success('创建成功') }
  dialogVisible.value = false; fetch()
}
async function handleDelete(id: number) { await ElMessageBox.confirm('确定删除？', '确认', { type: 'warning' }); await deleteOrganization(id); ElMessage.success('已删除'); fetch() }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>组织架构</h3><el-button type="primary" @click="openCreate">新增组织</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe row-key="id" default-expand-all>
      <el-table-column prop="name" label="名称" min-width="240" />
      <el-table-column label="类型" width="140"><template #default="{ row }"><el-tag :type="row.orgType === 'SASAC' ? 'danger' : row.orgType === 'GROUP' ? 'warning' : 'primary'">{{ row.orgType }}</el-tag></template></el-table-column>
      <el-table-column prop="orgCode" label="编码" width="120" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }"><el-button link type="primary" @click="openEdit(row)">编辑</el-button><el-button link type="danger" @click="handleDelete(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-dialog :title="editingId ? '编辑组织' : '新增组织'" v-model="dialogVisible" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.orgType"><el-option label="国资委" value="SASAC" /><el-option label="集团" value="GROUP" /><el-option label="企业" value="ENTERPRISE" /></el-select></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.orgCode" /></el-form-item>
        <el-form-item label="上级组织"><el-input-number v-model="form.parentId" /></el-form-item>
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
