<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRequests, createRequest } from '@/api/maintenance'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ assetId: null as number | null, providerId: null as number | null, faultDescription: '', priority: 'MEDIUM', sourceType: 'MANUAL' })

async function fetch() { loading.value = true; try { const res = await getRequests(); list.value = res.data } finally { loading.value = false } }

async function handleCreate() { await createRequest(form.value); ElMessage.success('维保申请已提交'); dialogVisible.value = false; fetch() }

const statusMap: Record<string, { type: string; text: string }> = { PENDING: { type: 'warning', text: '待处理' }, PROCESSING: { type: 'primary', text: '处理中' }, COMPLETED: { type: 'success', text: '已完成' } }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>维保维修管理</h3><el-button type="primary" @click="dialogVisible = true">新增维保申请</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetId" label="资产ID" width="100" />
      <el-table-column prop="faultDescription" label="故障描述" min-width="240" />
      <el-table-column label="优先级" width="100"><template #default="{ row }"><el-tag :type="row.priority === 'URGENT' ? 'danger' : row.priority === 'HIGH' ? 'warning' : 'info'">{{ row.priority }}</el-tag></template></el-table-column>
      <el-table-column label="来源" width="100"><template #default="{ row }">{{ row.sourceType === 'FROM_INSPECTION' ? '巡检转入' : '手动' }}</template></el-table-column>
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.text }}</el-tag></template></el-table-column>
    </el-table>

    <el-dialog title="新增维保申请" v-model="dialogVisible" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="资产ID"><el-input-number v-model="form.assetId" /></el-form-item>
        <el-form-item label="维保商ID"><el-input-number v-model="form.providerId" /></el-form-item>
        <el-form-item label="优先级"><el-select v-model="form.priority"><el-option label="低" value="LOW" /><el-option label="中" value="MEDIUM" /><el-option label="高" value="HIGH" /><el-option label="紧急" value="URGENT" /></el-select></el-form-item>
        <el-form-item label="故障描述"><el-input v-model="form.faultDescription" type="textarea" /></el-form-item>
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
