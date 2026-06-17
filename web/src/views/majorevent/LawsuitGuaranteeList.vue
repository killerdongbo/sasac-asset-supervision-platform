<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import {
  getLawsuits, createLawsuit, updateLawsuit, deleteLawsuit, updateLawsuitStatus,
  getGuarantees, createGuarantee, updateGuarantee, deleteGuarantee, getExpiringGuarantees
} from '@/api/majorevent'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('lawsuits')

// ==================== Lawsuit ====================

const lawsuitList = ref<any[]>([])
const lawsuitTotal = ref(0)
const lawsuitLoading = ref(false)
const lawsuitPage = ref(1)
const lawsuitPageSize = 20

const lawsuitQuery = ref({ court: '', status: '' })

const lawsuitDialogVisible = ref(false)
const lawsuitEditing = ref(false)
const lawsuitFormRef = ref<any>(null)
const lawsuitForm = ref({
  eventId: undefined as number | undefined,
  caseNo: '',
  court: '',
  plaintiff: '',
  defendant: '',
  claimAmount: undefined as number | undefined,
  description: '',
  status: 'PENDING'
})

const lawsuitStatusDialogVisible = ref(false)
const currentLawsuit = ref<any>(null)
const lawsuitStatusForm = ref({ status: '', progress: '' })

const lawsuitStatusOptions = [
  { value: 'PENDING', label: '待立案' },
  { value: 'TRIAL', label: '审理中' },
  { value: 'CLOSED', label: '已结案' }
]

const lawsuitStatusMap: Record<string, { type: string; text: string }> = {
  PENDING: { type: 'info', text: '待立案' },
  TRIAL: { type: 'warning', text: '审理中' },
  CLOSED: { type: 'success', text: '已结案' }
}

async function fetchLawsuits() {
  lawsuitLoading.value = true
  try {
    const params: any = { page: lawsuitPage.value, size: lawsuitPageSize }
    if (lawsuitQuery.value.court) params.court = lawsuitQuery.value.court
    if (lawsuitQuery.value.status) params.status = lawsuitQuery.value.status
    const res = await getLawsuits(params)
    lawsuitList.value = res.data?.records || []
    lawsuitTotal.value = res.data?.total || 0
  } finally {
    lawsuitLoading.value = false
  }
}

function searchLawsuits() {
  lawsuitPage.value = 1
  fetchLawsuits()
}

function resetLawsuitSearch() {
  lawsuitQuery.value = { court: '', status: '' }
  lawsuitPage.value = 1
  fetchLawsuits()
}

function openLawsuitCreate() {
  lawsuitEditing.value = false
  lawsuitForm.value = { eventId: undefined, caseNo: '', court: '', plaintiff: '', defendant: '', claimAmount: undefined, description: '', status: 'PENDING' }
  lawsuitDialogVisible.value = true
}

function openLawsuitEdit(row: any) {
  lawsuitEditing.value = true
  lawsuitForm.value = { ...row }
  lawsuitDialogVisible.value = true
}

