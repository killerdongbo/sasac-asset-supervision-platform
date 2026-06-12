<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTasks, getMyTasks, createTask } from '@/api/inspection'
import { ElMessage } from 'element-plus'

const router = useRouter()
const list = ref<any[]>([])
const loading = ref(false)
const tab = ref('all')
const dialogVisible = ref(false)
const form = ref({ taskName: '', assigneeId: null as number | null, startDate: '', endDate: '', assetScope: '' })

async function fetch() {
  loading.value = true
  try { const res = tab.value === 'my' ? await getMyTasks() : await getTasks(); list.value = res.data || [] } finally { loading.value = false }
}

async function handleCreate() {
  await createTask({ ...form.value, assetScope: form.value.assetScope.split(',').map(s => parseInt(s.trim())).filter(Boolean) })
  ElMessage.success('巡检任务已创建'); dialogVisible.value = false; fetch()
}

const statusMap: Record<string, string> = { PENDING: '待执行', IN_PROGRESS: '执行中', COMPLETED: '已完成' }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h3>巡检管理</h3>
      <el-button type="primary" @click="dialogVisible = true">制定巡检任务</el-button>
    </div>
    <el-tabs v-model="tab" @tab-change="fetch">
      <el-tab-pane label="全部任务" name="all" />
      <el-tab-pane label="我的巡检任务" name="my" />
    </el-tabs>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="taskName" label="任务名称" min-width="200" />
      <el-table-column prop="startDate" label="开始日期" width="120" />
      <el-table-column prop="endDate" label="截止日期" width="120" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 'COMPLETED' ? 'success' : row.status === 'IN_PROGRESS' ? 'warning' : 'info'">{{ statusMap[row.status] }}</el-tag></template></el-table-column>
      <el-table-column label="进度" width="160"><template #default="{ row }">{{ row.completedCount || 0 }} / {{ row.totalCount || 0 }}</template></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/inspection-tasks/${row.id}/execute`)">执行巡检</el-button>
          <el-button link type="primary" @click="router.push(`/inspection-anomalies?taskId=${row.id}`)">查看异常</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="制定巡检任务" v-model="dialogVisible" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="任务名称"><el-input v-model="form.taskName" /></el-form-item>
        <el-form-item label="巡检人ID"><el-input-number v-model="form.assigneeId" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" /></el-form-item>
        <el-form-item label="截止日期"><el-date-picker v-model="form.endDate" type="date" /></el-form-item>
        <el-form-item label="资产范围"><el-input v-model="form.assetScope" placeholder="资产ID列表，逗号分隔" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleCreate">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
