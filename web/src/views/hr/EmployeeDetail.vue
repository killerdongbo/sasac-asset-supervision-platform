<template>
  <div class="employee-detail">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
      <el-button type="primary" @click="router.push(`/hr/employees/${route.params.id}/edit`)">编辑</el-button>
    </div>

    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span class="card-title">人员详情</span>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="员工编号">{{ employee.employeeNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ employee.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ formatGender(employee.gender) }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ employee.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ employee.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ employee.entryDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{ employee.position || '-' }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ employee.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学历">{{ formatEducation(employee.education) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="employee.status === 'ACTIVE' ? 'success' : 'info'">
            {{ statusMap[employee.status || ''] || employee.status || '-' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用工类型">{{ formatEmploymentType(employee.employmentType) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEmployee } from '@/api/hr'
import type { HrEmployee } from '@/api/hr'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const employee = ref<Partial<HrEmployee>>({})

const statusMap: Record<string, string> = {
  ACTIVE: '在职',
  RESIGNED: '离职',
  SUSPENDED: '停职'
}

const genderMap: Record<string, string> = {
  MALE: '男',
  FEMALE: '女'
}

const educationMap: Record<string, string> = {
  DOCTOR: '博士',
  MASTER: '硕士',
  BACHELOR: '本科',
  ASSOCIATE: '大专',
  HIGH_SCHOOL: '高中'
}

const employmentTypeMap: Record<string, string> = {
  FULL_TIME: '全职',
  PART_TIME: '兼职',
  CONTRACT: '合同',
  INTERNSHIP: '实习'
}

function formatGender(value?: string): string {
  return value ? genderMap[value] || value : '-'
}

function formatEducation(value?: string): string {
  return value ? educationMap[value] || value : '-'
}

function formatEmploymentType(value?: string): string {
  return value ? employmentTypeMap[value] || value : '-'
}

async function fetchEmployee() {
  const id = route.params.id as string
  if (!id) return

  loading.value = true
  try {
    const response = await getEmployee(id)
    employee.value = response.data || {}
  } catch {
    ElMessage.error('获取人员信息失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchEmployee()
})
</script>

<style scoped>
.employee-detail {
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
