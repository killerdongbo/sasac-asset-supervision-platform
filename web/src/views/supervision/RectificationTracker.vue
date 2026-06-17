<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRectifications, verifyRectification, checkOverdueRectifications } from '@/api/supervision'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const verifyDialogVisible = ref(false)
const currentRect = ref<any>(null)
const verifyResult = ref('')

const statusMap: Record<string, { type: string; text: string }> = {
  PENDING: { type: 'info', text: '待处理' },
  IN_PROGRESS: { type: 'warning', text: '进行中' },
  COMPLETED: { type: 'success', text: '已完成' },
  ESCALATED: { type: 'danger', text: '已升级' }
}

async function fetch() {
  loading.value = true
  try {
    const res = await getRectifications()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function isOverdue(row: any) {
  return row.status === 'PENDING' && row.deadline && new Date(row.deadline) < new Date()
}

async function handleVerify(row: any) {
  currentRect.value = row
  verifyResult.value = ''
  verifyDialogVisible.value = true
}

async function confirmVerify() {
  if (!verifyResult.value.trim()) {
    ElMessage.warning('请输入验证结果')
    return
  }
  try {
    await verifyRectification(currentRect.value.id, verifyResult.value)
    ElMessage.success('验证完成')
    verifyDialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

async function handleCheckOverdue() {
  try {
    const res = await checkOverdueRectifications(1)
    const count = res.data?.length || 0
    if (count > 0) {
      ElMessage.success(`已自动升级 ${count} 条超期整改任务`)
    } else {
      ElMessage.info('暂无超期整改任务')
    }
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h3>整改跟踪</h3>
      <el-button type="warning" @click="handleCheckOverdue">检查超期整改</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="taskTitle" label="任务标题" min-width="200" />
      <el-table-column prop="assigneeName" label="负责人" width="100" />
      <el-table-column label="截止日期" width="120">
        <template #default="{ row }">
          <span :style="isOverdue(row) ? { color: '#f56c6c', fontWeight: 'bold' } : {}">
            {{ row.deadline }}
            <el-tag v-if="isOverdue(row)" type="danger" size="small" effect="dark">超期</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status]?.type || 'info'">
            {{ statusMap[row.status]?.text || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rectificationMeasure" label="整改措施" min-width="240" show-overflow-tooltip />
      <el-table-column prop="resultVerification" label="验证结果" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status !== 'COMPLETED'" link type="primary" @click="handleVerify(row)">验证整改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="verifyDialogVisible" title="验证整改" width="500px">
      <p style="margin-bottom: 12px; color: #666;">任务：{{ currentRect?.taskTitle }}</p>
      <el-input v-model="verifyResult" type="textarea" :rows="4" placeholder="请输入验证结果" />
      <template #footer>
        <el-button @click="verifyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmVerify">确认验证</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
