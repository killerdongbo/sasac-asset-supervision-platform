<template>
  <div class="label-manage">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span class="card-title">资产标签管理</span>
          <div class="header-actions">
            <el-select v-model="filterStatus" placeholder="标签状态" clearable style="width: 140px;" @change="fetchAssets">
              <el-option label="未打印" value="UNPRINTED" />
              <el-option label="已打印" value="PRINTED" />
              <el-option label="已损坏" value="DAMAGED" />
            </el-select>
            <el-button type="primary" :disabled="!selectedIds.length" @click="handleBatchPrint">
              批量打印 ({{ selectedIds.length }})
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="assets" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column label="资产编码" prop="assetCode" width="160" />
        <el-table-column label="资产名称" prop="name" min-width="180" />
        <el-table-column label="分类" prop="category" width="120" />
        <el-table-column label="存放位置" prop="location" width="140" />
        <el-table-column label="标签状态" prop="labelStatus" width="100">
          <template #default="{ row }">
            <el-tag :type="labelStatusType(row.labelStatus)" size="small">
              {{ labelStatusText(row.labelStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="打印次数" prop="printCount" width="90" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handlePreview(row)">预览</el-button>
            <el-button type="success" link size="small" @click="handlePrint([row.id])">打印</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > pageSize"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
        style="margin-top: 16px; justify-content: center;"
      />
    </el-card>

    <LabelPrintDialog
      v-model:visible="printDialogVisible"
      :asset-ids="printAssetIds"
      @printed="onPrinted"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listAssetsByLabelStatus } from '@/api/label'
import LabelPrintDialog from './LabelPrintDialog.vue'

const assets = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = 20
const filterStatus = ref<string>('')
const selectedIds = ref<string[]>([])
const printDialogVisible = ref(false)
const printAssetIds = ref<string[]>([])

function labelStatusType(status: string): string {
  const map: Record<string, string> = { UNPRINTED: 'info', PRINTED: 'success', DAMAGED: 'danger' }
  return map[status] || 'info'
}

function labelStatusText(status: string): string {
  const map: Record<string, string> = { UNPRINTED: '未打印', PRINTED: '已打印', DAMAGED: '已损坏' }
  return map[status] || status
}

function handleSelectionChange(rows: any[]) {
  selectedIds.value = rows.map(r => r.id)
}

async function fetchAssets() {
  loading.value = true
  try {
    const res = await listAssetsByLabelStatus(filterStatus.value || undefined, currentPage.value, pageSize)
    assets.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    assets.value = []
  } finally {
    loading.value = false
  }
}

function handlePreview(row: any) {
  printAssetIds.value = [row.id]
  printDialogVisible.value = true
}

function handlePrint(ids: string[]) {
  printAssetIds.value = ids
  printDialogVisible.value = true
}

function handleBatchPrint() {
  handlePrint(selectedIds.value)
}

function onPrinted() {
  fetchAssets()
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchAssets()
}

onMounted(() => fetchAssets())
</script>

<style scoped>
.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
}
.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>
