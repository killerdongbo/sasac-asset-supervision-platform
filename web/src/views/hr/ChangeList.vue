<template>
  <div class="change-list">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
      <h3 style="margin: 0; line-height: 32px">人员变更记录</h3>
    </div>

    <el-table :data="changes" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="changeType" label="变更类型" width="140">
        <template #default="{ row }">
          <el-tag :type="typeTagType(row.changeType)">
            {{ typeMap[row.changeType] || row.changeType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="beforeValue" label="变更前" min-width="140" />
      <el-table-column prop="afterValue" label="变更后" min-width="140" />
      <el-table-column prop="effectiveDate" label="生效日期" width="120" />
      <el-table-column prop="reason" label="变更原因" min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'">
            {{ changeStatusMap[row.status] || row.status }}
          </el-tag>
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
import { useRoute, useRouter } from 'vue-router'
import { queryChanges } from '@/api/hr'
import type { HrEmployeeChange } from '@/api/hr'

const route = useRoute()
const router = useRouter()

const changes = ref<HrEmployeeChange[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const typeMap: Record<string, string> = {
  ENTRY: '入职',
  PROBATION: '转正',
  TRANSFER: '调动',
  RESIGN: '离职',
  PROMOTION: '晋升',
  TITLE_CHANGE: '职称变更',
  SALARY_ADJUSTMENT: '薪资调整'
}

const changeStatusMap: Record<string, string> = {
  PENDING: '待审批',
  APPROVED: '已通过',
  REJECTED: '已驳回'
}

function typeTagType(type: string): string {
  const map: Record<string, string> = {
    ENTRY: 'success',
    PROBATION: 'warning',
    TRANSFER: '',
    RESIGN: 'danger'
  }
  return map[type] || ''
}

async function fetchData() {
  loading.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    const employeeId = route.query.employeeId

    const params: Record<string, unknown> = {
      page: currentPage.value,
      limit: pageSize.value
    }
    if (tenantIdStr) params.tenantId = Number(tenantIdStr)
    if (employeeId) params.employeeId = Number(employeeId)

    const response = await queryChanges(params as any)
    changes.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    changes.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.change-list {
  padding: 0;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
