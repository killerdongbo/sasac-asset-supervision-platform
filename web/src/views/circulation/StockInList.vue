<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getStockIns, createStockIn } from '@/api/circulation'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false); const dialogVisible = ref(false)
const form = ref({ assetId: null as number | null, fromOrgId: null as number | null, toOrgId: null as number | null, locationId: null as number | null, remark: '' })

async function fetch() { loading.value = true; try { const res = await getStockIns(); list.value = res.data } finally { loading.value = false } }
async function handleCreate() { await createStockIn(form.value); ElMessage.success('入库成功'); dialogVisible.value = false; fetch() }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>入库管理</h3><el-button type="primary" @click="dialogVisible = true">新增入库</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetId" label="资产ID" width="120" />
      <el-table-column prop="fromOrgId" label="来源组织" width="120" />
      <el-table-column prop="toOrgId" label="目标组织" width="120" />
      <el-table-column prop="remark" label="备注" min-width="200" />
      <el-table-column prop="createdAt" label="入库时间" width="180" />
    </el-table>
    <el-dialog title="新增入库" v-model="dialogVisible" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="资产ID"><el-input-number v-model="form.assetId" /></el-form-item>
        <el-form-item label="目标组织"><el-input-number v-model="form.toOrgId" /></el-form-item>
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
