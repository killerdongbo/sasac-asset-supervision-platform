<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAlerts, acknowledgeAlert, resolveAlert } from '@/api/alert'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false)

async function fetch() { loading.value = true; try { const res = await getAlerts(); list.value = res.data || [] } finally { loading.value = false } }

async function handleAck(row: any) { await acknowledgeAlert(row.id); ElMessage.success('已确认'); fetch() }
async function handleResolve(row: any) { await resolveAlert(row.id); ElMessage.success('已处理'); fetch() }

const typeMap: Record<string, { color: string; text: string }> = {
  MAINTENANCE_EXPIRY: { color: '#ff9800', text: '维保到期' },
  INSPECTION_OVERDUE: { color: '#f44336', text: '巡检超期' },
  BORROW_OVERDUE: { color: '#2196f3', text: '借用超期' },
  IDLE_ASSET: { color: '#4caf50', text: '闲置资产' }
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>预警中心</h3></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="预警类型" width="120">
        <template #default="{ row }"><el-tag :color="typeMap[row.alertType]?.color" effect="dark" size="small">{{ typeMap[row.alertType]?.text }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="title" label="预警标题" min-width="240" />
      <el-table-column prop="content" label="预警内容" min-width="300" show-overflow-tooltip />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 'ACTIVE' ? 'danger' : row.status === 'ACKNOWLEDGED' ? 'warning' : 'success'">{{ row.status === 'ACTIVE' ? '活跃' : row.status === 'ACKNOWLEDGED' ? '已确认' : '已处理' }}</el-tag></template></el-table-column>
      <el-table-column prop="createdAt" label="触发时间" width="180" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-if="row.status === 'ACTIVE'" link type="warning" @click="handleAck(row)">确认</el-button>
          <el-button v-if="row.status !== 'RESOLVED'" link type="primary" @click="handleResolve(row)">处理</el-button>
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
