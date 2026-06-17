<template>
  <div class="performance-list">
    <div class="toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="员工ID">
          <el-input-number v-model="queryForm.employeeId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="showCreateDialog">新建考核</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="performances" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="employeeId" label="员工ID" width="80" />
      <el-table-column prop="cycleType" label="考核周期" width="100" />
      <el-table-column prop="cycleYear" label="年份" width="80" />
      <el-table-column prop="selfScore" label="自评分" width="80" align="center" />
      <el-table-column prop="managerScore" label="经理评分" width="90" align="center" />
      <el-table-column prop="finalScore" label="最终得分" width="90" align="center">
        <template #default="{ row }">
          <span :class="scoreClass(row.finalScore)">{{ row.finalScore ?? '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="grade" label="等级" width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.grade" :type="gradeType(row.grade)" size="large">{{ row.grade }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'CONFIRMED' ? 'success' : 'warning'">
            {{ row.status === 'CONFIRMED' ? '已确认' : row.status === 'DRAFT' ? '草稿' : row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'DRAFT'" link type="primary" size="small" @click="showScoreDialog(row)">评分</el-button>
          <el-button v-if="row.status !== 'CONFIRMED'" link type="success" size="small" @click="handleConfirm(row.id)">确认</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>

    <!-- Create Dialog -->
    <el-dialog v-model="createDialogVisible" title="新建考核" width="500px">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="员工ID" prop="employeeId">
          <el-input-number v-model="createForm.employeeId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="考核周期" prop="cycleType">
          <el-select v-model="createForm.cycleType" placeholder="请选择" style="width: 100%">
            <el-option label="月度" value="MONTHLY" />
            <el-option label="季度" value="QUARTERLY" />
            <el-option label="年度" value="YEARLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份" prop="cycleYear">
          <el-input-number v-model="createForm.cycleYear" :min="2020" :max="2030" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="reviewComment">
          <el-input v-model="createForm.reviewComment" type="textarea" :rows="3" placeholder="考核说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :loading="creating">创建</el-button>
      </template>
    </el-dialog>

    <!-- Score Dialog -->
    <el-dialog v-model="scoreDialogVisible" title="绩效评分" width="400px">
      <el-form ref="scoreFormRef" :model="scoreForm" :rules="scoreRules" label-width="100px">
        <el-form-item label="自评分" prop="selfScore">
          <el-slider v-model="scoreForm.selfScore" :min="0" :max="100" :step="0.1" show-input />
        </el-form-item>
        <el-form-item label="经理评分" prop="managerScore">
          <el-slider v-model="scoreForm.managerScore" :min="0" :max="100" :step="0.1" show-input />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scoreDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleScore" :loading="scoring">提交评分</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { queryPerformances, createPerformance, scorePerformance, confirmPerformance } from '@/api/hr'
import type { HrPerformance } from '@/api/hr'

const performances = ref<HrPerformance[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const queryForm = reactive({
  employeeId: null as number | null
})

function scoreClass(score: number | null | undefined): string {
  if (score == null) return ''
  if (score >= 90) return 'score-a'
  if (score >= 80) return 'score-b'
  if (score >= 70) return 'score-c'
  return 'score-d'
}

function gradeType(grade: string): string {
  const map: Record<string, string> = { A: 'success', B: 'primary', C: 'warning', D: 'danger' }
  return map[grade] || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    const params: Record<string, unknown> = {
      page: currentPage.value,
      limit: pageSize.value
    }
    if (tenantIdStr) params.tenantId = Number(tenantIdStr)
    if (queryForm.employeeId) params.employeeId = queryForm.employeeId

    const response = await queryPerformances(params as any)
    performances.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    performances.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function handleConfirm(id: string) {
  try {
    await confirmPerformance(id)
    ElMessage.success('绩效已确认')
    fetchData()
  } catch {
    // Error handled by interceptor
  }
}

// Create dialog
const createDialogVisible = ref(false)
const creating = ref(false)
const createFormRef = ref<FormInstance>()

const createForm = reactive({
  employeeId: null as number | null,
  cycleType: 'MONTHLY',
  cycleYear: new Date().getFullYear(),
  reviewComment: ''
})

const createRules: FormRules = {
  employeeId: [{ required: true, message: '请输入员工ID', trigger: 'blur' }],
  cycleType: [{ required: true, message: '请选择考核周期', trigger: 'change' }]
}

function showCreateDialog() {
  createDialogVisible.value = true
}

async function handleCreate() {
  if (!createFormRef.value) return
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return

  creating.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    if (tenantIdStr === null) {
      ElMessage.error('缺少组织信息')
      creating.value = false
      return
    }
    await createPerformance({
      employeeId: createForm.employeeId!,
      tenantId: Number(tenantIdStr),
      cycleType: createForm.cycleType,
      cycleYear: createForm.cycleYear,
      reviewComment: createForm.reviewComment
    })
    ElMessage.success('考核创建成功')
    createDialogVisible.value = false
    fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    creating.value = false
  }
}

// Score dialog
const scoreDialogVisible = ref(false)
const scoring = ref(false)
const scoreFormRef = ref<FormInstance>()
const currentPerfId = ref('')

const scoreForm = reactive({
  selfScore: 80,
  managerScore: 80
})

const scoreRules: FormRules = {
  selfScore: [{ required: true, message: '请输入自评分', trigger: 'change' }],
  managerScore: [{ required: true, message: '请输入经理评分', trigger: 'change' }]
}

function showScoreDialog(row: HrPerformance) {
  currentPerfId.value = row.id
  scoreForm.selfScore = 80
  scoreForm.managerScore = 80
  scoreDialogVisible.value = true
}

async function handleScore() {
  if (!scoreFormRef.value) return
  const valid = await scoreFormRef.value.validate().catch(() => false)
  if (!valid) return

  scoring.value = true
  try {
    await scorePerformance(currentPerfId.value, {
      selfScore: scoreForm.selfScore,
      managerScore: scoreForm.managerScore
    })
    ElMessage.success('评分提交成功')
    scoreDialogVisible.value = false
    fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    scoring.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.performance-list {
  padding: 0;
}

.toolbar {
  margin-bottom: 16px;
}

.score-a { color: #67c23a; font-weight: bold; }
.score-b { color: #409eff; font-weight: bold; }
.score-c { color: #e6a23c; font-weight: bold; }
.score-d { color: #f56c6c; font-weight: bold; }

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
