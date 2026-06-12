<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUsers, createUser, updateUser, deleteUser } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false); const dialogVisible = ref(false)
const form = ref({ username: '', realName: '', phone: '', roleCode: 'ENTERPRISE_OPERATOR', orgId: null as number | null, tenantId: 100, status: 1 })
const editingId = ref<number | null>(null)

async function fetch() { loading.value = true; try { const res = await getUsers(); list.value = res.data } finally { loading.value = false } }

function openCreate() { editingId.value = null; form.value = { username: '', realName: '', phone: '', roleCode: 'ENTERPRISE_OPERATOR', orgId: null, tenantId: 100, status: 1 }; dialogVisible.value = true }
function openEdit(row: any) { editingId.value = row.id; form.value = { ...row }; dialogVisible.value = true }

async function handleSave() {
  if (editingId.value) { await updateUser(editingId.value, form.value); ElMessage.success('更新成功') }
  else { await createUser(form.value); ElMessage.success('创建成功') }
  dialogVisible.value = false; fetch()
}

async function handleDelete(id: number) { await ElMessageBox.confirm('确定删除？', '确认', { type: 'warning' }); await deleteUser(id); ElMessage.success('已删除'); fetch() }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>用户管理</h3><el-button type="primary" @click="openCreate">新增用户</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="roleCode" label="角色" width="160" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }"><el-button link type="primary" @click="openEdit(row)">编辑</el-button><el-button link type="danger" @click="handleDelete(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-dialog :title="editingId ? '编辑用户' : '新增用户'" v-model="dialogVisible" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.roleCode"><el-option label="企业操作员" value="ENTERPRISE_OPERATOR" /><el-option label="集团审核员" value="GROUP_REVIEWER" /><el-option label="国资委监管员" value="SASAC_SUPERVISOR" /><el-option label="系统管理员" value="SYSTEM_ADMIN" /></el-select></el-form-item>
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
