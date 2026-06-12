<template>
  <div class="asset-import">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
    </div>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">Excel导入资产</span>
          <el-button type="success" @click="downloadTemplate">
            <el-icon><Download /></el-icon>
            下载导入模板
          </el-button>
        </div>
      </template>

      <el-upload
        ref="uploadRef"
        drag
        :auto-upload="false"
        :accept="acceptTypes"
        :limit="1"
        :on-change="handleFileChange"
        :on-exceed="handleExceed"
      >
        <el-icon class="upload-icon" :size="48"><UploadFilled /></el-icon>
        <div class="upload-text">
          <span>将Excel文件拖拽到此处，或<em>点击选择文件</em></span>
        </div>
        <template #tip>
          <div class="upload-tip">
            支持 .xlsx、.xls 格式的Excel文件
          </div>
        </template>
      </el-upload>

      <div class="submit-wrapper" v-if="selectedFile">
        <el-button type="primary" @click="handleImport" :loading="uploading">开始导入</el-button>
        <span class="file-name">已选择：{{ selectedFile.name }}</span>
      </div>

      <el-card v-if="result" class="result-card" shadow="never">
        <template #header>
          <span class="card-title">导入结果</span>
        </template>
        <div class="result-stats">
          <div class="stat-item">
            <span class="stat-label">总数</span>
            <span class="stat-value">{{ result.totalCount }}</span>
          </div>
          <div class="stat-item success">
            <span class="stat-label">成功</span>
            <span class="stat-value">{{ result.successCount }}</span>
          </div>
          <div class="stat-item failure">
            <span class="stat-label">失败</span>
            <span class="stat-value">{{ result.failedList?.length || 0 }}</span>
          </div>
        </div>
        <el-table v-if="result.failedList?.length" :data="result.failedList" border stripe size="small">
          <el-table-column prop="row" label="行号" width="80" />
          <el-table-column prop="error" label="失败原因" min-width="200" />
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled, Download } from '@element-plus/icons-vue'
import { importAssets } from '@/api/asset'
import type { ImportResult } from '@/api/asset'
const router = useRouter()

const acceptTypes = '.xlsx,.xls'
const selectedFile = ref<File | null>(null)
const uploading = ref(false)

const result = ref<ImportResult | null>(null)

function handleFileChange(file: { raw: File }) {
  selectedFile.value = file.raw
}

function handleExceed() {
  ElMessage.warning('每次只能上传一个文件')
}

function downloadTemplate() {
  const link = document.createElement('a')
  link.href = 'http://localhost:8082/api/assets/template'
  link.download = '资产导入模板.xlsx'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  ElMessage.success('模板下载中...')
}

async function handleImport() {
  if (!selectedFile.value) return

  uploading.value = true
  result.value = null

  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    const orgIdStr = localStorage.getItem('orgId')

    if (tenantIdStr === null || orgIdStr === null) {
      ElMessage.error('缺少组织信息，无法导入')
      uploading.value = false
      return
    }

    const tenantId = Number(tenantIdStr)
    const orgId = Number(orgIdStr)

    const response = await importAssets(selectedFile.value, orgId, tenantId)
    result.value = response.data ?? null
    ElMessage.success('导入完成')
  } catch {
    // Error handled by interceptor
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.asset-import {
  padding: 0;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.upload-icon {
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: #606266;
}

.upload-text em {
  color: #409eff;
  font-style: normal;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.submit-wrapper {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-name {
  font-size: 14px;
  color: #606266;
}

.result-card {
  margin-top: 24px;
}

.result-stats {
  display: flex;
  gap: 32px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-item.success .stat-value {
  color: #67c23a;
}

.stat-item.failure .stat-value {
  color: #f56c6c;
}
</style>
