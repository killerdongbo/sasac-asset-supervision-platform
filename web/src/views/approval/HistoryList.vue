<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getApprovalHistory } from '@/api/approval'

const list = ref<any[]>([]); const loading = ref(false)
async function fetch() { loading.value = true; try { const res = await getApprovalHistory(); list.value = res.data || [] } finally { loading.value = false } }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>审批历史</h3></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="bizType" label="业务类型" width="120" />
      <el-table-column prop="bizId" label="业务单号" width="120" />
      <el-table-column label="结果" width="100"><template #default="{ row }"><el-tag :type="row.status === 'APPROVED' ? 'success' : 'danger'">{{ row.status === 'APPROVED' ? '通过' : '驳回' }}</el-tag></template></el-table-column>
      <el-table-column prop="updatedAt" label="完成时间" width="180" />
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
