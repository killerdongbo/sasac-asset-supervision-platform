<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCases, openCase, investigateCase, decidePunishment } from '@/api/supervision'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)

// Dialog states
const createDialogVisible = ref(false)
const investigateDialogVisible = ref(false)
const decideDialogVisible = ref(false)

const currentCase = ref<any>(null)
const formRef = ref<any>(null)

const createForm = ref({
  tenantId: 1,
  caseTitle: '',
  violationType: 'FRAUD',
  suspectId: undefined as number | undefined,
  suspectName: ''
})

const investigateForm = ref({ result: '', assetLoss: undefined as number | undefined })
const decideForm = ref({ decision: '' })

const typeMap: Record<string, string> = {
  FRAUD: '贪污舞弊',
  EMBEZZLEMENT: '挪用公款',
  NEGLIGENCE: '失职渎职',
  POLICY_VIOLATION: '违规经营',
  OTHER: '其他'
}

const statusMap: Record<string, { type: string; text: string }> = {
  INVESTIGATING: { type: 'warning', text: '调查中' },
  RESOLVED: { type: 'success', text: '已结案' },
  DISMISSED: { type: 'info', text: '已驳回' }
}

async function fetch() {
  loading.value = true
  try {
    const res = await getCases()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function formatMoney(val: number | null | undefined) {
  if (val == null) return '-'
  return '¥' + Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function openCreate() {
  createForm.value = { tenantId: 1, caseTitle: '', violationType: 'FRAUD', suspectId: undefined, suspectName: '' }
  createDialogVisible.value = true
}

async function handleCreate() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await openCase(createForm.value)
    ElMessage.success('立案成功')
    createDialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function openInvestigate(row: any) {
  currentCase.value = row
  investigateForm.value = { result: '', assetLoss: undefined }
  investigateDialogVisible.value = true
}

async function handleInvestigate() {
  if (!investigateForm.value.result.trim()) {
    ElMessage.warning('请输入调查结果')
    return
  }
  try {
    await investigateCase(currentCase.value.id, investigateForm.value.result, investigateForm.value.assetLoss)
    ElMessage.success('调查结果已记录')
    investigateDialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

function openDecide(row: any) {
  currentCase.value = row
  decideForm.value = { decision: '' }
  decideDialogVisible.value = true
}

async function handleDecide() {
  if (!decideForm.value.decision.trim()) {
    ElMessage.warning('请输入处理决定')
    return
  }
  try {
    await decidePunishment(currentCase.value.id, decideForm.value.decision)
    ElMessage.success('处理决定已记录')
    decideDialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h3>违规经营案例</h3>
      <el-button type="primary" @click="openCreate">立案调查</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="caseNo" label="案件编号" width="180" />
      <el-table-column prop="caseTitle" label="案件标题" min-width="200" />
      <el-table-column label="违规类型" width="120">
        <template #default="{ row }">
          {{ typeMap[row.violationType] || row.violationType }}
        </template>
      </el-table-column>
      <el-table-column prop="suspectName" label="嫌疑人" width="100" />
      <el-table-column label="资产损失" width="140">
        <template #default="{ row }">
          <span :style="row.assetLoss ? { color: '#f56c6c', fontWeight: 'bold' } : {}">{{ formatMoney(row.assetLoss) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusMap[row.status]?.type">
            {{ statusMap[row.status]?.text || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button v-if="row.status === 'INVESTIGATING'" link type="primary" @click="openInvestigate(row)">记录结果</el-button>
          <el-button v-if="row.status === 'INVESTIGATING'" link type="success" @click="openDecide(row)">处理决定</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="createDialogVisible" title="立案调查" width="600px">
      <el-form ref="formRef" :model="createForm" label-width="120px">
        <el-form-item label="案件标题" prop="caseTitle" :rules="[{ required: true, message: '请输入案件标题' }]">
          <el-input v-model="createForm.caseTitle" />
        </el-form-item>
        <el-form-item label="违规类型" prop="violationType">
          <el-select v-model="createForm.violationType">
            <el-option label="贪污舞弊" value="FRAUD" />
            <el-option label="挪用公款" value="EMBEZZLEMENT" />
            <el-option label="失职渎职" value="NEGLIGENCE" />
            <el-option label="违规经营" value="POLICY_VIOLATION" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="嫌疑人ID" prop="suspectId">
          <el-input-number v-model="createForm.suspectId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="嫌疑人姓名" prop="suspectName">
          <el-input v-model="createForm.suspectName" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">立案</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="investigateDialogVisible" title="记录调查结果" width="500px">
      <p style="margin-bottom: 12px; color: #666;">案件：{{ currentCase?.caseTitle }}</p>
      <el-form :model="investigateForm" label-width="100px">
        <el-form-item label="调查结果">
          <el-input v-model="investigateForm.result" type="textarea" :rows="4" placeholder="请输入调查结果" />
        </el-form-item>
        <el-form-item label="资产损失">
          <el-input-number v-model="investigateForm.assetLoss" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="investigateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleInvestigate">保存结果</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="decideDialogVisible" title="处理决定" width="500px">
      <p style="margin-bottom: 12px; color: #666;">案件：{{ currentCase?.caseTitle }}</p>
      <el-input v-model="decideForm.decision" type="textarea" :rows="4" placeholder="请输入处理决定" />
      <template #footer>
        <el-button @click="decideDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDecide">确认决定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
