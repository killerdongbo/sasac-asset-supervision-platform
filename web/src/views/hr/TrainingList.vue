<template>
  <div class="training-list">
    <div class="toolbar">
      <el-button type="primary" @click="showCreateDialog">新增培训记录</el-button>
    </div>

    <el-table :data="trainings" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="courseName" label="课程名称" min-width="160" />
      <el-table-column prop="trainer" label="讲师" width="120" />
      <el-table-column prop="trainingDate" label="培训日期" width="120" />
      <el-table-column prop="durationHours" label="时长(小时)" width="110" align="center" />
      <el-table-column prop="attendees" label="参训人员" min-width="180" show-overflow-tooltip />
      <el-table-column prop="evaluationSummary" label="评估总结" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除该培训记录吗？" @confirm="handleDelete(row.id)">
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑培训记录' : '新增培训记录'" width="600px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="讲师" prop="trainer">
              <el-input v-model="form.trainer" placeholder="请输入讲师姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="培训日期" prop="trainingDate">
              <el-date-picker
                v-model="form.trainingDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长(小时)" prop="durationHours">
              <el-input-number v-model="form.durationHours" :min="0.5" :max="999" :step="0.5" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="参训人员" prop="attendees">
          <el-input v-model="form.attendees" type="textarea" :rows="2" placeholder="参训人员名单（逗号分隔）" />
        </el-form-item>
        <el-form-item label="评估总结" prop="evaluationSummary">
          <el-input v-model="form.evaluationSummary" type="textarea" :rows="3" placeholder="培训效果评估" />
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
import { queryTrainings, createTraining, updateTraining, getTraining, deleteTraining } from '@/api/hr'
import type { HrTraining } from '@/api/hr'

const trainings = ref<HrTraining[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

async function fetchData() {
  loading.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    const params: Record<string, unknown> = { page: currentPage.value, limit: pageSize.value }
    if (tenantIdStr) params.tenantId = Number(tenantIdStr)

    const response = await queryTrainings(params as any)
    trainings.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    trainings.value = []
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
  courseName: '',
  trainer: '',
  trainingDate: '',
  durationHours: 1,
  attendees: '',
  evaluationSummary: ''
})

const formRules: FormRules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
}

function showCreateDialog() {
  editId.value = ''
  form.courseName = ''
  form.trainer = ''
  form.trainingDate = ''
  form.durationHours = 1
  form.attendees = ''
  form.evaluationSummary = ''
  dialogVisible.value = true
}

async function showEditDialog(row: HrTraining) {
  editId.value = row.id
  try {
    const response = await getTraining(row.id)
    const data = response.data
    form.courseName = data.courseName || ''
    form.trainer = data.trainer || ''
    form.trainingDate = data.trainingDate || ''
    form.durationHours = data.durationHours || 1
    form.attendees = data.attendees || ''
    form.evaluationSummary = data.evaluationSummary || ''
    dialogVisible.value = true
  } catch {
    ElMessage.error('获取培训信息失败')
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
    const payload = { ...form, tenantId: Number(tenantIdStr) }

    if (isEdit.value) {
      await updateTraining(editId.value, payload)
      ElMessage.success('保存成功')
    } else {
      await createTraining(payload)
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
    await deleteTraining(id)
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
.training-list {
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
