<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getFindings, recordFinding, assignRectification } from '@/api/supervision'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const rectDialogVisible = ref(false)
const currentFinding = ref<any>(null)
const findingFormRef = ref<any>(null)
const rectFormRef = ref<any>(null)

const form = ref({
  tenantId: 1,
  planId: undefined as number | undefined,
  title: '',
  severity: 'MEDIUM',
  description: '',
  evidenceIds: ''
})

const rectForm = ref({
  tenantId: 1,
  taskTitle: '',
  assigneeId: undefined as number | undefined,
  assigneeName: '',
  deadline: '',
  rectificationMeasure: ''
})

const severityMap: Record<string, { type: string; text: string }> = {
  CRITICAL: { type: 'danger', text: '严重' },
  HIGH: { type: 'warning', text: '高危' },
  MEDIUM: { type: '', text: '中危' },
  LOW: { type: 'info', text: '低危' }
}

async function fetch() {
  loading.value = true
  try {
    const res = await getFindings()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.value = { tenantId: 1, planId: undefined, title: '', severity: 'MEDIUM', description: '', evidenceIds: '' }
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await findingFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await recordFinding(form.value)
    ElMessage.success('记录成功')
    dialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function openRectDialog(row: any) {
  currentFinding.value = row
  rectForm.value = { tenantId: 1, taskTitle: '', assigneeId: undefined, assigneeName: '', deadline: '', rectificationMeasure: '' }
  rectDialogVisible.value = true
}

async function handleAssignRect() {
  const valid = await rectFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await assignRectification(currentFinding.value.id, rectForm.value)
    ElMessage.success('整改任务已分配')
    rectDialogVisible.value = false
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
      <h3>审计发现问题看板</h3>
      <el-button type="primary" @click="openCreate">记录问题</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="findingNo" label="问题编号" width="160" />
      <el-table-column prop="title" label="问题标题" min-width="200" />
      <el-table-column label="严重程度" width="90">
        <template #default="{ row }">
          <el-tag :type="severityMap[row.severity]?.type" size="small">
            {{ severityMap[row.severity]?.text || row.severity }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="问题描述" min-width="240" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'OPEN' ? 'danger' : row.status === 'RECTIFYING' ? 'warning' : row.status === 'VERIFIED' ? 'primary' : 'success'">
            {{ row.status === 'OPEN' ? '待整改' : row.status === 'RECTIFYING' ? '整改中' : row.status === 'VERIFIED' ? '已验证' : '已关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="发现时间" width="180" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button v-if="row.status === 'OPEN'" link type="primary" @click="openRectDialog(row)">创建整改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="记录审计问题" width="600px">
      <el-form ref="findingFormRef" :model="form" label-width="100px">
        <el-form-item label="问题标题" prop="title" :rules="[{ required: true, message: '请输入问题标题' }]">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="严重程度" prop="severity" :rules="[{ required: true, message: '请选择严重程度' }]">
          <el-select v-model="form.severity">
            <el-option label="严重" value="CRITICAL" />
            <el-option label="高危" value="HIGH" />
            <el-option label="中危" value="MEDIUM" />
            <el-option label="低危" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="关联计划ID" prop="planId">
          <el-input-number v-model="form.planId" :min="1" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rectDialogVisible" title="创建整改任务" width="600px">
      <el-form ref="rectFormRef" :model="rectForm" label-width="120px">
        <el-form-item label="任务标题" prop="taskTitle" :rules="[{ required: true, message: '请输入任务标题' }]">
          <el-input v-model="rectForm.taskTitle" />
        </el-form-item>
        <el-form-item label="负责人ID" prop="assigneeId">
          <el-input-number v-model="rectForm.assigneeId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="负责人姓名" prop="assigneeName">
          <el-input v-model="rectForm.assigneeName" />
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker v-model="rectForm.deadline" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="整改措施" prop="rectificationMeasure">
          <el-input v-model="rectForm.rectificationMeasure" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignRect">分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
