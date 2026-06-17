<template>
  <div class="contract-list">
    <el-alert
      v-if="expiringCount > 0"
      :title="`有 ${expiringCount} 份合同即将到期，请及时处理`"
      type="warning"
      show-icon
      :closable="false"
      style="margin-bottom: 16px"
    />

    <div class="toolbar">
      <el-button type="primary" @click="router.push('/hr/contracts/create')">新增合同</el-button>
    </div>

    <el-table :data="contracts" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="contractNo" label="合同编号" width="160" />
      <el-table-column prop="contractType" label="合同类型" width="140">
        <template #default="{ row }">
          {{ typeMap[row.contractType] || row.contractType }}
        </template>
      </el-table-column>
      <el-table-column prop="startDate" label="开始日期" width="120" />
      <el-table-column prop="endDate" label="结束日期" width="120">
        <template #default="{ row }">
          <span :class="{ 'expiring-text': !row.isUnlimited && row.endDate && isExpiring(row.endDate) }">
            {{ row.isUnlimited ? '--' : row.endDate }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="router.push(`/hr/contracts/${row.id}/edit`)">编辑</el-button>
          <el-popconfirm title="确定删除该合同吗？" @confirm="handleDelete(row.id)">
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { queryContracts, deleteContract, getExpiringContracts } from '@/api/hr'
import type { HrContract } from '@/api/hr'

const router = useRouter()

const contracts = ref<HrContract[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const expiringCount = ref(0)

const typeMap: Record<string, string> = {
  LABOR: '劳动合同',
  INTERNSHIP: '实习合同',
  PROJECT: '项目合同'
}

const statusMap: Record<string, string> = {
  ACTIVE: '生效中',
  EXPIRED: '已过期',
  TERMINATED: '已终止',
  RENEWED: '已续签'
}

function statusType(status: string): string {
  const map: Record<string, string> = {
    ACTIVE: 'success',
    EXPIRED: 'info',
    TERMINATED: 'danger',
    RENEWED: 'warning'
  }
  return map[status] || 'info'
}

function isExpiring(endDate: string): boolean {
  if (!endDate) return false
  const now = new Date()
  const end = new Date(endDate)
  const diff = end.getTime() - now.getTime()
  return diff >= 0 && diff <= 30 * 24 * 60 * 60 * 1000
}

async function fetchExpiring() {
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    if (!tenantIdStr) return
    const response = await getExpiringContracts(Number(tenantIdStr), 30)
    expiringCount.value = (response.data || []).length
  } catch {
    expiringCount.value = 0
  }
}

async function fetchData() {
  loading.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    const params: Record<string, unknown> = {
      page: currentPage.value,
      limit: pageSize.value
    }
    if (tenantIdStr) params.tenantId = Number(tenantIdStr)

    const response = await queryContracts(params as any)
    contracts.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    contracts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function handleDelete(id: string) {
  try {
    await deleteContract(id)
    ElMessage.success('删除成功')
    fetchData()
    fetchExpiring()
  } catch {
    // Error handled by interceptor
  }
}

onMounted(() => {
  fetchData()
  fetchExpiring()
})
</script>

<style scoped>
.contract-list {
  padding: 0;
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.expiring-text {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
