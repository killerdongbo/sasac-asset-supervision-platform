<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDisposals, createDisposal } from '@/api/circulation'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false); const dialogVisible = ref(false)
const form = ref({ assetCode: '', disposalType: 'SCRAP', disposalValue: 0, reason: '' })

async function fetch() { loading.value = true; try { const res = await getDisposals(); list.value = res.data } finally { loading.value = false } }
async function handleCreate() { await createDisposal(form.value); ElMessage.success('报废申请已提交'); dialogVisible.value = false; fetch() }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>资产报废</h3><el-button type="primary" @click="dialogVisible = true">新增报废</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetCode" label="资产编号" width="160" />
      <el-table-column prop="assetName" label="资产名称" min-width="160" />
      <el-table-column label="处置类型" width="100"><template #default="{ row }">{{ row.disposalType === 'SCRAP' ? '报废' : row.disposalType === 'SELL' ? '出售' : '核销' }}</template></el-table-column>
      <el-table-column label="处置价值(元)" width="140"><template #default="{ row }">{{ row.disposalValue?.toLocaleString() }}</template></el-table-column>
      <el-table-column label="损益(元)" width="140"><template #default="{ row }"><span :style="{ color: row.gainLoss > 0 ? 'green' : 'red' }">{{ row.gainLoss?.toLocaleString() }}</span></template></el-table-column>
      <el-table-column prop="reason" label="原因" min-width="200" />
    </el-table>
    <el-dialog title="新增报废" v-model="dialogVisible" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="资产编号"><el-input v-model="form.assetCode" placeholder="请输入资产编号" /></el-form-item>
        <el-form-item label="处置类型"><el-select v-model="form.disposalType"><el-option label="报废" value="SCRAP" /><el-option label="出售" value="SELL" /><el-option label="核销" value="WRITE_OFF" /></el-select></el-form-item>
        <el-form-item label="处置价值"><el-input-number v-model="form.disposalValue" :min="0" :step="1000" /></el-form-item>
        <el-form-item label="原因"><el-input v-model="form.reason" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleCreate">提交</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
