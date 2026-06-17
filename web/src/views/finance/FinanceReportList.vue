<template>
  <div class="finance-report-list">
    <div class="page-header">
      <h2>财务报表填报</h2>
      <el-button type="primary" @click="showDialog = true">填报</el-button>
    </div>

    <!-- Search / Filter -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="报表类型">
          <el-select v-model="query.reportType" placeholder="全部类型" clearable style="width: 180px">
            <el-option label="资产负债表" value="BALANCE_SHEET" />
            <el-option label="利润表" value="INCOME_STATEMENT" />
            <el-option label="现金流量表" value="CASH_FLOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份">
          <el-date-picker v-model="query.reportYear" type="year" placeholder="选择年份" value-format="YYYY" style="width: 140px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已提交" value="SUBMITTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table -->
    <el-card shadow="never">
      <el-table :data="reports" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="reportYear" label="年份" width="80" />
        <el-table-column prop="reportPeriod" label="期间" width="80">
          <template #default="{ row }">
            {{ row.reportPeriod }}月
          </template>
        </el-table-column>
        <el-table-column prop="reportType" label="报表类型" width="150">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.reportType)" size="small">{{ typeLabel(row.reportType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUBMITTED' ? 'success' : 'info'" size="small">
              {{ row.status === 'SUBMITTED' ? '已提交' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submittedAt" label="提交时间" width="180">
          <template #default="{ row }">
            {{ row.submittedAt ? new Date(row.submittedAt).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="viewReport(row)">查看</el-button>
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

    <!-- Submit Dialog -->
    <el-dialog v-model="showDialog" title="填报财务报表" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="报表类型" prop="reportType">
          <el-select v-model="form.reportType" style="width: 100%">
            <el-option label="资产负债表" value="BALANCE_SHEET" />
            <el-option label="利润表" value="INCOME_STATEMENT" />
            <el-option label="现金流量表" value="CASH_FLOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份" prop="reportYear">
          <el-date-picker v-model="form.reportYear" type="year" placeholder="选择年份" value-format="YYYY" style="width: 100%" />
        </el-form-item>
        <el-form-item label="月份" prop="reportPeriod">
          <el-select v-model="form.reportPeriod" style="width: 100%">
            <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产总计" prop="totalAssets">
          <el-input-number v-model="form.totalAssets" :min="0" style="width: 100%" placeholder="万元" />
        </el-form-item>
        <el-form-item label="负债总计" prop="totalLiabilities">
          <el-input-number v-model="form.totalLiabilities" :min="0" style="width: 100%" placeholder="万元" />
        </el-form-item>
        <el-form-item label="流动资产" prop="currentAssets">
          <el-input-number v-model="form.currentAssets" :min="0" style="width: 100%" placeholder="万元" />
        </el-form-item>
        <el-form-item label="流动负债" prop="currentLiabilities">
          <el-input-number v-model="form.currentLiabilities" :min="0" style="width: 100%" placeholder="万元" />
        </el-form-item>
        <el-form-item label="净资产" prop="netAssets">
          <el-input-number v-model="form.netAssets" :min="0" style="width: 100%" placeholder="万元" />
        </el-form-item>
        <el-form-item label="营业收入" prop="revenue">
          <el-input-number v-model="form.revenue" :min="0" style="width: 100%" placeholder="万元" />
        </el-form-item>
        <el-form-item label="净利润" prop="netProfit">
          <el-input-number v-model="form.netProfit" style="width: 100%" placeholder="万元" />
        </el-form-item>
        <el-form-item label="应收账款" prop="accountsReceivable">
          <el-input-number v-model="form.accountsReceivable" :min="0" style="width: 100%" placeholder="万元" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryReports, submitReport, type FinReport } from '@/api/finance'
import type { FormInstance } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const reports = ref<FinReport[]>([])
const total = ref(0)
const showDialog = ref(false)
const formRef = ref<FormInstance>()

const query = ref({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  reportType: undefined as string | undefined,
  reportYear: undefined as number | undefined,
  status: undefined as string | undefined,
  page: 1,
  limit: 10,
})

const form = reactive({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  orgId: Number(localStorage.getItem('orgId') || 1),
  reportType: 'BALANCE_SHEET',
  reportYear: undefined as number | undefined,
  reportPeriod: 1,
  totalAssets: 0,
  totalLiabilities: 0,
  currentAssets: 0,
  currentLiabilities: 0,
  netAssets: 0,
  revenue: 0,
  netProfit: 0,
  accountsReceivable: 0,
})

const rules = {
  reportType: [{ required: true, message: '请选择报表类型', trigger: 'change' }],
  reportYear: [{ required: true, message: '请选择年份', trigger: 'change' }],
  reportPeriod: [{ required: true, message: '请选择月份', trigger: 'change' }],
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await queryReports(query.value)
    reports.value = res.data
    if (res.meta) total.value = res.meta.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.value.reportType = undefined
  query.value.reportYear = undefined
  query.value.status = undefined
  query.value.page = 1
  fetchData()
}

const typeTag = (type: string) => {
  const map: Record<string, string> = { BALANCE_SHEET: 'primary', INCOME_STATEMENT: 'success', CASH_FLOW: 'warning' }
  return map[type] || 'info'
}

const typeLabel = (type: string) => {
  const map: Record<string, string> = { BALANCE_SHEET: '资产负债表', INCOME_STATEMENT: '利润表', CASH_FLOW: '现金流量表' }
  return map[type] || type
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    let reportData: Record<string, number> = {}
    if (form.reportType === 'BALANCE_SHEET') {
      reportData = {
        totalAssets: form.totalAssets,
        totalLiabilities: form.totalLiabilities,
        currentAssets: form.currentAssets,
        currentLiabilities: form.currentLiabilities,
        netAssets: form.netAssets,
        accountsReceivable: form.accountsReceivable,
      }
    } else if (form.reportType === 'INCOME_STATEMENT') {
      reportData = {
        revenue: form.revenue,
        netProfit: form.netProfit,
      }
    } else if (form.reportType === 'CASH_FLOW') {
      reportData = {
        revenue: form.revenue,
        netProfit: form.netProfit,
      }
    }

    await submitReport({
      tenantId: form.tenantId,
      orgId: form.orgId,
      reportType: form.reportType,
      reportYear: form.reportYear!,
      reportPeriod: form.reportPeriod,
      reportData: JSON.stringify(reportData),
    })
    ElMessage.success('报表提交成功')
    showDialog.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

const viewReport = (row: FinReport) => {
  ElMessage.info('查看报表详情：' + row.reportType + ' - ' + row.reportYear + '年')
}

onMounted(fetchData)
</script>

<style scoped>
.finance-report-list {
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
.search-card {
  margin-bottom: 16px;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
