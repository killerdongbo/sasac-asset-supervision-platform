<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAnomalies, resolveAnomaly, transferToMaintenance } from '@/api/inspection'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

async function fetch() { loading.value = true; try { const res = await getAnomalies(); list.value = res.data || [] } finally { loading.value = false } }

async function handleResolve(row: any) { await resolveAnomaly(row.id, 'RECTIFY'); ElMessage.success('已安排整改'); fetch() }
async function handleTransfer(row: any) { await transferToMaintenance(row.id); ElMessage.success('已转维保维修'); fetch() }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>巡检异常处理</h3></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetId" label="资产ID" width="100" />
      <el-table-column prop="anomalyType" label="异常类型" width="140" />
      <el-table-column prop="description" label="异常描述" min-width="240" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 'OPEN' ? 'danger' : 'success'">{{ row.status === 'OPEN' ? '待处理' : '已处理' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-if="row.status === 'OPEN'" link type="warning" @click="handleResolve(row)">安排整改</el-button>
          <el-button v-if="row.status === 'OPEN'" link type="primary" @click="handleTransfer(row)">转维修</el-button>
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
