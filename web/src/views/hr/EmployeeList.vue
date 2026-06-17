<template>
  <div class="employee-list">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="姓名/编号/手机号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="全部状态" value="" />
            <el-option v-for="(label, key) in statusMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="toolbar">
      <el-button type="primary" @click="router.push('/hr/employees/create')">新增人员</el-button>
    </div>

    <el-table :data="employees" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="employeeNo" label="员工编号" width="140" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="gender" label="性别" width="80" :formatter="formatGender" />
      <el-table-column prop="position" label="职位" min-width="140" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="entryDate" label="入职日期" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="router.push(`/hr/employees/${row.id}`)">详情</el-button>
          <el-button link type="primary" size="small" @click="router.push(`/hr/employees/${row.id}/edit`)">编辑</el-button>
          <el-popconfirm title="确定删除该人员吗？" @confirm="handleDelete(row.id)">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { queryEmployees, deleteEmployee } from '@/api/hr'
import type { HrEmployee } from '@/api/hr'

const router = useRouter()

const queryForm = reactive({
  keyword: '',
  status: ''
})

const employees = ref<HrEmployee[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const statusMap: Record<string, string> = {
  ACTIVE: '在职',
  RESIGNED: '离职',
  SUSPENDED: '停职'
}

const genderMap: Record<string, string> = {
  MALE: '男',
  FEMALE: '女'
}

function formatGender(_row: unknown, _column: unknown, value: string): string {
  return genderMap[value] || value
}

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      page: currentPage.value,
      limit: pageSize.value
    }
    if (queryForm.keyword) params.keyword = queryForm.keyword
    if (queryForm.status) params.status = queryForm.status

    const response = await queryEmployees(params as any)
    employees.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    employees.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchData()
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.status = ''
  currentPage.value = 1
  fetchData()
}

async function handleDelete(id: string) {
  try {
    await deleteEmployee(id)
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
.employee-list {
  padding: 0;
}

.search-card {
  margin-bottom: 16px;
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
