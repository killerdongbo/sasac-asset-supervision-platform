<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAssignments, createAssignment } from '@/api/circulation'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false); const dialogVisible = ref(false)
const form = ref({ assetCode: '', toUserId: null as number | null, toOrgId: null as number | null, expectedReturnDate: '', remark: '' })

async function fetch() { loading.value = true; try { const res = await getAssignments(); list.value = res.data } finally { loading.value = false } }
async function handleCreate() { await createAssignment(form.value); ElMessage.success('领用成功'); dialogVisible.value = false; fetch() }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>资产领用</h3><el-button type="primary" @click="dialogVisible = true">新增领用</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetCode" label="资产编号" width="160" />
      <el-table-column prop="assetName" label="资产名称" min-width="160" />
      <el-table-column prop="toUserId" label="领用人ID" width="120" />
      <el-table-column prop="toOrgId" label="领用部门" width="120" />
      <el-table-column prop="expectedReturnDate" label="预计归还" width="120" />
      <el-table-column prop="remark" label="备注" min-width="200" />
    </el-table>
    <el-dialog title="新增领用" v-model="dialogVisible" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="资产编号"><el-input v-model="form.assetCode" placeholder="请输入资产编号" /></el-form-item>
        <el-form-item label="领用人ID"><el-input-number v-model="form.toUserId" /></el-form-item>
        <el-form-item label="领用部门"><el-input-number v-model="form.toOrgId" /></el-form-item>
        <el-form-item label="预计归还"><el-date-picker v-model="form.expectedReturnDate" type="date" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleCreate">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
