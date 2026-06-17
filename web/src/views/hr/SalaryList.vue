<template>
  <div class="salary-list">
    <div class="toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="年份">
          <el-input-number v-model="queryForm.salaryYear" :min="2020" :max="2030" controls-position="right" />
        </el-form-item>
        <el-form-item label="月份">
          <el-select v-model="queryForm.salaryMonth" placeholder="全部月份" clearable style="width: 120px">
            <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="showCalculateDialog">薪资计算</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="salaries" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="salaryYear" label="年份" width="80" />
      <el-table-column prop="salaryMonth" label="月份" width="80" />
      <el-table-column prop="employeeId" label="员工ID" width="80" />
      <el-table-column prop="baseSalary" label="基本工资" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.baseSalary) }}</template>
      </el-table-column>
      <el-table-column prop="performancePay" label="绩效工资" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.performancePay) }}</template>
      </el-table-column>
      <el-table-column prop="allowance" label="补贴" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.allowance) }}</template>
      </el-table-column>
      <el-table-column prop="socialInsurance" label="社保" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.socialInsurance) }}</template>
      </el-table-column>
      <el-table-column prop="housingFund" label="公积金" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.housingFund) }}</template>
      </el-table-column>
      <el-table-column prop="tax" label="个税" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.tax) }}</template>
      </el-table-column>
      <el-table-column prop="netSalary" label="实发工资" width="130" align="right">
        <template #default="{ row }">{{ formatMoney(row.netSalary) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'CONFIRMED' ? 'success' : 'warning'">
            {{ row.status === 'CONFIRMED' ? '已确认' : '已计算' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status !== 'CONFIRMED'" link type="primary" size="small" @click="handleConfirm(row.id)">确认</el-button>
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

    <!-- Calculate Dialog -->
    <el-dialog v-model="calcDialogVisible" title="薪资计算" width="400px">
      <el-form ref="calcFormRef" :model="calcForm" :rules="calcRules" label-width="100px">
        <el-form-item label="员工ID" prop="employeeId">
          <el-input-number v-model="calcForm.employeeId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="年份" prop="salaryYear">
          <el-input-number v-model="calcForm.salaryYear" :min="2020" :max="2030" style="width: 100%" />
        </el-form-item>
        <el-form-item label="月份" prop="salaryMonth">
          <el-select v-model="calcForm.salaryMonth" placeholder="选择月份" style="width: 100%">
            <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="calcDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCalculate" :loading="calcLoading">开始计算</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { querySalaries, calculateSalary, confirmSalary } from '@/api/hr'
import type { HrSalary } from '@/api/hr'

const salaries = ref<HrSalary[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const queryForm = reactive({
  salaryYear: new Date().getFullYear(),
  salaryMonth: null as number | null
})

function formatMoney(val: number | null | undefined): string {
  if (val == null) return '-'
  return '¥ ' + val.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

async function fetchData() {
  loading.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    const params: Record<string, unknown> = {
      page: currentPage.value,
      limit: pageSize.value,
      salaryYear: queryForm.salaryYear
    }
    if (tenantIdStr) params.tenantId = Number(tenantIdStr)
    if (queryForm.salaryMonth) params.salaryMonth = queryForm.salaryMonth

    const response = await querySalaries(params as any)
    salaries.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    salaries.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function handleConfirm(id: string) {
  try {
    await confirmSalary(id)
    ElMessage.success('薪资已确认')
    fetchData()
  } catch {
    // Error handled by interceptor
  }
}

// Calculate dialog
const calcDialogVisible = ref(false)
const calcLoading = ref(false)
const calcFormRef = ref<FormInstance>()

const calcForm = reactive({
  employeeId: null as number | null,
  salaryYear: new Date().getFullYear(),
  salaryMonth: new Date().getMonth() + 1
})

const calcRules: FormRules = {
  employeeId: [{ required: true, message: '请输入员工ID', trigger: 'blur' }],
  salaryYear: [{ required: true, message: '请选择年份', trigger: 'change' }],
  salaryMonth: [{ required: true, message: '请选择月份', trigger: 'change' }]
}

function showCalculateDialog() {
  calcDialogVisible.value = true
}

async function handleCalculate() {
  if (!calcFormRef.value) return
  const valid = await calcFormRef.value.validate().catch(() => false)
  if (!valid) return

  calcLoading.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    if (tenantIdStr === null) {
      ElMessage.error('缺少组织信息')
      calcLoading.value = false
      return
    }
    const result = await calculateSalary({
      employeeId: calcForm.employeeId!,
      tenantId: Number(tenantIdStr),
      salaryYear: calcForm.salaryYear,
      salaryMonth: calcForm.salaryMonth
    })
    ElMessage.success(`薪资计算完成，实发：¥${result.data.netSalary?.toLocaleString()}`)
    calcDialogVisible.value = false
    fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    calcLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.salary-list {
  padding: 0;
}

.toolbar {
  margin-bottom: 16px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
