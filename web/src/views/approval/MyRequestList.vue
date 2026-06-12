<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyRequests } from '@/api/approval'

const list = ref<any[]>([]); const loading = ref(false)

async function fetch() { loading.value = true; try { const res = await getMyRequests(1); list.value = res.data || [] } finally { loading.value = false } }

const statusMap: Record<string, { type: string; text: string }> = { PENDING: { type: 'warning', text: '审批中' }, APPROVED: { type: 'success', text: '已通过' }, REJECTED: { type: 'danger', text: '已驳回' } }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>我发起的申请</h3></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="bizType" label="业务类型" width="120" />
      <el-table-column prop="bizId" label="业务单号" width="120" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.text }}</el-tag></template></el-table-column>
      <el-table-column label="当前节点" width="80"><template #default="{ row }">第{{ row.currentNode }}步</template></el-table-column>
      <el-table-column prop="createdAt" label="提交时间" width="180" />
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
