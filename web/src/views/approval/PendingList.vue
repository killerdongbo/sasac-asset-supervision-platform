<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPendingApprovals, approve } from '@/api/approval'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false)

async function fetch() { loading.value = true; try { const res = await getPendingApprovals({ tenantId: 100, roleCode: 'DEPT_MANAGER' }); list.value = res.data || [] } finally { loading.value = false } }

async function handleApprove(row: any) { await approve(row.id, { approverId: 1, approved: true, remark: '同意' }); ElMessage.success('已审批通过'); fetch() }
async function handleReject(row: any) { await approve(row.id, { approverId: 1, approved: false, remark: '退回修改' }); ElMessage.warning('已驳回'); fetch() }

const bizTypeMap: Record<string, string> = { DISPOSAL: '资产报废', TRANSFER: '资产调拨', PURCHASE: '采购申请', INVENTORY_DIFF: '盘点差异', MAINTENANCE: '维保维修', REPORT: '监管上报' }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>待我审批</h3></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="业务类型" width="120"><template #default="{ row }">{{ bizTypeMap[row.bizType] || row.bizType }}</template></el-table-column>
      <el-table-column prop="bizId" label="业务单号" width="120" />
      <el-table-column prop="submitterId" label="提交人" width="100" />
      <el-table-column label="当前节点" width="80"><template #default="{ row }">第{{ row.currentNode }}步</template></el-table-column>
      <el-table-column prop="createdAt" label="提交时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="success" @click="handleApprove(row)">通过</el-button>
          <el-button link type="danger" @click="handleReject(row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
