<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDepreciationRecords, runDepreciation } from '@/api/depreciation'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

async function fetch() { loading.value = true; try { const res = await getDepreciationRecords(); list.value = res.data } finally { loading.value = false } }

async function handleRun(assetId: number) { await runDepreciation(assetId); ElMessage.success('折旧计提完成'); fetch() }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>折旧管理</h3></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="assetId" label="资产ID" width="120" />
      <el-table-column label="折旧金额(元)" width="140"><template #default="{ row }">{{ row.depreciationAmount?.toLocaleString() }}</template></el-table-column>
      <el-table-column label="折旧前净值(元)" width="150"><template #default="{ row }">{{ row.beforeValue?.toLocaleString() }}</template></el-table-column>
      <el-table-column label="折旧后净值(元)" width="150"><template #default="{ row }">{{ row.afterValue?.toLocaleString() }}</template></el-table-column>
      <el-table-column prop="period" label="计提期间" width="120" />
      <el-table-column prop="depreciationDate" label="计提日期" width="120" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }"><el-button link type="primary" @click="handleRun(row.assetId)">重新计提</el-button></template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
