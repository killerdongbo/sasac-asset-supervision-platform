<template>
  <div class="project-dashboard">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
      <el-button type="primary" @click="router.push(`/projects/${route.params.id}/edit`)">编辑</el-button>
    </div>

    <el-card v-loading="loading" shadow="never" style="margin-bottom: 16px">
      <template #header>
        <span class="card-title">基本信息</span>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="项目编号">{{ project.projectNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ project.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="项目类型">{{ typeMap[project.projectType || ''] || project.projectType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预算总额">{{ project.budgetTotal ? '¥' + Number(project.budgetTotal).toLocaleString() : '-' }}</el-descriptions-item>
        <el-descriptions-item label="已批预算">{{ project.budgetApproved ? '¥' + Number(project.budgetApproved).toLocaleString() : '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(project.status)">
            {{ statusMap[project.status || ''] || project.status || '-' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ project.startDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="计划结束日期">{{ project.plannedEndDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际结束日期">{{ project.actualEndDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ project.managerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ project.department || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ project.category || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never" style="margin-bottom: 16px">
          <template #header>
            <span class="card-title">进度记录</span>
            <el-button style="float: right" size="small" @click="showProgressForm = true">记录进度</el-button>
          </template>

          <div v-if="progressHistory.length === 0" style="text-align: center; color: #999; padding: 20px">
            暂无进度记录
          </div>

          <el-table v-else :data="progressHistory" stripe style="width: 100%">
            <el-table-column prop="reportDate" label="日期" width="110" />
            <el-table-column prop="progressPct" label="进度" width="120">
              <template #default="{ row }">
                <el-progress :percentage="row.progressPct || 0" :stroke-width="14" />
              </template>
            </el-table-column>
            <el-table-column prop="completedWork" label="完成工作" min-width="180" show-overflow-tooltip />
            <el-table-column prop="issues" label="问题" min-width="140" show-overflow-tooltip />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never" style="margin-bottom: 16px">
          <template #header>
            <span class="card-title">预算概况</span>
          </template>

          <div ref="budgetChartRef" style="width: 100%; height: 280px" />
        </el-card>
      </el-col>
    </el-row>

    <el-card v-if="acceptanceRecords.length > 0" shadow="never">
      <template #header>
        <span class="card-title">验收记录</span>
      </template>

      <el-table :data="acceptanceRecords" stripe style="width: 100%">
        <el-table-column prop="acceptanceNo" label="验收编号" width="160" />
        <el-table-column prop="acceptanceType" label="验收类型" width="120">
          <template #default="{ row }">
            {{ acceptanceTypeMap[row.acceptanceType] || row.acceptanceType || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="result" label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === 'PASS' ? 'success' : 'danger'">
              {{ row.result === 'PASS' ? '通过' : row.result === 'FAIL' ? '不通过' : row.result || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="80" />
        <el-table-column prop="reviewerName" label="评审人" width="100" />
        <el-table-column prop="acceptanceDate" label="验收日期" width="110" />
        <el-table-column prop="reviewOpinion" label="评审意见" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-card>

    <!-- Progress Record Dialog -->
    <el-dialog v-model="showProgressForm" title="记录进度" width="560px">
      <el-form ref="progressFormRef" :model="progressForm" :rules="progressRules" label-width="100px">
        <el-form-item label="汇报日期" prop="reportDate">
          <el-date-picker
            v-model="progressForm.reportDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="进度百分比" prop="progressPct">
          <el-slider
            v-model="progressForm.progressPct"
            :min="0"
            :max="100"
            show-input
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="完成工作" prop="completedWork">
          <el-input v-model="progressForm.completedWork" type="textarea" :rows="2" placeholder="已完成的工作" />
        </el-form-item>
        <el-form-item label="待完成工作" prop="pendingWork">
          <el-input v-model="progressForm.pendingWork" type="textarea" :rows="2" placeholder="待完成的工作" />
        </el-form-item>
        <el-form-item label="存在问题" prop="issues">
          <el-input v-model="progressForm.issues" type="textarea" :rows="2" placeholder="当前存在的问题" />
        </el-form-item>
        <el-form-item label="下一步计划" prop="nextPlan">
          <el-input v-model="progressForm.nextPlan" type="textarea" :rows="2" placeholder="下一步工作计划" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showProgressForm = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleRecordProgress">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, reactive, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as echarts from 'echarts'
import { getProject, getProgressHistory, getAcceptance, getProjectBudgets, recordProgress } from '@/api/project'
import type { PmProject, PmProgress, PmAcceptance, PmBudget } from '@/api/project'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const showProgressForm = ref(false)
const progressFormRef = ref<FormInstance>()

const project = ref<Partial<PmProject>>({})
const progressHistory = ref<PmProgress[]>([])
const acceptanceRecords = ref<PmAcceptance[]>([])
const budgets = ref<PmBudget[]>([])

const budgetChartRef = ref<HTMLDivElement | null>(null)
let budgetChart: echarts.ECharts | null = null

const statusMap: Record<string, string> = {
  DRAFT: '草稿',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  SUSPENDED: '已暂停'
}

const typeMap: Record<string, string> = {
  ENGINEERING: '工程建设',
  IT: '信息化',
  TECH_UPGRADE: '技改',
  OTHER: '其他'
}

const acceptanceTypeMap: Record<string, string> = {
  FINAL: '终验',
  MIDTERM: '中期验收',
  PHASE: '阶段验收'
}

function statusTagType(status?: string): string {
  const map: Record<string, string> = {
    DRAFT: 'info',
    IN_PROGRESS: 'primary',
    COMPLETED: 'success',
    SUSPENDED: 'warning'
  }
  return map[status || ''] || 'info'
}

const progressForm = reactive({
  reportDate: '',
  progressPct: 0,
  completedWork: '',
  pendingWork: '',
  issues: '',
  nextPlan: ''
})

const progressRules: FormRules = {
  reportDate: [{ required: true, message: '请选择汇报日期', trigger: 'blur' }],
  progressPct: [{ required: true, message: '请设置进度百分比', trigger: 'blur' }]
}

function initBudgetChart() {
  if (!budgetChartRef.value) return

  budgetChart = echarts.init(budgetChartRef.value)

  const years = budgets.value.map(b => String(b.budgetYear))
  const planned = budgets.value.map(b => Number(b.plannedAmount || 0))
  const actual = budgets.value.map(b => Number(b.actualAmount || 0))

  if (years.length === 0) {
    budgetChart.setOption({
      title: { text: '暂无预算数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } }
    })
    return
  }

  budgetChart.setOption({
    tooltip: {
      trigger: 'axis',
      valueFormatter: (v: number) => '¥' + v.toLocaleString()
    },
    legend: { data: ['预算金额', '实际金额'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '18%', top: '6%', containLabel: true },
    xAxis: { type: 'category', data: years, axisLabel: { rotate: 0 } },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (v: number) => {
          if (v >= 1e8) return (v / 1e8).toFixed(1) + '亿'
          if (v >= 1e4) return (v / 1e4).toFixed(0) + '万'
          return String(v)
        }
      }
    },
    series: [
      {
        name: '预算金额',
        type: 'bar',
        data: planned,
        itemStyle: { color: '#5470c6', borderRadius: [4, 4, 0, 0] },
        barWidth: '30%'
      },
      {
        name: '实际金额',
        type: 'bar',
        data: actual,
        itemStyle: { color: '#91cc75', borderRadius: [4, 4, 0, 0] },
        barWidth: '30%'
      }
    ]
  })
}

async function fetchProjectDetail() {
  const id = route.params.id as string
  if (!id) return

  loading.value = true
  try {
    const projectRes = await getProject(id)
    project.value = projectRes.data || {}

    const progressRes = await getProgressHistory(id, Number(localStorage.getItem('tenantId')) || 0)
    progressHistory.value = progressRes.data || []

    const acceptanceRes = await getAcceptance(id)
    acceptanceRecords.value = acceptanceRes.data || []

    const budgetRes = await getProjectBudgets(id)
    budgets.value = budgetRes.data || []

    await nextTick()
    initBudgetChart()
  } catch {
    ElMessage.error('获取项目信息失败')
  } finally {
    loading.value = false
  }
}

async function handleRecordProgress() {
  if (!progressFormRef.value) return
  const valid = await progressFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const id = route.params.id as string
    const tenantId = Number(localStorage.getItem('tenantId')) || 0

    await recordProgress(id, {
      projectId: Number(id),
      tenantId,
      ...progressForm
    } as any)

    ElMessage.success('进度记录成功')
    showProgressForm.value = false
    // Refresh progress history
    const progressRes = await getProgressHistory(id, tenantId)
    progressHistory.value = progressRes.data || []
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchProjectDetail()
})

onBeforeUnmount(() => {
  if (budgetChart) {
    budgetChart.dispose()
    budgetChart = null
  }
})
</script>

<style scoped>
.project-dashboard {
  padding: 0;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
