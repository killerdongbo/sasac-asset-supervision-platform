<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getEvent, approveEvent, trackEvent, resolveEvent } from '@/api/majorevent'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const event = ref<any>(null)
const loading = ref(false)
const trackDialogVisible = ref(false)
const resolveDialogVisible = ref(false)
const trackProgress = ref('')
const resolveResult = ref('')

const eventTypeOptions: Record<string, string> = {
  MERGER: '合并',
  DIVISION: '分立',
  RESTRUCTURING: '改制',
  LISTING: '上市',
  BANKRUPTCY: '破产',
  MAJOR_LITIGATION: '重大诉讼',
  MAJOR_GUARANTEE: '重大担保',
  OTHER: '其他'
}

const statusTimeline = computed(() => {
  const statuses = ['REPORTED', 'APPROVED', 'TRACKING', 'RESOLVED']
  const currentIdx = statuses.indexOf(event.value?.status || '')
  return statuses.map((s, i) => ({
    status: s,
    label: getStatusLabel(s),
    passed: i <= currentIdx,
    current: i === currentIdx
  }))
})

function getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    REPORTED: '已报告',
    APPROVED: '已审批',
    TRACKING: '跟踪中',
    RESOLVED: '已解决'
  }
  return labels[status] || status
}

function formatTime(time: string): string {
  return time?.replace('T', ' ').substring(0, 19) || ''
}