async function handleLawsuitSave() {
  const valid = await lawsuitFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    if (lawsuitEditing.value) {
      await updateLawsuit(lawsuitForm.value.id, lawsuitForm.value)
      ElMessage.success('更新成功')
    } else {
      await createLawsuit(lawsuitForm.value)
      ElMessage.success('创建成功')
    }
    lawsuitDialogVisible.value = false
    fetchLawsuits()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

async function handleLawsuitDelete(row: any) {
  await ElMessageBox.confirm('确认删除此诉讼记录？', '提示', { type: 'warning' })
  try {
    await deleteLawsuit(row.id)
    ElMessage.success('删除成功')
    fetchLawsuits()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function openLawsuitStatus(row: any) {
  currentLawsuit.value = row
  lawsuitStatusForm.value = { status: row.status, progress: '' }
  lawsuitStatusDialogVisible.value = true
}

async function handleLawsuitStatusUpdate() {
  if (!lawsuitStatusForm.value.status) {
    ElMessage.warning('请选择状态')
    return
  }
  if (!lawsuitStatusForm.value.progress.trim()) {
    ElMessage.warning('请输入进展说明')
    return
  }
  try {
    await updateLawsuitStatus(currentLawsuit.value.id, lawsuitStatusForm.value.status, lawsuitStatusForm.value.progress)
    ElMessage.success('状态更新成功')
    lawsuitStatusDialogVisible.value = false
    fetchLawsuits()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

// ==================== Guarantee ====================

const guaranteeList = ref<any[]>([])
const guaranteeTotal = ref(0)
const guaranteeLoading = ref(false)
const guaranteePage = ref(1)
const guaranteePageSize = 20

const guaranteeQuery = ref({ guaranteeType: '', riskLevel: '' })

const guaranteeDialogVisible = ref(false)
const guaranteeEditing = ref(false)
const guaranteeFormRef = ref<any>(null)
const guaranteeForm = ref({
  eventId: undefined as number | undefined,
  guaranteeType: '',
  beneficiary: '',
  guaranteeAmount: undefined as number | undefined,
  startDate: '',
  endDate: '',
  riskLevel: 'LOW',
  description: '',
  status: 'ACTIVE'
})

const guaranteeTypeOptions = [
  { value: 'CREDIT', label: '信贷担保' },
  { value: 'BID_BOND', label: '投标保函' },
  { value: 'PERFORMANCE', label: '履约担保' },
  { value: 'ADVANCE_PAYMENT', label: '预付款担保' },
  { value: 'QUALITY', label: '质量担保' },
  { value: 'OTHER', label: '其他' }
]

const riskLevelOptions = [
  { value: 'LOW', label: '低风险' },
  { value: 'MEDIUM', label: '中风险' },
  { value: 'HIGH', label: '高风险' }
]

const riskLevelMap: Record<string, { type: string; text: string }> = {
  LOW: { type: 'success', text: '低风险' },
  MEDIUM: { type: 'warning', text: '中风险' },
  HIGH: { type: 'danger', text: '高风险' }
}

const guaranteeStatusOptions = [
  { value: 'ACTIVE', label: '有效' },
  { value: 'EXPIRED', label: '已到期' },
  { value: 'CANCELLED', label: '已撤销' }
]

const guaranteeStatusMap: Record<string, { type: string; text: string }> = {
  ACTIVE: { type: 'success', text: '有效' },
  EXPIRED: { type: 'info', text: '已到期' },
  CANCELLED: { type: 'danger', text: '已撤销' }
}

async function fetchGuarantees() {
  guaranteeLoading.value = true
  try {
    const params: any = { page: guaranteePage.value, size: guaranteePageSize }
    if (guaranteeQuery.value.guaranteeType) params.guaranteeType = guaranteeQuery.value.guaranteeType
    if (guaranteeQuery.value.riskLevel) params.riskLevel = guaranteeQuery.value.riskLevel
    const res = await getGuarantees(params)
    guaranteeList.value = res.data?.records || []
    guaranteeTotal.value = res.data?.total || 0
  } finally {
    guaranteeLoading.value = false
  }
}

function searchGuarantees() {
  guaranteePage.value = 1
  fetchGuarantees()
}

function resetGuaranteeSearch() {
  guaranteeQuery.value = { guaranteeType: '', riskLevel: '' }
  guaranteePage.value = 1
  fetchGuarantees()
}

function isExpiringSoon(endDate: string): boolean {
  if (!endDate) return false
  const now = new Date()
  const end = new Date(endDate)
  const diffDays = Math.ceil((end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  return diffDays >= 0 && diffDays <= 30
}

async function handleCheckExpiring() {
  try {
    const res = await getExpiringGuarantees(1, 30)
    const count = res.data?.length || 0
    if (count > 0) {
      ElMessage.warning(`共有 ${count} 条担保即将到期`)
      guaranteeList.value = res.data || []
    } else {
      ElMessage.info('暂无即将到期的担保')
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function openGuaranteeCreate() {
  guaranteeEditing.value = false
  guaranteeForm.value = { eventId: undefined, guaranteeType: '', beneficiary: '', guaranteeAmount: undefined, startDate: '', endDate: '', riskLevel: 'LOW', description: '', status: 'ACTIVE' }
  guaranteeDialogVisible.value = true
}

function openGuaranteeEdit(row: any) {
  guaranteeEditing.value = true
  guaranteeForm.value = { ...row }
  guaranteeDialogVisible.value = true
}

async function handleGuaranteeSave() {
  const valid = await guaranteeFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    if (guaranteeEditing.value) {
      await updateGuarantee(guaranteeForm.value.id, guaranteeForm.value)
      ElMessage.success('更新成功')
    } else {
      await createGuarantee(guaranteeForm.value)
      ElMessage.success('创建成功')
    }
    guaranteeDialogVisible.value = false
    fetchGuarantees()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

async function handleGuaranteeDelete(row: any) {
  await ElMessageBox.confirm('确认删除此担保记录？', '提示', { type: 'warning' })
  try {
    await deleteGuarantee(row.id)
    ElMessage.success('删除成功')
    fetchGuarantees()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

// ==================== Common ====================

function formatCurrency(val: number | null | undefined): string {
  if (val == null) return '-'
  return '¥' + Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function formatTime(time: string): string {
  return time?.replace('T', ' ').substring(0, 16) || ''
}

function formatDate(date: string): string {
  return date?.substring(0, 10) || ''
}

function handleLawsuitPageChange(page: number) {
  lawsuitPage.value = page
  fetchLawsuits()
}

function handleGuaranteePageChange(page: number) {
  guaranteePage.value = page
  fetchGuarantees()
}

onMounted(() => {
  fetchLawsuits()
  fetchGuarantees()
})
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h3>诉讼与担保管理</h3>
    </div>

    <el-tabs v-model="activeTab" type="border-card">
      <!-- ========== Lawsuit Tab ========== -->
      <el-tab-pane label="重大诉讼" name="lawsuits">
        <el-card shadow="never" class="search-card">
          <el-form :model="lawsuitQuery" inline>
            <el-form-item label="法院">
              <el-input v-model="lawsuitQuery.court" placeholder="请输入法院名称" clearable style="width: 200px" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="lawsuitQuery.status" placeholder="全部状态" clearable style="width: 140px">
                <el-option v-for="o in lawsuitStatusOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchLawsuits">查询</el-button>
              <el-button @click="resetLawsuitSearch">重置</el-button>
            </el-form-item>
            <el-form-item style="margin-left: auto;">
              <el-button type="primary" @click="openLawsuitCreate">新增诉讼</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-table :data="lawsuitList" v-loading="lawsuitLoading" border stripe style="margin-top: 16px">
          <el-table-column prop="caseNo" label="案号" min-width="160" />
          <el-table-column prop="court" label="法院" min-width="180" show-overflow-tooltip />
          <el-table-column prop="plaintiff" label="原告" width="120" />
          <el-table-column prop="defendant" label="被告" width="120" />
          <el-table-column label="诉讼金额" width="140">
            <template #default="{ row }">
              <span :style="row.claimAmount ? { color: '#f56c6c', fontWeight: 'bold' } : {}">{{ formatCurrency(row.claimAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="案情描述" min-width="200" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="lawsuitStatusMap[row.status]?.type" size="small">
                {{ lawsuitStatusMap[row.status]?.text || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status !== 'CLOSED'" link type="primary" @click="openLawsuitStatus(row)">更新状态</el-button>
              <el-button link type="primary" @click="openLawsuitEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="handleLawsuitDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-if="lawsuitTotal > lawsuitPageSize"
          v-model:current-page="lawsuitPage"
          :page-size="lawsuitPageSize"
          :total="lawsuitTotal"
          layout="prev, pager, next, total"
          @current-change="handleLawsuitPageChange"
          style="margin-top: 16px; justify-content: center;"
        />
      </el-tab-pane>

      <!-- ========== Guarantee Tab ========== -->
      <el-tab-pane label="重大担保" name="guarantees">
        <el-card shadow="never" class="search-card">
          <el-form :model="guaranteeQuery" inline>
            <el-form-item label="担保类型">
              <el-select v-model="guaranteeQuery.guaranteeType" placeholder="全部类型" clearable style="width: 160px">
                <el-option v-for="o in guaranteeTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="风险等级">
              <el-select v-model="guaranteeQuery.riskLevel" placeholder="全部等级" clearable style="width: 140px">
                <el-option v-for="o in riskLevelOptions" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchGuarantees">查询</el-button>
              <el-button @click="resetGuaranteeSearch">重置</el-button>
            </el-form-item>
            <el-form-item style="margin-left: auto;">
              <el-button type="warning" @click="handleCheckExpiring">检查即将到期</el-button>
              <el-button type="primary" @click="openGuaranteeCreate">新增担保</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-table :data="guaranteeList" v-loading="guaranteeLoading" border stripe style="margin-top: 16px">
          <el-table-column label="担保类型" width="120">
            <template #default="{ row }">
              {{ guaranteeTypeOptions.find(o => o.value === row.guaranteeType)?.label || row.guaranteeType }}
            </template>
          </el-table-column>
          <el-table-column prop="beneficiary" label="受益人" min-width="200" show-overflow-tooltip />
          <el-table-column label="担保金额" width="140">
            <template #default="{ row }">
              <span :style="{ fontWeight: 'bold' }">{{ formatCurrency(row.guaranteeAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="担保期限" width="240">
            <template #default="{ row }">
              <span :style="isExpiringSoon(row.endDate) ? { color: '#f56c6c', fontWeight: 'bold' } : {}">
                {{ formatDate(row.startDate) }} ~ {{ formatDate(row.endDate) }}
                <el-tag v-if="isExpiringSoon(row.endDate)" type="danger" size="small" effect="dark" style="margin-left: 4px">即将到期</el-tag>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="风险等级" width="100">
            <template #default="{ row }">
              <el-tag :type="riskLevelMap[row.riskLevel]?.type" size="small">
                {{ riskLevelMap[row.riskLevel]?.text || row.riskLevel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="guaranteeStatusMap[row.status]?.type" size="small">
                {{ guaranteeStatusMap[row.status]?.text || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openGuaranteeEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="handleGuaranteeDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-if="guaranteeTotal > guaranteePageSize"
          v-model:current-page="guaranteePage"
          :page-size="guaranteePageSize"
          :total="guaranteeTotal"
          layout="prev, pager, next, total"
          @current-change="handleGuaranteePageChange"
          style="margin-top: 16px; justify-content: center;"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- ========== Lawsuit Create/Edit Dialog ========== -->
    <el-dialog v-model="lawsuitDialogVisible" :title="lawsuitEditing ? '编辑诉讼' : '新增诉讼'" width="650px">
      <el-form ref="lawsuitFormRef" :model="lawsuitForm" label-width="100px">
        <el-form-item label="关联事项ID" prop="eventId">
          <el-input-number v-model="lawsuitForm.eventId" :min="1" controls-position="right" style="width: 200px" />
        </el-form-item>
        <el-form-item label="案号" prop="caseNo" :rules="[{ required: true, message: '请输入案号' }]">
          <el-input v-model="lawsuitForm.caseNo" />
        </el-form-item>
        <el-form-item label="法院" prop="court" :rules="[{ required: true, message: '请输入法院名称' }]">
          <el-input v-model="lawsuitForm.court" />
        </el-form-item>
        <el-form-item label="原告" prop="plaintiff">
          <el-input v-model="lawsuitForm.plaintiff" />
        </el-form-item>
        <el-form-item label="被告" prop="defendant">
          <el-input v-model="lawsuitForm.defendant" />
        </el-form-item>
        <el-form-item label="诉讼金额" prop="claimAmount">
          <el-input-number v-model="lawsuitForm.claimAmount" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="案情描述" prop="description">
          <el-input v-model="lawsuitForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="lawsuitForm.status" style="width: 100%">
            <el-option v-for="o in lawsuitStatusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="lawsuitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleLawsuitSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- ========== Lawsuit Status Update Dialog ========== -->
    <el-dialog v-model="lawsuitStatusDialogVisible" title="更新诉讼状态" width="500px">
      <p style="margin-bottom: 12px; color: #666;">案件：{{ currentLawsuit?.caseNo }}</p>
      <el-form :model="lawsuitStatusForm" label-width="100px">
        <el-form-item label="新状态">
          <el-select v-model="lawsuitStatusForm.status" style="width: 100%">
            <el-option v-for="o in lawsuitStatusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="进展说明">
          <el-input v-model="lawsuitStatusForm.progress" type="textarea" :rows="3" placeholder="请输入进展说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="lawsuitStatusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleLawsuitStatusUpdate">确认更新</el-button>
      </template>
    </el-dialog>

    <!-- ========== Guarantee Create/Edit Dialog ========== -->
    <el-dialog v-model="guaranteeDialogVisible" :title="guaranteeEditing ? '编辑担保' : '新增担保'" width="650px">
      <el-form ref="guaranteeFormRef" :model="guaranteeForm" label-width="120px">
        <el-form-item label="关联事项ID" prop="eventId">
          <el-input-number v-model="guaranteeForm.eventId" :min="1" controls-position="right" style="width: 200px" />
        </el-form-item>
        <el-form-item label="担保类型" prop="guaranteeType" :rules="[{ required: true, message: '请选择担保类型' }]">
          <el-select v-model="guaranteeForm.guaranteeType" style="width: 100%">
            <el-option v-for="o in guaranteeTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="受益人" prop="beneficiary" :rules="[{ required: true, message: '请输入受益方' }]">
          <el-input v-model="guaranteeForm.beneficiary" />
        </el-form-item>
        <el-form-item label="担保金额" prop="guaranteeAmount" :rules="[{ required: true, message: '请输入担保金额' }]">
          <el-input-number v-model="guaranteeForm.guaranteeAmount" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="guaranteeForm.startDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="到期日期" prop="endDate">
          <el-date-picker v-model="guaranteeForm.endDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-select v-model="guaranteeForm.riskLevel" style="width: 100%">
            <el-option v-for="o in riskLevelOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="guaranteeForm.status" style="width: 100%">
            <el-option v-for="o in guaranteeStatusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注说明" prop="description">
          <el-input v-model="guaranteeForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="guaranteeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGuaranteeSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
.search-card { margin-bottom: 0; }
</style>
