<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTransfers, createTransfer } from '@/api/circulation'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false); const dialogVisible = ref(false)
const form = ref({ assetCode: '', fromOrgId: null as number | null, toOrgId: null as number | null, reason: '' })

async function fetch() { loading.value = true; try { const res = await getTransfers(); list.value = res.data } finally { loading.value = false } }
async function handleCreate() { await createTransfer(form.value); ElMessage.success('调拨成功'); dialogVisible.value = false; fetch() }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>资产调拨</h3><el-button type="primary" @click="dialogVisible = true">新增调拨</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetCode" label="资产编号" width="160" />
      <el-table-column prop="assetName" label="资产名称" min-width="160" />
      <el-table-column prop="fromOrgId" label="调出组织" width="120" />
      <el-table-column prop="toOrgId" label="调入组织" width="120" />
      <el-table-column prop="reason" label="调拨原因" min-width="200" />
      <el-table-column prop="createdAt" label="调拨时间" width="180" />
    </el-table>
    <el-dialog title="新增调拨" v-model="dialogVisible" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="资产编号"><el-input v-model="form.assetCode" placeholder="请输入资产编号" /></el-form-item>
        <el-form-item label="调出组织"><el-input-number v-model="form.fromOrgId" /></el-form-item>
        <el-form-item label="调入组织"><el-input-number v-model="form.toOrgId" /></el-form-item>
        <el-form-item label="原因"><el-input v-model="form.reason" /></el-form-item>
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
