<template>
  <div class="asset-list">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="资产名称/编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="资产类别">
          <el-select v-model="queryForm.category" placeholder="全部类别" clearable style="width: 160px">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="使用状态">
          <el-select v-model="queryForm.useStatus" placeholder="全部状态" clearable style="width: 140px">
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
      <el-button type="primary" @click="router.push('/assets/create')">新增资产</el-button>
      <el-button @click="router.push('/assets/import')">Excel导入</el-button>
    </div>

    <el-table :data="assets" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="assetCode" label="资产编号" width="160" />
      <el-table-column prop="name" label="资产名称" min-width="180" />
      <el-table-column prop="category" label="资产类别" width="120" :formatter="formatCategory" />
      <el-table-column prop="originalValue" label="原值" width="140" align="right" :formatter="formatCurrency" />
      <el-table-column prop="currentValue" label="净值" width="140" align="right" :formatter="formatCurrency" />
      <el-table-column prop="accumulatedDepreciation" label="累计折旧" width="140" align="right" :formatter="formatCurrency" />
      <el-table-column prop="useStatus" label="使用状态" width="110" :formatter="formatStatus" />
      <el-table-column prop="location" label="所在地" min-width="140" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="router.push(`/assets/${row.id}`)">查看</el-button>
          <el-button link type="primary" size="small" @click="router.push(`/assets/${row.id}/edit`)">编辑</el-button>
          <el-popconfirm title="确定删除该资产吗？" @confirm="handleDelete(row.id)">
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
        @size-change="fetchAssets"
        @current-change="fetchAssets"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { queryAssets, deleteAsset } from '@/api/asset'
import type { Asset } from '@/api/asset'

const router = useRouter()

const queryForm = reactive({
  keyword: '',
  category: '',
  useStatus: ''
})

const assets = ref<Asset[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const categoryOptions = [
  { value: '', label: '全部类别' },
  { value: 'LAND', label: '土地' },
  { value: 'REAL_ESTATE', label: '房产' },
  { value: 'EQUIPMENT', label: '设备' },
  { value: 'VEHICLE', label: '车辆' },
  { value: 'FURNITURE', label: '家具' },
  { value: 'IT_EQUIPMENT', label: 'IT设备' }
]

const statusMap: Record<string, string> = {
  IN_USE: '使用中',
  IDLE: '闲置',
  RENTED: '已出租',
  MORTGAGED: '已抵押',
  DISPOSED: '已处置'
}

const categoryMap: Record<string, string> = {
  LAND: '土地',
  REAL_ESTATE: '房产',
  EQUIPMENT: '设备',
  VEHICLE: '车辆',
  FURNITURE: '家具',
  IT_EQUIPMENT: 'IT设备'
}

function formatCurrency(_row: unknown, _column: unknown, value: number): string {
  if (value == null) return '-'
  return value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY' })
}

function formatCategory(_row: unknown, _column: unknown, value: string): string {
  return categoryMap[value] || value
}

function formatStatus(_row: unknown, _column: unknown, value: string): string {
  return statusMap[value] || value
}

async function fetchAssets() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      page: currentPage.value,
      limit: pageSize.value
    }
    if (queryForm.keyword) params.keyword = queryForm.keyword
    if (queryForm.category) params.category = queryForm.category
    if (queryForm.useStatus) params.useStatus = queryForm.useStatus

    const response = await queryAssets(params)
    assets.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    assets.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchAssets()
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.category = ''
  queryForm.useStatus = ''
  currentPage.value = 1
  fetchAssets()
}

async function handleDelete(id: string) {
  try {
    await deleteAsset(id)
    ElMessage.success('删除成功')
    fetchAssets()
  } catch {
    // Error handled by interceptor
  }
}

onMounted(() => {
  fetchAssets()
})
</script>

<style scoped>
.asset-list {
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
