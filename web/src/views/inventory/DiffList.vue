<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAllDiffs, adjustDiff } from '@/api/inventory'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

async function fetch() { loading.value = true; try { const res = await getAllDiffs(); list.value = res.data || [] } finally { loading.value = false } }

const diffTypeMap: Record<string, string> = { SURPLUS: '盘盈', SHORTAGE: '盘亏', WRONG_LOCATION: '位置不符', STATUS_MISMATCH: '状态不符' }

async function handleAdjust(row: any) { await adjustDiff(row.id, { action: 'ADJUST' }); ElMessage.success('差异已调整'); fetch() }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>盘点差异报告</h3></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetId" label="资产ID" width="100" />
      <el-table-column label="差异类型" width="120"><template #default="{ row }"><el-tag :type="row.diffType === 'SHORTAGE' ? 'danger' : 'warning'">{{ diffTypeMap[row.diffType] }}</el-tag></template></el-table-column>
      <el-table-column prop="bookValue" label="账面值" width="200" />
      <el-table-column prop="actualValue" label="实际值" width="200" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 'OPEN' ? 'danger' : 'success'">{{ row.status === 'OPEN' ? '待处理' : '已调整' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="120"><template #default="{ row }"><el-button v-if="row.status === 'OPEN'" link type="primary" @click="handleAdjust(row)">调整台账</el-button></template></el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
