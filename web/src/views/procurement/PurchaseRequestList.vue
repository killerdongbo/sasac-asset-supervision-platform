<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPurchaseRequests, createPurchaseRequest, acceptPurchase, convertToAsset } from '@/api/procurement'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ assetName: '', quantity: 1, budget: 0, supplierId: null as number | null, requestReason: '' })

async function fetch() { loading.value = true; try { const res = await getPurchaseRequests(); list.value = res.data } finally { loading.value = false } }

async function handleCreate() {
  await createPurchaseRequest(form.value); ElMessage.success('采购申请已提交'); dialogVisible.value = false; fetch()
}

async function handleAccept(row: any) {
  await acceptPurchase(row.id, { passed: true, remark: '验收合格' }); ElMessage.success('验收通过'); fetch()
}

async function handleConvert(row: any) {
  await convertToAsset(row.id); ElMessage.success('已转为资产'); fetch()
}

const statusMap: Record<string, { type: string; text: string }> = {
  PENDING: { type: 'warning', text: '待审批' },
  APPROVED: { type: 'primary', text: '已批准' },
  ACCEPTED: { type: 'success', text: '已验收' },
  REJECTED: { type: 'danger', text: '已驳回' }
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>采购申请</h3><el-button type="primary" @click="dialogVisible = true">新增申请</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetName" label="资产名称" min-width="160" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column label="预算(元)" width="140"><template #default="{ row }">{{ row.budget?.toLocaleString() }}</template></el-table-column>
      <el-table-column prop="requestReason" label="申请原因" min-width="180" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.text }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-if="row.status === 'APPROVED'" link type="primary" @click="handleAccept(row)">验收</el-button>
          <el-button v-if="row.status === 'ACCEPTED'" link type="success" @click="handleConvert(row)">转资产</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="新增采购申请" v-model="dialogVisible" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="资产名称"><el-input v-model="form.assetName" /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="form.quantity" :min="1" /></el-form-item>
        <el-form-item label="预算(元)"><el-input-number v-model="form.budget" :min="0" :step="1000" /></el-form-item>
        <el-form-item label="申请原因"><el-input v-model="form.requestReason" type="textarea" /></el-form-item>
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
