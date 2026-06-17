<template>
  <div class="assessment-list">
    <div class="toolbar">
      <el-button type="primary" @click="showAddDialog = true">新增评估备案</el-button>
    </div>

    <el-table :data="assessments" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="assessNo" label="评估编号" width="180" />
      <el-table-column prop="assessAgency" label="评估机构" min-width="180" />
      <el-table-column prop="bookValue" label="账面值(万元)" width="140">
        <template #default="{ row }">{{ formatMoney(row.bookValue) }}</template>
      </el-table-column>
      <el-table-column prop="assessedValue" label="评估值(万元)" width="140">
        <template #default="{ row }">{{ formatMoney(row.assessedValue) }}</template>
      </el-table-column>
      <el-table-column prop="deviationPct" label="偏差率" width="100">
        <template #default="{ row }">
          <span :style="{ color: row.deviationPct > 10 ? '#e6a23c' : '#67c23a' }">
            {{ row.deviationPct != null ? row.deviationPct + '%' : '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="assessDate" label="评估日期" width="120" />
      <el-table-column prop="approvalStatus" label="备案状态" width="120">
        <template #default="{ row }">
          <el-tag :type="approvalTag(row.approvalStatus)">
            {{ approvalMap[row.approvalStatus] || row.approvalStatus }}
          </el-tag>
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

    <!-- Add Assessment Dialog -->
    <el-dialog v-model="showAddDialog" title="新增资产评估备案" width="700px">
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="120px">
        <el-form-item label="评估编号" prop="assessNo">
          <el-input v-model="dialogForm.assessNo" placeholder="请输入评估编号" />
        </el-form-item>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="评估机构" prop="assessAgency">
              <el-input v-model="dialogForm.assessAgency" placeholder="请输入评估机构" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评估目的" prop="assessPurpose">
              <el-select v-model="dialogForm.assessPurpose" placeholder="请选择" clearable style="width: 100%">
                <el-option label="产权转让" value="TRANSFER" />
                <el-option label="增资扩股" value="CAPITAL_INCREASE" />
                <el-option label="改制重组" value="RESTRUCTURING" />
                <el-option label="抵押担保" value="MORTGAGE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="评估方法" prop="assessMethod">
              <el-select v-model="dialogForm.assessMethod" placeholder="请选择" clearable style="width: 100%">
                <el-option label="收益法" value="INCOME" />
                <el-option label="市场法" value="MARKET" />
                <el-option label="成本法" value="COST" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评估日期" prop="assessDate">
              <el-date-picker v-model="dialogForm.assessDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="账面值(万元)" prop="bookValue">
              <el-input-number v-model="dialogForm.bookValue" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评估值(万元)" prop="assessedValue">
              <el-input-number v-model="dialogForm.assessedValue" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddAssessment" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createAssessment, queryAssessments } from '@/api/property'
import type { PrAssessment } from '@/api/property'

const assessments = ref<PrAssessment[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const showAddDialog = ref(false)
const submitting = ref(false)
const dialogFormRef = ref<FormInstance>()

interface AssessmentFormData {
  assessNo: string
  assessAgency: string
  assessPurpose: string
  assessMethod: string
  bookValue: number | undefined
  assessedValue: number | undefined
  assessDate: string
}

const dialogForm = reactive<AssessmentFormData>({
  assessNo: '',
  assessAgency: '',
  assessPurpose: '',
  assessMethod: '',
  bookValue: undefined,
  assessedValue: undefined,
  assessDate: ''
})

const dialogRules: FormRules = {
  assessNo: [{ required: true, message: '请输入评估编号', trigger: 'blur' }],
  assessAgency: [{ required: true, message: '请输入评估机构', trigger: 'blur' }]
}

const approvalMap: Record<string, string> = {
  PENDING: '待备案',
  APPROVED: '已备案',
  REJECTED: '已驳回'
}

function approvalTag(status: string): string {
  const map: Record<string, string> = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger'
  }
  return map[status] || 'info'
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
    const response = await queryAssessments({
      tenantId: getTenantId(),
      page: currentPage.value,
      limit: pageSize.value
    })
    assessments.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    assessments.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function handleAddAssessment() {
  if (!dialogFormRef.value) return
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createAssessment({
      ...dialogForm,
      tenantId: getTenantId()
    } as any)
    ElMessage.success('评估备案已提交')
    showAddDialog.value = false
    dialogForm.assessNo = ''
    dialogForm.assessAgency = ''
    dialogForm.assessPurpose = ''
    dialogForm.assessMethod = ''
    dialogForm.bookValue = undefined
    dialogForm.assessedValue = undefined
    dialogForm.assessDate = ''
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
.assessment-list {
  padding: 0;
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
