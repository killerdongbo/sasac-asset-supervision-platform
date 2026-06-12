<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRoles, createRole } from '@/api/system'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false); const dialogVisible = ref(false)
const form = ref({ roleCode: '', roleName: '', description: '' })

async function fetch() { loading.value = true; try { const res = await getRoles(); list.value = res.data } finally { loading.value = false } }
async function handleCreate() { await createRole(form.value); ElMessage.success('角色创建成功'); dialogVisible.value = false; fetch() }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>角色管理</h3><el-button type="primary" @click="dialogVisible = true">新增角色</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="roleCode" label="角色编码" width="180" />
      <el-table-column prop="roleName" label="角色名称" min-width="200" />
      <el-table-column prop="description" label="描述" min-width="240" />
    </el-table>
    <el-dialog title="新增角色" v-model="dialogVisible" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="编码"><el-input v-model="form.roleCode" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleCreate">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
