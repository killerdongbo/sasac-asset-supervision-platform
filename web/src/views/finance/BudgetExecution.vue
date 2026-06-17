<template>
  <div class="budget-execution">
    <div class="page-header">
      <h2>预算执行</h2>
      <div class="header-actions">
        <el-date-picker v-model="queryYear" type="year" placeholder="选择年份" value-format="YYYY" style="width: 140px; margin-right: 8px;" />
        <el-button type="primary" @click="handleCheck" :loading="checking">执行检查</el-button>
        <el-button type="success" @click="showDialog = true">新增预算</el-button>
      </div>
    </div>

    <!-- Table -->
    <el-card shadow="never">
      <el-table :data="budgets" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="budgetYear" label="年份" width="100" />
        <el-table-column prop="budgetType" label="预算类型" width="150">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.budgetType)" size="small">{{ row.budgetType || '综合' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plannedAmount" label="预算金额" width="160">
          <template #default="{ row }">
            ¥{{ Number(row.plannedAmount).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实际金额" width="160">
          <template #default="{ row }">
            ¥{{ Number(row.actualAmount).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="executedPct" label="执行进度" width="200">
          <template #default="{ row }">
            <div class="progress-wrapper">
              <el-progress
                :percentage="Math.min(Number(row.executedPct || 0), 100)"
                :color="progressColor(row.executedPct)"
                :stroke-width="20"
                :text-inside="true"
              />
              <span class="pct-label">{{ row.executedPct ? row.executedPct.toFixed(2) + '%' : '0%' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="progressTag(row.executedPct)" size="small">
              {{ progressLabel(row.executedPct) }}
            </el-tag>
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

    <!-- Add Budget Dialog -->
    <el-dialog v-model="showDialog" title="新增预算" width="450px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="预算年份" prop="budgetYear">
          <el-date-picker v-model="form.budgetYear" type="year" placeholder="选择年份" value-format="YYYY" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预算类型" prop="budgetType">
          <el-select v-model="form.budgetType" style="width: 100%">
            <el-option label="运营预算" value="运营预算" />
            <el-option label="投资预算" value="投资预算" />
            <el-option label="项目预算" value="项目预算" />
            <el-option label="管理预算" value="管理预算" />
          </el-select>
        </el-form-item>
        <el-form-item label="预算金额" prop="plannedAmount">
          <el-input-number v-model="form.plannedAmount" :min="0" :precision="2" style="width: 100%" placeholder="请输入预算金额" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryBudgets, checkBudgetExecution, createBudget, type FinBudget } from '@/api/finance'
import type { FormInstance } from 'element-plus'

const loading = ref(false)
const checking = ref(false)
const submitting = ref(false)
const budgets = ref<FinBudget[]>([])
const total = ref(0)
const showDialog = ref(false)
const formRef = ref<FormInstance>()
const queryYear = ref(new Date().getFullYear())

const query = ref({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  budgetYear: undefined as number | undefined,
  page: 1,
  limit: 10,
})

const form = reactive({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  orgId: Number(localStorage.getItem('orgId') || 1),
  budgetYear: undefined as number | undefined,
  budgetType: '运营预算',
  plannedAmount: 0,
})

const rules = {
  budgetYear: [{ required: true, message: '请选择预算年份', trigger: 'change' }],
  budgetType: [{ required: true, message: '请选择预算类型', trigger: 'change' }],
  plannedAmount: [{ required: true, message: '请输入预算金额', trigger: 'blur' }],
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await queryBudgets(query.value)
    budgets.value = res.data
    if (res.meta) total.value = res.meta.total
  } finally {
    loading.value = false
  }
}

const handleCheck = async () => {
  checking.value = true
  try {
    const res = await checkBudgetExecution({
      tenantId: Number(localStorage.getItem('tenantId') || 1),
      budgetYear: queryYear.value as number,
    })
    budgets.value = res.data
    ElMessage.success('预算执行检查完成')
  } finally {
    checking.value = false
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await createBudget({
      ...form,
      budgetYear: form.budgetYear as number,
    })
    ElMessage.success('预算创建成功')
    showDialog.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

const typeTag = (type: string) => {
  const map: Record<string, string> = { 运营预算: 'primary', 投资预算: 'success', 项目预算: 'warning', 管理预算: 'info' }
  return map[type || ''] || 'info'
}

const progressColor = (pct: number) => {
  if (pct >= 95) return '#f56c6c'
  if (pct >= 80) return '#e6a23c'
  return '#67c23a'
}

const progressTag = (pct: number) => {
  if (pct >= 95) return 'danger'
  if (pct >= 80) return 'warning'
  return 'success'
}

const progressLabel = (pct: number) => {
  if (pct >= 95) return '超预算'
  if (pct >= 80) return '预警'
  return '正常'
}

onMounted(fetchData)
</script>

<style scoped>
.budget-execution {
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
.search-card {
  margin-bottom: 16px;
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
