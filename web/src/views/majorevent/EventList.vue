<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getEvents, createEvent, approveEvent, trackEvent, resolveEvent, deleteEvent } from '@/api/majorevent'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const list = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 20

const query = ref({
  eventType: '',
  status: ''
})

const dialogVisible = ref(false)
const formRef = ref<any>(null)
const form = ref({
  eventType: '',
  title: '',
  description: '',
  impactAssessment: '',
  handlingPlan: ''
})

const trackDialogVisible = ref(false)
const resolveDialogVisible = ref(false)
const currentEvent = ref<any>(null)
const trackProgress = ref('')
const resolveResult = ref('')

const eventTypeOptions = [
  { value: 'MERGER', label: '合并' },
  { value: 'DIVISION', label: '分立' },
  { value: 'RESTRUCTURING', label: '改制' },
  { value: 'LISTING', label: '上市' },
  { value: 'BANKRUPTCY', label: '破产' },
  { value: 'MAJOR_LITIGATION', label: '重大诉讼' },
  { value: 'MAJOR_GUARANTEE', label: '重大担保' },
  { value: 'OTHER', label: '其他' }
]

const statusOptions = [
  { value: 'REPORTED', label: '已报告' },
  { value: 'APPROVED', label: '已审批' },
  { value: 'TRACKING', label: '跟踪中' },
  { value: 'RESOLVED', label: '已解决' }
]

const eventTypeColorMap: Record<string, string> = {
  MERGER: '#409eff',
  DIVISION: '#67c23a',
  RESTRUCTURING: '#e6a23c',
  LISTING: '#f56c6c',
  BANKRUPTCY: '#909399',
  MAJOR_LITIGATION: '#b37feb',
  MAJOR_GUARANTEE: '#ff85c0',
  OTHER: '#909399'
}

const statusMap: Record<string, { type: string; text: string }> = {
  REPORTED: { type: 'warning', text: '已报告' },
  APPROVED: { type: 'primary', text: '已审批' },
  TRACKING: { type: 'info', text: '跟踪中' },
  RESOLVED: { type: 'success', text: '已解决' }
}

async function fetch() {
  loading.value = true
  try {
    const params: any = { page: currentPage.value, size: pageSize }
    if (query.value.eventType) params.eventType = query.value.eventType
    if (query.value.status) params.status = query.value.status
    const res = await getEvents(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetch()
}

function handleReset() {
  query.value = { eventType: '', status: '' }
  currentPage.value = 1
  fetch()
}

function openCreate() {
  form.value = { eventType: '', title: '', description: '', impactAssessment: '', handlingPlan: '' }
  dialogVisible.value = true
}

async function handleCreate() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await createEvent(form.value)
    ElMessage.success('事项报告成功')
    dialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function handleApprove(row: any) {
  ElMessageBox.confirm('确认审批通过此事项？', '提示', { type: 'info' }).then(async () => {
    try {
      await approveEvent(row.id)
      ElMessage.success('审批通过')
      fetch()
    } catch (e: any) {
      ElMessage.error(e.response?.data?.error || '操作失败')
    }
  }).catch(() => {})
}

function openTrack(row: any) {
  currentEvent.value = row
  trackProgress.value = ''
  trackDialogVisible.value = true
}

async function handleTrack() {
  if (!trackProgress.value.trim()) {
    ElMessage.warning('请输入进展内容')
    return
  }
  try {
    await trackEvent(currentEvent.value.id, trackProgress.value)
    ElMessage.success('进展已记录')
    trackDialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function openResolve(row: any) {
  currentEvent.value = row
  resolveResult.value = ''
  resolveDialogVisible.value = true
}

async function handleResolve() {
  if (!resolveResult.value.trim()) {
    ElMessage.warning('请输入解决结果')
    return
  }
  try {
    await resolveEvent(currentEvent.value.id)
    ElMessage.success('事项已解决')
    resolveDialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除此事项？', '提示', { type: 'warning' })
  try {
    await deleteEvent(row.id)
    ElMessage.success('删除成功')
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function goDetail(row: any) {
  router.push(`/major-events/${row.id}`)
}

function formatTime(time: string): string {
  return time?.replace('T', ' ').substring(0, 19) || ''
}

function getEventTypeLabel(type: string): string {
  return eventTypeOptions.find(o => o.value === type)?.label || type
}

function _formatCurrency(val: number | null | undefined): string {
  if (val == null) return '-'
  return '¥' + Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetch()
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h3>重大事项管理</h3>
      <el-button type="primary" @click="openCreate">报告事项</el-button>
    </div>

    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="事项类型">
          <el-select v-model="query.eventType" placeholder="全部类型" clearable style="width: 160px">
            <el-option v-for="o in eventTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="list" v-loading="loading" border stripe style="margin-top: 16px" @row-click="goDetail" highlight-current-row>
      <el-table-column prop="eventNo" label="事项编号" width="180" />
      <el-table-column label="事项类型" width="120">
        <template #default="{ row }">
          <el-tag :color="eventTypeColorMap[row.eventType] || '#909399'" style="color: #fff" size="small">
            {{ getEventTypeLabel(row.eventType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="事项标题" min-width="200" show-overflow-tooltip />
      <el-table-column label="报告时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.reportedAt || row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">
            {{ statusMap[row.status]?.text || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'REPORTED'" link type="primary" @click.stop="handleApprove(row)">审批</el-button>
          <el-button v-if="row.status === 'APPROVED'" link type="warning" @click.stop="openTrack(row)">跟踪</el-button>
          <el-button v-if="row.status === 'TRACKING'" link type="success" @click.stop="openResolve(row)">解决</el-button>
          <el-button link type="primary" @click.stop="goDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'REPORTED'" link type="danger" @click.stop="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > pageSize"
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next, total"
      @current-change="handlePageChange"
      style="margin-top: 16px; justify-content: center;"
    />

    <el-dialog v-model="dialogVisible" title="报告事项" width="700px">
      <el-form ref="formRef" :model="form" label-width="120px">
        <el-form-item label="事项类型" prop="eventType" :rules="[{ required: true, message: '请选择事项类型' }]">
          <el-select v-model="form.eventType" style="width: 100%">
            <el-option v-for="o in eventTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="事项标题" prop="title" :rules="[{ required: true, message: '请输入事项标题' }]">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="事项描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述事项内容" />
        </el-form-item>
        <el-form-item label="影响评估" prop="impactAssessment">
          <el-input v-model="form.impactAssessment" type="textarea" :rows="3" placeholder="评估对企业的影响" />
        </el-form-item>
        <el-form-item label="处理方案" prop="handlingPlan">
          <el-input v-model="form.handlingPlan" type="textarea" :rows="3" placeholder="拟定处理方案" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">提交报告</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="trackDialogVisible" title="记录进展" width="500px">
      <p style="margin-bottom: 12px; color: #666;">事项：{{ currentEvent?.title }}</p>
      <el-input v-model="trackProgress" type="textarea" :rows="4" placeholder="请描述当前进展" />
      <template #footer>
        <el-button @click="trackDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTrack">保存进展</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resolveDialogVisible" title="解决事项" width="500px">
      <p style="margin-bottom: 12px; color: #666;">事项：{{ currentEvent?.title }}</p>
      <el-input v-model="resolveResult" type="textarea" :rows="4" placeholder="请描述解决结果" />
      <template #footer>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="success" @click="handleResolve">确认解决</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
.search-card { margin-bottom: 0; }
</style>
