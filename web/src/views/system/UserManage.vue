<template>
  <div class="user-page">
    <div class="page-header"><h2>用户管理</h2><el-button type="primary" @click="openForm(null)">新增用户</el-button></div>
    <el-card>
      <div class="filter-bar">
        <el-input v-model="keyword" placeholder="用户名/姓名" clearable style="width:200px" @keyup.enter="load"/>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width:120px;margin-left:12px" @change="load">
          <el-option label="正常" :value="1"/><el-option label="禁用" :value="0"/>
        </el-select>
        <el-button style="margin-left:12px" @click="load">查询</el-button>
      </div>
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="140"/>
        <el-table-column prop="realName" label="姓名" width="120"/>
        <el-table-column prop="phone" label="电话" width="140"/>
        <el-table-column prop="roleCode" label="角色" width="140"/>
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{row.status===1?'启用':'禁用'}}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170"/>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" @click="openForm(row)">编辑</el-button>
            <el-button link type="warning" @click="openAssignRoles(row)">分配角色</el-button>
            <el-button link type="info" @click="doResetPwd(row)">重置密码</el-button>
            <el-button link :type="row.status===1?'danger':'success'" @click="doToggle(row)">{{row.status===1?'禁用':'启用'}}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="20" :total="total" layout="total,prev,pager,next" @current-change="load" style="margin-top:16px;justify-content:flex-end"/>
    </el-card>

    <el-dialog v-model="showForm" :title="editingId?'编辑用户':'新增用户'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="!!editingId"/></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName"/></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone"/></el-form-item>
        <el-form-item v-if="!editingId" label="密码"><el-input v-model="form.password" placeholder="默认123456"/></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleCode" style="width:100%">
            <el-option label="企业操作员" value="ENTERPRISE_OPERATOR"/><el-option label="组织管理员" value="ORG_MANAGER"/>
            <el-option label="租户管理员" value="TENANT_ADMIN"/><el-option label="只读用户" value="VIEWER"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="showForm=false">取消</el-button><el-button type="primary" @click="doSave">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="showRoles" title="分配角色" width="400px">
      <el-checkbox-group v-model="checkedRoleIds">
        <div v-for="r in allRoles" :key="r.id" style="margin-bottom:8px">
          <el-checkbox :value="r.id">{{r.roleName}} ({{r.roleCode}})</el-checkbox>
        </div>
      </el-checkbox-group>
      <template #footer><el-button @click="showRoles=false">取消</el-button><el-button type="primary" @click="doAssignRoles">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listUsers, createUser, updateUser, resetPassword, toggleUserStatus, assignUserRoles } from '@/api/user'
import { listRoles, getUserRoles } from '@/api/permission'
import type { Role } from '@/api/permission'

const users = ref<any[]>([]); const loading = ref(false)
const keyword = ref(''); const statusFilter = ref<number|undefined>(undefined)
const page = ref(1); const total = ref(0)

const showForm = ref(false); const editingId = ref<string|null>(null)
const form = ref({ username:'', realName:'', phone:'', roleCode:'ENTERPRISE_OPERATOR', password:'' })

const showRoles = ref(false); const currentUserId = ref<string>('')
const allRoles = ref<Role[]>([]); const checkedRoleIds = ref<string[]>([])

async function load() {
  loading.value = true
  try { const r = await listUsers({ keyword:keyword.value||undefined, status:statusFilter.value, page:page.value, size:20 }); users.value = r.data.records||[]; total.value = r.data.total||0 } finally { loading.value = false }
}
function openForm(row: any) {
  if (row) { editingId.value = row.id; form.value = { ...row, password:'' } }
  else { editingId.value = null; form.value = { username:'', realName:'', phone:'', roleCode:'ENTERPRISE_OPERATOR', password:'' } }
  showForm.value = true
}
async function doSave() {
  if (editingId.value) await updateUser(editingId.value, form.value)
  else await createUser(form.value)
  ElMessage.success('保存成功'); showForm.value = false; load()
}
async function doResetPwd(row: any) {
  await ElMessageBox.confirm('确定重置密码为123456？','确认')
  await resetPassword(row.id, '123456'); ElMessage.success('密码已重置')
}
async function doToggle(row: any) {
  const s = row.status === 1 ? 0 : 1
  await toggleUserStatus(row.id, s); ElMessage.success('操作成功'); load()
}
async function openAssignRoles(row: any) {
  currentUserId.value = row.id
  const [allRes, userRes] = await Promise.all([listRoles(), getUserRoles(row.id)])
  allRoles.value = allRes.data || []
  checkedRoleIds.value = (userRes.data||[]).map((r:Role)=>r.id)
  showRoles.value = true
}
async function doAssignRoles() {
  await assignUserRoles(currentUserId.value, checkedRoleIds.value)
  ElMessage.success('角色已分配'); showRoles.value = false
}
onMounted(load)
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.filter-bar { display:flex; align-items:center; margin-bottom:16px; }
</style>
