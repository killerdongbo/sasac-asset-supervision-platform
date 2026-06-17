<template>
  <div class="decision-item-list">
    <div class="page-header">
      <h2>三重一大事项</h2>
      <el-button type="primary" @click="$router.push({ name: 'DecisionItemCreate' })">
        新增事项
      </el-button>
    </div>

    <!-- Search / Filter -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="事项类型">
          <el-select v-model="query.itemType" placeholder="全部类型" clearable style="width: 180px">
            <el-option label="重大决策" value="重大决策" />
            <el-option label="重要人事任免" value="重要人事任免" />
            <el-option label="重大项目安排" value="重大项目安排" />
            <el-option label="大额资金使用" value="大额资金使用" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="待审批" value="PENDING" />
            <el-option label="已批准" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table -->
    <el-card shadow="never">
      <el-table :data="items" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="itemNo" label="事项编号" width="180" />
        <el-table-column prop="itemType" label="事项类型" width="140">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.itemType)" size="small">{{ row.itemType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="proposerName" label="提议人" width="120" />
        <el-table-column prop="department" label="部门" width="140" />
        <el-table-column prop="amount" label="金额" width="140">
          <template #default="{ row }">
            {{ row.amount ? '¥' + Number(row.amount).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="urgency" label="紧急程度" width="120">
          <template #default="{ row }">
            <el-tag :type="urgencyTag(row.urgency)" size="small">
              {{ urgencyLabel(row.urgency) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.limit"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { queryDecisionItems, type DecisionItem } from '@/api/decision'

const loading = ref(false)
const items = ref<DecisionItem[]>([])
const total = ref(0)
const query = ref({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  itemType: undefined as string | undefined,
  status: undefined as string | undefined,
  page: 1,
  limit: 10,
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await queryDecisionItems(query.value)
    items.value = res.data
    if (res.meta) total.value = res.meta.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.value.itemType = undefined
  query.value.status = undefined
  query.value.page = 1
  fetchData()
}

const typeTag = (type: string) => {
  const map: Record<string, string> = { 重大决策: 'danger', 重要人事任免: 'warning', 重大项目安排: 'primary', 大额资金使用: 'success' }
  return map[type] || 'info'
}

const urgencyTag = (u: string) => {
  const map: Record<string, string> = { CRITICAL: 'danger', URGENT: 'warning', NORMAL: 'info' }
  return map[u] || 'info'
}

const urgencyLabel = (u: string) => {
  const map: Record<string, string> = { CRITICAL: '紧急', URGENT: '加急', NORMAL: '普通' }
  return map[u] || u
}

const statusTag = (s: string) => {
  const map: Record<string, string> = { DRAFT: 'info', PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[s] || 'info'
}

const statusLabel = (s: string) => {
  const map: Record<string, string> = { DRAFT: '草稿', PENDING: '待审批', APPROVED: '已批准', REJECTED: '已驳回' }
  return map[s] || s
}

onMounted(fetchData)
</script>

<style scoped>
.decision-item-list {
  padding: 16px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
.search-card {
  margin-bottom: 16px;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
