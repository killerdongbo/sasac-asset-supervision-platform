<template>
  <div class="transaction-monitor">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="异常监测">
          <el-select v-model="queryForm.isAbnormal" placeholder="全部" clearable style="width: 140px">
            <el-option label="全部" :value="undefined" />
            <el-option label="异常" :value="true" />
            <el-option label="正常" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="toolbar">
      <el-button type="primary" @click="showAddDialog = true">新增监测</el-button>
    </div>

    <el-table :data="transactions" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="exchangeName" label="交易所" width="140" />
      <el-table-column prop="projectName" label="项目名称" min-width="180" />
      <el-table-column prop="listingPrice" label="挂牌价(万元)" width="140">
        <template #default="{ row }">{{ formatMoney(row.listingPrice) }}</template>
      </el-table-column>
      <el-table-column prop="transactionPrice" label="成交价(万元)" width="140">
        <template #default="{ row }">{{ formatMoney(row.transactionPrice) }}</template>
      </el-table-column>
      <el-table-column prop="priceDeviationPct" label="价格偏差率" width="120">
        <template #default="{ row }">
          <span :style="{ color: row.priceDeviationPct > 15 ? '#f56c6c' : '#67c23a', fontWeight: row.priceDeviationPct > 15 ? 'bold' : 'normal' }">
            {{ row.priceDeviationPct != null ? row.priceDeviationPct + '%' : '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="buyerName" label="受让方" width="140" />
      <el-table-column prop="isAbnormal" label="异常标记" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.isAbnormal" type="danger" size="small">异常</el-tag>
          <el-tag v-else type="success" size="small">正常</el-tag>
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

    <!-- Add Transaction Dialog -->
    <el-dialog v-model="showAddDialog" title="新增交易监测" width="600px">
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="120px">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="交易所" prop="exchangeName">
              <el-input v-model="dialogForm.exchangeName" placeholder="请输入交易所名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="挂牌编号" prop="listingNo">
              <el-input v-model="dialogForm.listingNo" placeholder="请输入挂牌编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="dialogForm.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="挂牌价(万元)" prop="listingPrice">
              <el-input-number v-model="dialogForm.listingPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成交价(万元)" prop="transactionPrice">
              <el-input-number v-model="dialogForm.transactionPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="竞价开始日" prop="bidStartDate">
              <el-date-picker v-model="dialogForm.bidStartDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="竞价结束日" prop="bidEndDate">
              <el-date-picker v-model="dialogForm.bidEndDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="受让方" prop="buyerName">
          <el-input v-model="dialogForm.buyerName" placeholder="请输入受让方名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddTransaction" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { queryTransactions, createTransaction } from '@/api/property'
import type { PrTransactionMonitor } from '@/api/property'

const queryForm = reactive({
  isAbnormal: undefined as boolean | undefined
})

const transactions = ref<PrTransactionMonitor[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const showAddDialog = ref(false)
const submitting = ref(false)
const dialogFormRef = ref<FormInstance>()

interface TransactionFormData {
  exchangeName: string
  listingNo: string
  projectName: string
  listingPrice: number | undefined
  transactionPrice: number | undefined
  bidStartDate: string
  bidEndDate: string
  buyerName: string
}

const dialogForm = reactive<TransactionFormData>({
  exchangeName: '',
  listingNo: '',
  projectName: '',
  listingPrice: undefined,
  transactionPrice: undefined,
  bidStartDate: '',
  bidEndDate: '',
  buyerName: ''
})

const dialogRules: FormRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }]
}

function getTenantId(): number {
  return Number(localStorage.getItem('tenantId')) || 0
}

function formatMoney(val: number | undefined | null): string {
  if (val == null) return '-'
  return val.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      tenantId: getTenantId(),
      page: currentPage.value,
      limit: pageSize.value
    }
    if (queryForm.isAbnormal !== undefined) {
      params.isAbnormal = queryForm.isAbnormal
    }

    const response = await queryTransactions(params as any)
    transactions.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    transactions.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchData()
}

function handleReset() {
  queryForm.isAbnormal = undefined
  currentPage.value = 1
  fetchData()
}

async function handleAddTransaction() {
  if (!dialogFormRef.value) return
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createTransaction({
      ...dialogForm,
      tenantId: getTenantId()
    } as any)
    ElMessage.success('添加成功')
    showAddDialog.value = false
    dialogForm.exchangeName = ''
    dialogForm.listingNo = ''
    dialogForm.projectName = ''
    dialogForm.listingPrice = undefined
    dialogForm.transactionPrice = undefined
    dialogForm.bidStartDate = ''
    dialogForm.bidEndDate = ''
    dialogForm.buyerName = ''
    fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.transaction-monitor {
  padding: 0;
}

.search-card {
  margin-bottom: 16px;
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
