<template>
  <div class="fund-monitor">
    <div class="page-header">
      <h2>大额资金监控</h2>
      <el-button type="primary" @click="showDialog = true">录入交易</el-button>
    </div>

    <!-- Search / Filter -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="是否预警">
          <el-select v-model="query.isFlagged" placeholder="全部" clearable style="width: 140px">
            <el-option label="已预警" :value="true" />
            <el-option label="未预警" :value="false" />
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
      <el-table :data="funds" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="transactionNo" label="交易编号" width="180" />
        <el-table-column prop="transactionDate" label="交易日期" width="180">
          <template #default="{ row }">
            {{ row.transactionDate ? new Date(row.transactionDate).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="140">
          <template #default="{ row }">
            <span :style="{ color: row.isFlagged ? '#f56c6c' : 'inherit', fontWeight: row.isFlagged ? 'bold' : 'normal' }">
              ¥{{ Number(row.amount).toLocaleString() }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="payer" label="付款方" width="150" />
        <el-table-column prop="payee" label="收款方" width="150" />
        <el-table-column prop="purpose" label="用途" min-width="200" show-overflow-tooltip />
        <el-table-column prop="isFlagged" label="预警" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isFlagged" type="danger" size="small">已预警</el-tag>
            <el-tag v-else type="info" size="small">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="flagReason" label="预警原因" width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.flagReason" style="color: #f56c6c;">{{ row.flagReason }}</span>
            <span v-else>-</span>
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

    <!-- Add Transaction Dialog -->
    <el-dialog v-model="showDialog" title="录入交易" width="550px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="交易编号" prop="transactionNo">
          <el-input v-model="form.transactionNo" placeholder="自动生成留空" />
        </el-form-item>
        <el-form-item label="交易日期" prop="transactionDate">
          <el-date-picker v-model="form.transactionDate" type="datetime" placeholder="选择日期时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" placeholder="请输入金额" />
        </el-form-item>
        <el-form-item label="付款方" prop="payer">
          <el-input v-model="form.payer" placeholder="付款方名称" />
        </el-form-item>
        <el-form-item label="收款方" prop="payee">
          <el-input v-model="form.payee" placeholder="收款方名称" />
        </el-form-item>
        <el-form-item label="用途" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" :rows="2" placeholder="交易用途说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确认录入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryFunds, monitorFund, type FinFundMonitor } from '@/api/finance'
import type { FormInstance } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const funds = ref<FinFundMonitor[]>([])
const total = ref(0)
const showDialog = ref(false)
const formRef = ref<FormInstance>()

const query = ref({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  isFlagged: undefined as boolean | undefined,
  page: 1,
  limit: 10,
})

const form = reactive({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  orgId: Number(localStorage.getItem('orgId') || 1),
  transactionNo: '',
  transactionDate: null as string | null,
  amount: 0,
  payer: '',
  payee: '',
  purpose: '',
})

const rules = {
  transactionDate: [{ required: true, message: '请选择交易日期', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await queryFunds(query.value)
    funds.value = res.data
    if (res.meta) total.value = res.meta.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.value.isFlagged = undefined
  query.value.page = 1
  fetchData()
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await monitorFund({
      ...form,
      transactionDate: form.transactionDate as string,
    })
    ElMessage.success('交易录入成功')
    showDialog.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.fund-monitor {
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
