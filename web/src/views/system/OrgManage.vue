<template>
  <div class="org-page">
    <div class="page-header"><h2>组织架构</h2><el-button type="primary" @click="openForm(null)">新增组织</el-button></div>
    <el-card>
      <el-table :data="orgList" v-loading="loading" stripe row-key="id" default-expand-all :tree-props="{children:'children'}">
        <el-table-column prop="name" label="名称" min-width="240"/>
        <el-table-column label="类型" width="120">
          <template #default="{row}">
            <el-tag :type="orgTag(row.orgType)">{{orgTypeLabel(row.orgType)}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orgCode" label="编码" width="140"/>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" @click="openForm(row)">编辑</el-button>
            <el-button link type="success" @click="openForm({parentId:row.id})">新增下级</el-button>
            <el-button link type="danger" @click="doDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showForm" :title="editingId?'编辑组织':'新增组织'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.orgType" style="width:100%">
            <el-option label="国资委" value="SASAC"/><el-option label="集团" value="GROUP"/>
            <el-option label="企业" value="ENTERPRISE"/><el-option label="部门" value="DEPARTMENT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="编码"><el-input v-model="form.orgCode"/></el-form-item>
        <el-form-item label="上级组织">
          <el-tree-select v-model="form.parentId" :data="orgTree" :props="{label:'name',value:'id',children:'children'}" check-strictly clearable placeholder="留空为根节点" style="width:100%"/>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="showForm=false">取消</el-button><el-button type="primary" @click="doSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrganizations, createOrganization, updateOrganization, deleteOrganization } from '@/api/organization'
import type { Organization } from '@/api/organization'

const orgList = ref<any[]>([]); const loading = ref(false)
const showForm = ref(false); const editingId = ref<string|null>(null)
const form = ref<Organization>({ name:'', orgType:'ENTERPRISE', orgCode:'', parentId:null })

const orgTree = computed(() => buildTree(orgList.value))

function orgTag(t: string) { return { SASAC:'danger', GROUP:'warning', ENTERPRISE:'', DEPARTMENT:'info' }[t]||'' }
function orgTypeLabel(t: string) { return { SASAC:'国资委', GROUP:'集团', ENTERPRISE:'企业', DEPARTMENT:'部门' }[t]||t }

function buildTree(list: any[]): any[] {
  const map = new Map(); const roots: any[] = []
  list.forEach(item => { map.set(item.id, {...item, children:[]}) })
  map.forEach(item => {
    if (item.parentId && map.has(item.parentId)) map.get(item.parentId).children.push(item)
    else roots.push(item)
  })
  return roots
}

async function load() {
  loading.value = true
  try { const r = await getOrganizations(); orgList.value = r.data || [] } finally { loading.value = false }
}
function openForm(row: any) {
  if (row && row.id) { editingId.value = row.id; form.value = { name:row.name, orgType:row.orgType, orgCode:row.orgCode, parentId:row.parentId } }
  else { editingId.value = null; form.value = { name:'', orgType:'ENTERPRISE', orgCode:'', parentId: row?.parentId ?? null } }
  showForm.value = true
}
async function doSave() {
  if (editingId.value) await updateOrganization(editingId.value, form.value)
  else await createOrganization(form.value)
  ElMessage.success('保存成功'); showForm.value = false; load()
}
async function doDelete(row: any) {
  await ElMessageBox.confirm('确定删除？','确认',{type:'warning'})
  await deleteOrganization(row.id); ElMessage.success('已删除'); load()
}
onMounted(load)
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
</style>
