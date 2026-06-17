<template>
  <div class="pr-registration-list">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="企业名称/登记编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="登记类型">
          <el-select v-model="queryForm.regType" placeholder="全部类型" clearable style="width: 140px">
            <el-option label="全部类型" value="" />
            <el-option v-for="(label, key) in regTypeMap" :key="key" :label="label" :value="key" />
          </el-select>
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
      <el-button type="primary" @click="router.push('/property/registrations/create')">新增登记</el-button>
    </div>

    <el-table :data="registrations" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="regNo" label="登记编号" width="180" />
      <el-table-column prop="enterpriseName" label="企业名称" min-width="180" />
      <el-table-column prop="regType" label="登记类型" width="120">
        <template #default="{ row }">
          <el-tag :type="regTypeTag(row.regType)">{{ regTypeMap[row.regType] || row.regType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="equityPct" label="股权比例(%)" width="120" />
      <el-table-column prop="registrationDate" label="登记日期" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="router.push(`/property/registrations/${row.id}`)">详情</el-button>
          <el-button link type="primary" size="small" @click="handleChange(row)">变动</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryRegistrations, recordChange } from '@/api/property'
import type { PrRegistration } from '@/api/property'

const router = useRouter()

const queryForm = reactive({
  keyword: '',
  regType: '',
  status: ''
})

const registrations = ref<PrRegistration[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const regTypeMap: Record<string, string> = {
  POSSESSION: '占有登记',
  CHANGE: '变动登记',
  CANCEL: '注销登记'
}

const statusMap: Record<string, string> = {
  ACTIVE: '有效',
  INACTIVE: '无效'
}

function regTypeTag(type: string): string {
  const map: Record<string, string> = {
    POSSESSION: 'primary',
    CHANGE: 'warning',
    CANCEL: 'danger'
  }
  return map[type] || 'info'
}

function getTenantId(): number {
  return Number(localStorage.getItem('tenantId')) || 0
}

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      tenantId: getTenantId(),
      page: currentPage.value,
      limit: pageSize.value
    }
    if (queryForm.keyword) params.keyword = queryForm.keyword
    if (queryForm.regType) params.regType = queryForm.regType
    if (queryForm.status) params.status = queryForm.status

    const response = await queryRegistrations(params as any)
    registrations.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    registrations.value = []
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
  queryForm.regType = ''
  queryForm.status = ''
  currentPage.value = 1
  fetchData()
}

async function handleChange(row: PrRegistration) {
  try {
    const { value } = await ElMessageBox.prompt('请输入变动原因', '产权变动', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '变动原因不能为空'
    })
    await recordChange(row.id, {
      tenantId: getTenantId(),
      changeType: 'CHANGE',
      changeReason: value
    })
    ElMessage.success('变动记录已创建')
    fetchData()
  } catch {
    // cancelled or error
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.pr-registration-list {
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
