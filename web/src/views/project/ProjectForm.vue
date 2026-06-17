<template>
  <div class="project-form">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
    </div>

    <el-card shadow="never">
      <template #header>
        <span class="card-title">{{ isEdit ? '编辑项目' : '新增项目' }}</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 960px"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="projectType">
              <el-select v-model="form.projectType" placeholder="请选择类型" clearable style="width: 100%">
                <el-option label="工程建设" value="ENGINEERING" />
                <el-option label="信息化" value="IT" />
                <el-option label="技改" value="TECH_UPGRADE" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-input v-model="form.category" placeholder="请输入分类" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预算总额" prop="budgetTotal">
              <el-input-number
                v-model="form.budgetTotal"
                :precision="2"
                :min="0"
                style="width: 100%"
                placeholder="请输入预算总额"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划结束日期" prop="plannedEndDate">
              <el-date-picker
                v-model="form.plannedEndDate"
                type="date"
                placeholder="选择计划结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="负责人" prop="managerName">
              <el-input v-model="form.managerName" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入所属部门" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '创建项目' }}
          </el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getProject, createProject, updateProject } from '@/api/project'
import type { ProjectCreateDTO } from '@/api/project'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref<FormInstance>()
const submitting = ref(false)

interface ProjectFormData {
  name: string
  projectType: string
  category: string
  budgetTotal: number | undefined
  startDate: string
  plannedEndDate: string
  managerName: string
  department: string
  description: string
}

const form = reactive<ProjectFormData>({
  name: '',
  projectType: '',
  category: '',
  budgetTotal: undefined,
  startDate: '',
  plannedEndDate: '',
  managerName: '',
  department: '',
  description: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }]
}

async function fetchProject() {
  const id = route.params.id as string
  if (!id) return

  try {
    const response = await getProject(id)
    const data = response.data
    if (data) {
      form.name = data.name || ''
      form.projectType = data.projectType || ''
      form.category = data.category || ''
      form.budgetTotal = data.budgetTotal ?? undefined
      form.startDate = data.startDate || ''
      form.plannedEndDate = data.plannedEndDate || ''
      form.managerName = data.managerName || ''
      form.department = data.department || ''
      form.description = data.description || ''
    }
  } catch {
    ElMessage.error('获取项目信息失败')
    router.back()
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    const orgIdStr = localStorage.getItem('orgId')

    if (tenantIdStr === null || orgIdStr === null) {
      ElMessage.error('缺少组织信息，无法提交')
      submitting.value = false
      return
    }

    const tenantId = Number(tenantIdStr)
    const orgId = Number(orgIdStr)

    if (isEdit.value) {
      const id = route.params.id as string
      await updateProject(id, { ...form } as any)
      ElMessage.success('保存成功')
    } else {
      await createProject({
        ...form,
        tenantId,
        orgId
      } as ProjectCreateDTO)
      ElMessage.success('创建成功')
    }
    router.push('/projects')
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    fetchProject()
  }
})
</script>

<style scoped>
.project-form {
  padding: 0;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
