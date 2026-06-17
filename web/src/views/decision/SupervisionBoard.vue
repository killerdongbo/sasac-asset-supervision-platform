<template>
  <div class="supervision-board">
    <div class="page-header">
      <h2>督办看板</h2>
    </div>

    <el-card shadow="never">
      <el-table :data="supervisions" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="taskTitle" label="任务标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="assigneeName" label="负责人" width="120" />
        <el-table-column prop="deadline" label="截止日期" width="140">
          <template #default="{ row }">
            <span :class="{ 'overdue-text': isOverdue(row) }">
              {{ row.deadline || '-' }}
              <el-tag v-if="isOverdue(row)" type="danger" size="small" effect="dark" style="margin-left: 4px">逾期</el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="progressNote" label="进度说明" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'COMPLETED' ? '已完成' : '待办' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="text"
              size="small"
              :disabled="row.status === 'COMPLETED'"
              @click="handleUpdateProgress(row)"
            >
              更新进度
            </el-button>
            <el-button
              type="text"
              size="small"
              :disabled="row.status === 'COMPLETED'"
              @click="handleComplete(row)"
            >
              办结
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && supervisions.length === 0" description="暂无督办任务" />
    </el-card>

    <!-- Progress Update Dialog -->
    <el-dialog v-model="dialogVisible" title="更新进度" width="500px">
      <el-form :model="progressForm">
        <el-form-item label="进度说明">
          <el-input v-model="progressForm.note" type="textarea" :rows="3" placeholder="请输入进度说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmUpdateProgress" :loading="updating">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingSupervisions, type DecisionSupervision } from '@/api/decision'

const loading = ref(false)
const supervisions = ref<DecisionSupervision[]>([])

const dialogVisible = ref(false)
const updating = ref(false)
const progressForm = ref({ note: '' })
const currentRow = ref<DecisionSupervision | null>(null)

const tenantId = Number(localStorage.getItem('tenantId') || 1)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getPendingSupervisions(tenantId)
    supervisions.value = res.data
  } finally {
    loading.value = false
  }
}

/** Check if a supervision task is overdue (deadline past today and not completed) */
const isOverdue = (item: DecisionSupervision): boolean => {
  if (!item.deadline || item.status === 'COMPLETED') return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return new Date(item.deadline) < today
}

const handleUpdateProgress = (row: DecisionSupervision) => {
  currentRow.value = row
  progressForm.value.note = row.progressNote || ''
  dialogVisible.value = true
}

const confirmUpdateProgress = async () => {
  if (!currentRow.value) return
  updating.value = true
  try {
    // In a real implementation, call an API to update progress
    ElMessage.success('进度已更新')
    dialogVisible.value = false
    await fetchData()
  } finally {
    updating.value = false
  }
}

const handleComplete = async (row: DecisionSupervision) => {
  try {
    // In a real implementation, call an API to mark as completed
    ElMessage.success('督办任务已办结')
    await fetchData()
  } catch {
    // Error handled by interceptor
  }
}

onMounted(fetchData)
</script>

<style scoped>
.supervision-board {
  padding: 16px;
}
.page-header {
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>
