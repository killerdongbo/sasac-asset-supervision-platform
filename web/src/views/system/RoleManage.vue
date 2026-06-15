<template>
  <div class="role-manage">
    <div class="page-header">
      <h2>角色管理</h2>
      <el-button type="primary" @click="openForm(null)">新增角色</el-button>
    </div>

    <el-card>
      <el-table :data="roles" v-loading="loading" stripe>
        <el-table-column prop="roleCode" label="角色编码" width="160" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.roleType === 'SYSTEM' ? 'warning' : 'info'" size="small">
              {{ row.roleType === 'SYSTEM' ? '内置' : '自定义' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dataScope" label="数据范围" width="120">
          <template #default="{ row }">
            {{ dataScopeLabel(row.dataScope) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openPerm(row)">权限分配</el-button>
            <el-button link type="success" @click="openForm(row)" :disabled="row.roleType === 'SYSTEM'">编辑</el-button>
            <el-button link type="danger" @click="doDelete(row)" :disabled="row.roleType === 'SYSTEM'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showForm" :title="editingRole ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="角色编码">
          <el-input v-model="form.roleCode" :disabled="!!editingRole" />
        </el-form-item>
        <el-form-item label="角色名称"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="数据范围">
          <el-select v-model="form.dataScope" style="width: 100%">
            <el-option label="全部数据" value="ALL" />
            <el-option label="本租户数据" value="TENANT" />
            <el-option label="本组织及下级" value="ORG_AND_SUB" />
            <el-option label="仅本组织" value="ORG" />
            <el-option label="仅本人" value="SELF" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showPerm" title="权限分配" width="600px">
      <div class="perm-header">
        <span>角色：<b>{{ currentRole?.roleName }}</b></span>
        <el-checkbox v-model="checkAll" :indeterminate="indeterminate" @change="onCheckAll">全选</el-checkbox>
      </div>
      <el-checkbox-group v-model="checkedPermIds" @change="onPermChange">
        <div v-for="menu in menuPerms" :key="menu.id" class="perm-group">
          <div class="perm-menu">
            <el-checkbox :value="menu.id">{{ menu.permName }}</el-checkbox>
          </div>
          <div class="perm-buttons">
            <el-checkbox v-for="btn in getBtnPerms(menu.id)" :key="btn.id" :value="btn.id">
              {{ btn.permName }}
            </el-checkbox>
          </div>
        </div>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="showPerm = false">取消</el-button>
        <el-button type="primary" @click="doSavePerms">保存权限</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listRoles, createRole, updateRole, deleteRole, listPermissions, getRolePermissions, assignRolePermissions } from '@/api/permission'
import type { Role, Permission } from '@/api/permission'

const loading = ref(false)
const roles = ref<Role[]>([])
const allPerms = ref<Permission[]>([])

const showForm = ref(false)
const editingRole = ref<Role | null>(null)
const form = ref({ roleCode: '', roleName: '', dataScope: 'ORG', description: '' })

const showPerm = ref(false)
const currentRole = ref<Role | null>(null)
const checkedPermIds = ref<string[]>([])
const checkAll = ref(false)
const indeterminate = ref(false)

const menuPerms = computed(() => allPerms.value.filter(p => p.permType === 'MENU'))
const btnPerms = computed(() => allPerms.value.filter(p => p.permType === 'BUTTON'))

function getBtnPerms(parentId: string | number) {
  return btnPerms.value.filter(p => Number(p.parentId) === Number(parentId))
}

function dataScopeLabel(scope: string) {
  return { ALL: '全部', TENANT: '本租户', ORG_AND_SUB: '本组织及下级', ORG: '仅本组织', SELF: '仅本人' }[scope] || scope
}

async function loadRoles() {
  loading.value = true
  try {
    const res = await listRoles()
    roles.value = res.data || []
  } finally { loading.value = false }
}

async function loadPerms() {
  const res = await listPermissions()
  allPerms.value = res.data || []
}

function openForm(row: Role | null) {
  if (row) {
    editingRole.value = row
    form.value = { roleCode: row.roleCode, roleName: row.roleName, dataScope: row.dataScope, description: row.description || '' }
  } else {
    editingRole.value = null
    form.value = { roleCode: '', roleName: '', dataScope: 'ORG', description: '' }
  }
  showForm.value = true
}

async function doSave() {
  if (editingRole.value) {
    await updateRole(editingRole.value.id, form.value)
  } else {
    await createRole(form.value)
  }
  ElMessage.success('保存成功')
  showForm.value = false
  loadRoles()
}

async function doDelete(row: Role) {
  await ElMessageBox.confirm('确定删除该角色？', '确认')
  await deleteRole(row.id)
  ElMessage.success('已删除')
  loadRoles()
}

async function openPerm(row: Role) {
  currentRole.value = row
  const res = await getRolePermissions(row.id)
  const assigned = (res.data || []).map((p: Permission) => p.id)
  checkedPermIds.value = assigned
  updateCheckAllState()
  showPerm.value = true
}

function onCheckAll(val: boolean) {
  checkedPermIds.value = val ? allPerms.value.map(p => p.id) : []
  indeterminate.value = false
}

function onPermChange() {
  updateCheckAllState()
}

function updateCheckAllState() {
  const allIds = allPerms.value.map(p => p.id)
  const checked = checkedPermIds.value.length
  checkAll.value = checked === allIds.length
  indeterminate.value = checked > 0 && checked < allIds.length
}

async function doSavePerms() {
  if (!currentRole.value) return
  await assignRolePermissions(currentRole.value.id, checkedPermIds.value)
  ElMessage.success('权限已保存')
  showPerm.value = false
}

onMounted(() => { loadRoles(); loadPerms() })
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.perm-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.perm-group { margin-bottom: 8px; }
.perm-menu { margin-bottom: 6px; font-weight: 500; }
.perm-buttons { display: flex; flex-wrap: wrap; gap: 12px; padding-left: 24px; }
</style>
