<template>
  <div class="employee-form">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
    </div>

    <el-card shadow="never">
      <template #header>
        <span class="card-title">{{ isEdit ? '编辑人员' : '新增人员' }}</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 960px"
      >
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="员工编号" prop="employeeNo">
              <el-input v-model="form.employeeNo" placeholder="请输入员工编号" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别" clearable style="width: 100%">
                <el-option label="男" value="MALE" />
                <el-option label="女" value="FEMALE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker
                v-model="form.entryDate"
                type="date"
                placeholder="选择入职日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="职称" prop="title">
              <el-input v-model="form.title" placeholder="请输入职称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学历" prop="education">
              <el-select v-model="form.education" placeholder="请选择学历" clearable style="width: 100%">
                <el-option label="博士" value="DOCTOR" />
                <el-option label="硕士" value="MASTER" />
                <el-option label="本科" value="BACHELOR" />
                <el-option label="大专" value="ASSOCIATE" />
                <el-option label="高中" value="HIGH_SCHOOL" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '创建人员' }}
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
import { getEmployee, createEmployee, updateEmployee } from '@/api/hr'
import type { EmployeeCreateDTO } from '@/api/hr'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref<FormInstance>()
const submitting = ref(false)

interface EmployeeFormData {
  employeeNo: string
  name: string
  gender: string
  phone: string
  email: string
  entryDate: string
  position: string
  title: string
  education: string
}

const form = reactive<EmployeeFormData>({
  employeeNo: '',
  name: '',
  gender: '',
  phone: '',
  email: '',
  entryDate: '',
  position: '',
  title: '',
  education: ''
})

const rules: FormRules = {
  employeeNo: [{ required: true, message: '请输入员工编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

async function fetchEmployee() {
  const id = route.params.id as string
  if (!id) return

  try {
    const response = await getEmployee(id)
    const data = response.data
    if (data) {
      form.employeeNo = data.employeeNo || ''
      form.name = data.name || ''
      form.gender = data.gender || ''
      form.phone = data.phone || ''
      form.email = data.email || ''
      form.entryDate = data.entryDate || ''
      form.position = data.position || ''
      form.title = data.title || ''
      form.education = data.education || ''
    }
  } catch {
    ElMessage.error('获取人员信息失败')
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
      await updateEmployee(id, { ...form } as any)
      ElMessage.success('保存成功')
    } else {
      await createEmployee({
        ...form,
        tenantId,
        orgId
      } as EmployeeCreateDTO)
      ElMessage.success('创建成功')
    }
    router.push('/hr/employees')
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    fetchEmployee()
  }
})
</script>

<style scoped>
.employee-form {
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
