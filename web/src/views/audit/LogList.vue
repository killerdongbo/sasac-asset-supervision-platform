<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getOperationLogs } from '@/api/audit'

const list = ref<any[]>([]); const loading = ref(false)
const filter = ref({ targetType: '', keyword: '' })

async function fetch() { loading.value = true; try { const res = await getOperationLogs(filter.value); list.value = res.data || [] } finally { loading.value = false } }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>操作日志</h3></div>
    <div class="toolbar">
      <el-select v-model="filter.targetType" placeholder="对象类型" clearable style="width:160px" @change="fetch">
        <el-option label="资产" value="Asset" /><el-option label="巡检任务" value="InspectionTask" /><el-option label="盘点任务" value="InventoryTask" /><el-option label="报表" value="Report" />
      </el-select>
      <el-input v-model="filter.keyword" placeholder="搜索" clearable style="width:200px" @keyup.enter="fetch" />
      <el-button type="primary" @click="fetch">查询</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe size="small">
      <el-table-column prop="action" label="操作" width="100" />
      <el-table-column prop="targetType" label="对象类型" width="120" />
      <el-table-column prop="targetId" label="对象ID" width="100" />
      <el-table-column prop="operatorName" label="操作人" width="120" />
      <el-table-column prop="ip" label="IP" width="140" />
      <el-table-column prop="createdAt" label="时间" width="180" />
      <el-table-column label="详情" min-width="200">
        <template #default="{ row }"><el-popover placement="left" :width="400" trigger="click"><template #reference><el-button link type="primary">查看详情</el-button></template><div style="font-size:12px"><p>变更前: {{ row.beforeData }}</p><p>变更后: {{ row.afterData }}</p></div></el-popover></template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
