<template>
  <div class="inquiry-list">
    <div class="page-header">
      <h2>询价管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">新建询价</el-button>
    </div>

    <el-card>
      <div class="filter-bar">
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable @change="loadData">
          <el-option label="草稿" value="DRAFT" />
          <el-option label="进行中" value="OPEN" />
          <el-option label="已关闭" value="CLOSED" />
        </el-select>
      </div>

      <el-table :data="inquiries" v-loading="loading" stripe>
        <el-table-column prop="inquiryNo" label="询价编号" width="160" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="category" label="品类" width="120" />
        <el-table-column prop="specification" label="规格" width="140" />
        <el-table-column prop="quantity" label="数量" width="80" align="right" />
        <el-table-column prop="budgetAmount" label="预算金额" width="120" align="right">
          <template #default="{ row }">
            {{ row.budgetAmount ? `¥${Number(row.budgetAmount).toLocaleString()}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">查看报价</el-button>
            <el-button link type="success" v-if="row.status === 'DRAFT'" @click="doPublish(row)">发布</el-button>
            <el-button link type="warning" v-if="row.status === 'OPEN'" @click="doClose(row)">关闭</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        :page-size="20"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadData"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="showCreateDialog" title="新建询价" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入询价标题" />
        </el-form-item>
        <el-form-item label="品类">
          <el-input v-model="form.category" placeholder="如：办公设备、车辆" />
        </el-form-item>
        <el-form-item label="规格型号">
          <el-input v-model="form.specification" placeholder="规格型号描述" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="台/套/件" style="width: 120px" />
        </el-form-item>
        <el-form-item label="预算金额">
          <el-input-number v-model="form.budgetAmount" :min="0" :precision="2" :step="1000" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="form.deadline" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="doCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listInquiries, createInquiry, publishInquiry, closeInquiry } from '@/api/quotation'
import type { Inquiry } from '@/api/quotation'

const router = useRouter()
const loading = ref(false)
const inquiries = ref<Inquiry[]>([])
const statusFilter = ref('')
const page = ref(1)
const total = ref(0)
const showCreateDialog = ref(false)

const form = ref({
  title: '',
  category: '',
  specification: '',
  quantity: 1,
  unit: '台',
  budgetAmount: null as number | null,
  deadline: ''
})

const statusType = (s: string) => ({ DRAFT: 'info', OPEN: 'success', CLOSED: 'warning' }[s] || 'info')
const statusLabel = (s: string) => ({ DRAFT: '草稿', OPEN: '进行中', CLOSED: '已关闭' }[s] || s)

async function loadData() {
  loading.value = true
  try {
    const res = await listInquiries({ status: statusFilter.value || undefined, page: page.value, size: 20 })
    inquiries.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function doCreate() {
  await createInquiry(form.value)
  ElMessage.success('询价单已创建')
  showCreateDialog.value = false
  form.value = { title: '', category: '', specification: '', quantity: 1, unit: '台', budgetAmount: null, deadline: '' }
  loadData()
}

async function doPublish(row: Inquiry) {
  await publishInquiry(row.id)
  ElMessage.success('已发布')
  loadData()
}

async function doClose(row: Inquiry) {
  await closeInquiry(row.id)
  ElMessage.success('已关闭')
  loadData()
}

function viewDetail(row: Inquiry) {
  router.push(`/quotation/compare/${row.id}`)
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-bar { margin-bottom: 16px; }
</style>
