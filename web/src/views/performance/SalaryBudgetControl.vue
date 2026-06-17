<template>
  <div class="salary-budget-control">
    <div class="page-header">
      <h2>薪酬总额管控</h2>
      <div class="header-actions">
        <el-select v-model="queryYear" placeholder="选择年份" style="width: 140px; margin-right: 8px;">
          <el-option v-for="y in yearOptions" :key="y" :label="y" :value="y" />
        </el-select>
        <el-button type="primary" @click="fetchData" :loading="loading">刷新</el-button>
        <el-button type="success" @click="showCreateDialog = true">新增预算</el-button>
      </div>
    </div>

    <!-- Table -->
    <el-card shadow="never">
      <el-table :data="budgets" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="budgetYear" label="年份" width="100" />
        <el-table-column prop="totalBudget" label="预算总额" width="160">
          <template #default="{ row }">
            ¥{{ Number(row.totalBudget).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="approvedBudget" label="审批预算" width="160">
          <template #default="{ row }">
            ¥{{ Number(row.approvedBudget || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="actualPaid" label="实际发放" width="160">
          <template #default="{ row }">
            ¥{{ Number(row.actualPaid || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="执行进度" width="220">
          <template #default="{ row }">
            <div class="progress-wrapper">
              <el-progress
                :percentage="executedPct(row)"
                :color="progressColor(executedPct(row))"
                :stroke-width="20"
                :text-inside="true"
              />
              <span class="pct-label">{{ executedPct(row).toFixed(1) }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openAdjustDialog(row)">调整预算</el-button>
            <el-popconfirm title="确认删除?" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.limit"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="fetchData"
        />
      </div>
    </el-card>

    <!-- Create Budget Dialog -->
    <el-dialog v-model="showCreateDialog" title="新增薪酬预算" width="500px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="预算年份" prop="budgetYear">
          <el-date-picker v-model="createForm.budgetYear" type="year" placeholder="选择年份" value-format="YYYY" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预算总额" prop="totalBudget">
          <el-input-number v-model="createForm.totalBudget" :min="0" :precision="2" style="width: 100%" placeholder="请输入预算总额" />
        </el-form-item>
        <el-form-item label="审批预算" prop="approvedBudget">
          <el-input-number v-model="createForm.approvedBudget" :min="0" :precision="2" style="width: 100%" placeholder="请输入审批预算" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :loading="creating">确认</el-button>
      </template>
    </el-dialog>

    <!-- Adjust Budget Dialog -->
    <el-dialog v-model="showAdjustDialog" title="调整预算" width="500px">
      <el-form :model="adjustForm" :rules="adjustRules" ref="adjustFormRef" label-width="100px">
        <el-form-item label="调整金额" prop="adjustment">
          <el-input-number v-model="adjustForm.adjustment" :precision="2" style="width: 100%" placeholder="正数增加，负数减少" />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input v-model="adjustForm.reason" type="textarea" :rows="3" placeholder="请输入调整原因" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdjustDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdjust" :loading="adjusting">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { querySalaryBudgets, manageSalaryBudget, adjustBudget, deleteSalaryBudget, type PerfSalaryBudget } from '@/api/performance'
import type { FormInstance } from 'element-plus'

const loading = ref(false)
const creating = ref(false)
const adjusting = ref(false)
const budgets = ref<PerfSalaryBudget[]>([])
const total = ref(0)
const showCreateDialog = ref(false)
const showAdjustDialog = ref(false)
const createFormRef = ref<FormInstance>()
const adjustFormRef = ref<FormInstance>()
const queryYear = ref(new Date().getFullYear())

const query = ref({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  budgetYear: undefined as number | undefined,
  page: 1,
  limit: 10,
})

const yearOptions = computed(() => {
  const year = new Date().getFullYear()
  return [year - 2, year - 1, year, year + 1]
})

const createForm = ref({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  orgId: Number(localStorage.getItem('orgId') || 1),
  budgetYear: undefined as number | undefined,
  totalBudget: 0,
  approvedBudget: 0,
})

const createRules = {
  budgetYear: [{ required: true, message: '请选择预算年份', trigger: 'change' }],
  totalBudget: [{ required: true, message: '请输入预算总额', trigger: 'blur' }],
}

const adjustForm = ref({
  id: '',
  adjustment: 0,
  reason: '',
})

const adjustRules = {
  adjustment: [{ required: true, message: '请输入调整金额', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }],
}

function executedPct(row: PerfSalaryBudget): number {
  const base = row.approvedBudget || row.totalBudget
  if (!base || base === 0) return 0
  return Math.min(Number((row.actualPaid || 0)) / Number(base) * 100, 100)
}

function progressColor(pct: number): string {
  if (pct >= 95) return '#f56c6c'
  if (pct >= 80) return '#e6a23c'
  return '#67c23a'
}

function statusTag(status: string): string {
  const map: Record<string, string> = { DRAFT: 'info', SUBMITTED: 'primary', APPROVED: 'success', ADJUSTED: 'warning', CLOSED: 'info' }
  return map[status] || 'info'
}

function statusLabel(status: string): string {
  const map: Record<string, string> = { DRAFT: '草稿', SUBMITTED: '已提交', APPROVED: '已审批', ADJUSTED: '已调整', CLOSED: '已关闭' }
  return map[status] || status
}

const fetchData = async () => {
  loading.value = true
  try {
    query.value.budgetYear = queryYear.value || undefined
    const res = await querySalaryBudgets(query.value)
    budgets.value = res.data
    if (res.meta) total.value = res.meta.total
  } finally {
    loading.value = false
  }
}

const handleCreate = async () => {
  const valid = await createFormRef.value?.validate().catch(() => false)
  if (!valid) return
  creating.value = true
  try {
    await manageSalaryBudget({
      ...createForm.value,
      budgetYear: createForm.value.budgetYear as number,
    })
    ElMessage.success('预算创建成功')
    showCreateDialog.value = false
    createForm.value = {
      tenantId: Number(localStorage.getItem('tenantId') || 1),
      orgId: Number(localStorage.getItem('orgId') || 1),
      budgetYear: undefined,
      totalBudget: 0,
      approvedBudget: 0,
    }
    fetchData()
  } finally {
    creating.value = false
  }
}

function openAdjustDialog(row: PerfSalaryBudget) {
  adjustForm.value = {
    id: row.id,
    adjustment: 0,
    reason: '',
  }
  showAdjustDialog.value = true
}

const handleAdjust = async () => {
  const valid = await adjustFormRef.value?.validate().catch(() => false)
  if (!valid) return
  adjusting.value = true
  try {
    await adjustBudget(adjustForm.value.id, adjustForm.value.adjustment, adjustForm.value.reason)
    ElMessage.success('预算调整成功')
    showAdjustDialog.value = false
    fetchData()
  } finally {
    adjusting.value = false
  }
}

const handleDelete = async (id: string) => {
  try {
    await deleteSalaryBudget(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // Error handled by interceptor
  }
}

onMounted(fetchData)
</script>

<style scoped>
.salary-budget-control {
  padding: 16px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
.header-actions {
  display: flex;
  align-items: center;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.progress-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-wrapper .el-progress {
  flex: 1;
}
.pct-label {
  min-width: 60px;
  font-size: 12px;
  color: #909399;
}
</style>