function formatCurrency(val: number | null | undefined): string {
  if (val == null) return '-'
  return '¥' + Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

async function fetch() {
  const id = route.params.id as string
  if (!id) return
  loading.value = true
  try {
    const res = await getEvent(Number(id))
    event.value = res.data ?? null
  } catch {
    event.value = null
  } finally {
    loading.value = false
  }
}

function handleApprove() {
  if (!event.value) return
  ElMessageBox.confirm('确认审批通过此事项？', '提示', { type: 'info' }).then(async () => {
    try {
      await approveEvent(event.value.id)
      ElMessage.success('审批通过')
      fetch()
    } catch (e: any) {
      ElMessage.error(e.response?.data?.error || '操作失败')
    }
  }).catch(() => {})
}

function openTrack() {
  trackProgress.value = ''
  trackDialogVisible.value = true
}

async function handleTrack() {
  if (!trackProgress.value.trim()) {
    ElMessage.warning('请输入进展内容')
    return
  }
  try {
    await trackEvent(event.value.id, trackProgress.value)
    ElMessage.success('进展已记录')
    trackDialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function openResolve() {
  resolveResult.value = ''
  resolveDialogVisible.value = true
}

async function handleResolve() {
  if (!resolveResult.value.trim()) {
    ElMessage.warning('请输入解决结果')
    return
  }
  try {
    await resolveEvent(event.value.id, resolveResult.value)
    ElMessage.success('事项已解决')
    resolveDialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

onMounted(fetch)
</script>

<template>
  <div class="detail-page">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
      <div class="header-actions">
        <el-button v-if="event?.status === 'REPORTED'" type="primary" @click="handleApprove">审批通过</el-button>
        <el-button v-if="event?.status === 'APPROVED'" type="warning" @click="openTrack">记录进展</el-button>
        <el-button v-if="event?.status === 'TRACKING'" type="success" @click="openResolve">解决事项</el-button>
      </div>
    </div>

    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span style="font-size: 16px; font-weight: 600;">事项详情</span>
      </template>

      <el-empty v-if="!loading && !event" description="未找到事项信息" />

      <template v-if="event">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="事项编号" :span="1">{{ event.eventNo }}</el-descriptions-item>
          <el-descriptions-item label="事项类型" :span="1">
            {{ eventTypeOptions[event.eventType] || event.eventType }}
          </el-descriptions-item>
          <el-descriptions-item label="事项标题" :span="2">{{ event.title }}</el-descriptions-item>
          <el-descriptions-item label="事项描述" :span="2">{{ event.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="影响评估" :span="2">{{ event.impactAssessment || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处理方案" :span="2">{{ event.handlingPlan || '-' }}</el-descriptions-item>
          <el-descriptions-item label="进展记录" :span="2">{{ event.progress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="解决结果" :span="2">{{ event.resolution || '-' }}</el-descriptions-item>
          <el-descriptions-item label="报告时间">{{ formatTime(event.reportedAt || event.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="event.status === 'REPORTED'" type="warning">已报告</el-tag>
            <el-tag v-else-if="event.status === 'APPROVED'" type="primary">已审批</el-tag>
            <el-tag v-else-if="event.status === 'TRACKING'" type="info">跟踪中</el-tag>
            <el-tag v-else-if="event.status === 'RESOLVED'" type="success">已解决</el-tag>
            <el-tag v-else>{{ event.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatTime(event.updatedAt) }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4 style="margin-bottom: 16px;">状态流转</h4>
        <el-steps :active="statusTimeline.filter(s => s.passed).length" align-center finish-status="success">
          <el-step v-for="s in statusTimeline" :key="s.status" :title="s.label" />
        </el-steps>

        <el-divider v-if="event.relatedLawsuits?.length || event.relatedGuarantees?.length" />

        <div v-if="event.relatedLawsuits?.length" style="margin-top: 16px;">
          <h4 style="margin-bottom: 12px;">关联诉讼</h4>
          <el-table :data="event.relatedLawsuits" border stripe size="small">
            <el-table-column prop="caseNo" label="案号" min-width="160" />
            <el-table-column prop="court" label="法院" min-width="200" />
            <el-table-column prop="plaintiff" label="原告" width="120" />
            <el-table-column prop="defendant" label="被告" width="120" />
            <el-table-column label="诉讼金额" width="140">
              <template #default="{ row }">{{ formatCurrency(row.claimAmount) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'CLOSED' ? 'success' : row.status === 'TRIAL' ? 'warning' : 'info'" size="small">
                  {{ row.status === 'PENDING' ? '待立案' : row.status === 'TRIAL' ? '审理中' : row.status === 'CLOSED' ? '已结案' : row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-if="event.relatedGuarantees?.length" style="margin-top: 16px;">
          <h4 style="margin-bottom: 12px;">关联担保</h4>
          <el-table :data="event.relatedGuarantees" border stripe size="small">
            <el-table-column prop="guaranteeType" label="担保类型" width="120" />
            <el-table-column prop="beneficiary" label="受益人" min-width="200" />
            <el-table-column label="担保金额" width="140">
              <template #default="{ row }">{{ formatCurrency(row.guaranteeAmount) }}</template>
            </el-table-column>
            <el-table-column prop="guaranteePeriod" label="担保期限" width="200" />
            <el-table-column label="风险等级" width="100">
              <template #default="{ row }">
                <el-tag :type="row.riskLevel === 'HIGH' ? 'danger' : row.riskLevel === 'MEDIUM' ? 'warning' : 'success'" size="small">
                  {{ row.riskLevel === 'LOW' ? '低风险' : row.riskLevel === 'MEDIUM' ? '中风险' : row.riskLevel === 'HIGH' ? '高风险' : row.riskLevel }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
    </el-card>

    <el-dialog v-model="trackDialogVisible" title="记录进展" width="500px">
      <p style="margin-bottom: 12px; color: #666;">事项：{{ event?.title }}</p>
      <el-input v-model="trackProgress" type="textarea" :rows="4" placeholder="请描述当前进展" />
      <template #footer>
        <el-button @click="trackDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTrack">保存进展</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resolveDialogVisible" title="解决事项" width="500px">
      <p style="margin-bottom: 12px; color: #666;">事项：{{ event?.title }}</p>
      <el-input v-model="resolveResult" type="textarea" :rows="4" placeholder="请描述解决结果" />
      <template #footer>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="success" @click="handleResolve">确认解决</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.detail-page { padding: 0; }
.page-header { margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; }
.header-actions { display: flex; gap: 8px; }
</style>
