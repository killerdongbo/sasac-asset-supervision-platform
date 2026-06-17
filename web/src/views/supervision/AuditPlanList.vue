<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAuditPlans, createAuditPlan, updateAuditPlan, deleteAuditPlan } from '@/api/supervision'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref(false)
const formRef = ref<any>(null)

const form = ref({
  tenantId: 1,
  orgId: 1,
  planYear: new Date().getFullYear(),
  planName: '',
  auditScope: '',
  auditTeam: '',
  status: 'DRAFT'
})

async function fetch() {
  loading.value = true
  try {
    const res = await getAuditPlans()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = false
  form.value = { tenantId: 1, orgId: 1, planYear: new Date().getFullYear(), planName: '', auditScope: '', auditTeam: '', status: 'DRAFT' }
  dialogVisible.value = true
}

function openEdit(row: any) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    if (editing.value) {
      await updateAuditPlan(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createAuditPlan(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetch()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '操作失败')
  }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确认删除此审计计划？', '提示', { type: 'warning' })
  await deleteAuditPlan(row.id)
  ElMessage.success('删除成功')
  fetch()
}

const statusMap: Record<string, string> = {
  DRAFT: '草稿',
  PUBLISHED: '已发布',
  EXECUTING: '执行中',
  COMPLETED: '已完成'
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h3>审计计划管理</h3>
      <el-button type="primary" @click="openCreate">新增计划</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="planYear" label="年度" width="80" />
      <el-table-column prop="planName" label="计划名称" min-width="200" />
      <el-table-column prop="auditScope" label="审计范围" min-width="240" show-overflow-tooltip />
      <el-table-column prop="auditTeam" label="审计团队" min-width="180" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'DRAFT' ? 'info' : row.status === 'PUBLISHED' ? 'primary' : row.status === 'EXECUTING' ? 'warning' : 'success'">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑审计计划' : '新增审计计划'" width="600px">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="计划年度" prop="planYear" :rules="[{ required: true, message: '请输入计划年度' }]">
          <el-input-number v-model="form.planYear" :min="2020" :max="2030" />
        </el-form-item>
        <el-form-item label="计划名称" prop="planName" :rules="[{ required: true, message: '请输入计划名称' }]">
          <el-input v-model="form.planName" />
        </el-form-item>
        <el-form-item label="审计范围" prop="auditScope">
          <el-input v-model="form.auditScope" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="审计团队" prop="auditTeam">
          <el-input v-model="form.auditTeam" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="执行中" value="EXECUTING" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
