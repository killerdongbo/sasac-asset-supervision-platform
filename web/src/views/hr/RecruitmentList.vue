<template>
  <div class="recruitment-list">
    <div class="toolbar">
      <el-button type="primary" @click="showCreateDialog">新增招聘需求</el-button>
    </div>

    <el-table :data="recruitments" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="positionName" label="职位名称" min-width="140" />
      <el-table-column prop="headcount" label="招聘人数" width="100" align="center" />
      <el-table-column prop="pipeline" label="招聘渠道" width="120">
        <template #default="{ row }">
          {{ pipelineMap[row.pipeline] || row.pipeline || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="candidates" label="候选人" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除该招聘需求吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button link type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
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

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑招聘需求' : '新增招聘需求'" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="职位名称" prop="positionName">
          <el-input v-model="form.positionName" placeholder="请输入职位名称" />
        </el-form-item>
        <el-form-item label="招聘人数" prop="headcount">
          <el-input-number v-model="form.headcount" :min="1" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="招聘渠道" prop="pipeline">
          <el-select v-model="form.pipeline" placeholder="请选择" style="width: 100%" clearable>
            <el-option label="校园招聘" value="CAMPUS" />
            <el-option label="社会招聘" value="SOCIAL" />
            <el-option label="内部推荐" value="INTERNAL" />
            <el-option label="猎头" value="HEADHUNTER" />
          </el-select>
        </el-form-item>
        <el-form-item label="候选人" prop="candidates">
          <el-input v-model="form.candidates" type="textarea" :rows="3" placeholder="候选人名单（逗号分隔）" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
            <el-option label="招聘中" value="OPEN" />
            <el-option label="已关闭" value="CLOSED" />
            <el-option label="已到岗" value="FILLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存修改' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { queryRecruitments, createRecruitment, updateRecruitment, getRecruitment, deleteRecruitment } from '@/api/hr'
import type { HrRecruitment } from '@/api/hr'

const recruitments = ref<HrRecruitment[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const pipelineMap: Record<string, string> = {
  CAMPUS: '校园招聘',
  SOCIAL: '社会招聘',
  INTERNAL: '内部推荐',
  HEADHUNTER: '猎头'
}

const statusMap: Record<string, string> = {
  OPEN: '招聘中',
  CLOSED: '已关闭',
  FILLED: '已到岗'
}

function statusType(status: string): string {
  const map: Record<string, string> = { OPEN: 'success', CLOSED: 'info', FILLED: 'primary' }
  return map[status] || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    const params: Record<string, unknown> = { page: currentPage.value, limit: pageSize.value }
    if (tenantIdStr) params.tenantId = Number(tenantIdStr)

    const response = await queryRecruitments(params as any)
    recruitments.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    recruitments.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// Dialog
const dialogVisible = ref(false)
const isEdit = computed(() => !!editId.value)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const editId = ref('')

const form = reactive({
  positionName: '',
  headcount: 1,
  pipeline: '',
  candidates: '',
  status: 'OPEN'
})

const formRules: FormRules = {
  positionName: [{ required: true, message: '请输入职位名称', trigger: 'blur' }]
}

function showCreateDialog() {
  editId.value = ''
  form.positionName = ''
  form.headcount = 1
  form.pipeline = ''
  form.candidates = ''
  form.status = 'OPEN'
  dialogVisible.value = true
}

async function showEditDialog(row: HrRecruitment) {
  editId.value = row.id
  try {
    const response = await getRecruitment(row.id)
    const data = response.data
    form.positionName = data.positionName || ''
    form.headcount = data.headcount || 1
    form.pipeline = data.pipeline || ''
    form.candidates = data.candidates || ''
    form.status = data.status || 'OPEN'
    dialogVisible.value = true
  } catch {
    ElMessage.error('获取招聘信息失败')
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    if (tenantIdStr === null) {
      ElMessage.error('缺少组织信息')
      submitting.value = false
      return
    }
    const payload = {
      ...form,
      tenantId: Number(tenantIdStr),
      orgId: Number(localStorage.getItem('orgId') || tenantIdStr)
    }

    if (isEdit.value) {
      await updateRecruitment(editId.value, payload)
      ElMessage.success('保存成功')
    } else {
      await createRecruitment(payload)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: string) {
  try {
    await deleteRecruitment(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // Error handled by interceptor
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.recruitment-list {
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
