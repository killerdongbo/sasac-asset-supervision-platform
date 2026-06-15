<template>
  <el-dialog :model-value="visible" title="资产标签预览与打印" width="700px" @close="handleClose">
    <div v-loading="loading" class="label-preview-container">
      <div v-for="label in labels" :key="label.assetId" class="label-card" ref="labelCards">
        <div class="label-header">
          <span class="label-org">{{ label.orgName || '湛江国资' }}</span>
          <span class="label-title">固定资产标签</span>
        </div>
        <div class="label-body">
          <div class="label-info">
            <div class="info-row"><span class="info-label">编码：</span><span>{{ label.assetCode }}</span></div>
            <div class="info-row"><span class="info-label">名称：</span><span>{{ label.assetName }}</span></div>
            <div class="info-row"><span class="info-label">分类：</span><span>{{ label.category }}</span></div>
            <div class="info-row"><span class="info-label">位置：</span><span>{{ label.location || '-' }}</span></div>
            <div class="info-row"><span class="info-label">保管人：</span><span>{{ label.custodian || '-' }}</span></div>
          </div>
          <div class="label-qr">
            <canvas :ref="el => setQrCanvas(el, label)" width="100" height="100"></canvas>
          </div>
        </div>
        <div class="label-barcode">
          <svg :ref="el => setBarcodeEl(el, label)"></svg>
        </div>
      </div>
      <el-empty v-if="!loading && labels.length === 0" description="无标签数据" />
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handlePrint" :disabled="labels.length === 0">
        确认打印
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { batchGetLabelData, markLabelsPrinted } from '@/api/label'
import type { LabelData } from '@/api/label'
import { ElMessage } from 'element-plus'

/* eslint-disable @typescript-eslint/no-explicit-any */
declare global {
  interface Window {
    QRCode: any
    JsBarcode: any
  }
}
/* eslint-enable @typescript-eslint/no-explicit-any */

const props = defineProps<{ visible: boolean; assetIds: string[] }>()
const emit = defineEmits<{ (e: 'update:visible', val: boolean): void; (e: 'printed'): void }>()

const loading = ref(false)
const labels = ref<LabelData[]>([])

watch(() => props.visible, async (val) => {
  if (val && props.assetIds.length > 0) {
    loading.value = true
    try {
      const res = await batchGetLabelData(props.assetIds)
      labels.value = res.data || []
      await nextTick()
      renderCodes()
    } catch {
      labels.value = []
    } finally {
      loading.value = false
    }
  }
})

function setQrCanvas(el: any, label: LabelData) {
  if (!el) return
  try {
    // @ts-ignore - QRCode loaded globally or via dynamic import
    if (window.QRCode) {
      window.QRCode.toCanvas(el, label.qrContent, { width: 100, margin: 1 })
    }
  } catch { /* QRCode library not loaded */ }
}

function setBarcodeEl(el: any, label: LabelData) {
  if (!el) return
  try {
    // @ts-ignore - JsBarcode loaded globally or via dynamic import
    if (window.JsBarcode) {
      window.JsBarcode(el, label.barcode, { format: 'CODE128', width: 1.5, height: 40, displayValue: true, fontSize: 12 })
    }
  } catch { /* JsBarcode library not loaded */ }
}

function renderCodes() {
  // Re-render triggered by nextTick after data load
}

async function handlePrint() {
  try {
    await markLabelsPrinted(props.assetIds)
    ElMessage.success('标签已标记为已打印')
    window.print()
    emit('printed')
    handleClose()
  } catch {
    ElMessage.error('标记打印状态失败')
  }
}

function handleClose() {
  emit('update:visible', false)
  labels.value = []
}
</script>

<style scoped>
.label-preview-container {
  max-height: 500px;
  overflow-y: auto;
}
.label-card {
  border: 2px solid #333;
  padding: 16px;
  margin-bottom: 16px;
  border-radius: 4px;
  page-break-inside: avoid;
}
.label-header {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #ddd;
  padding-bottom: 8px;
  margin-bottom: 12px;
}
.label-org { font-weight: 600; }
.label-title { font-size: 14px; color: #666; }
.label-body {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}
.label-info { flex: 1; }
.info-row { margin-bottom: 6px; font-size: 13px; }
.info-label { color: #666; min-width: 56px; display: inline-block; }
.label-qr { flex-shrink: 0; }
.label-barcode {
  margin-top: 12px;
  text-align: center;
}
@media print {
  .label-card { border: 1px solid #000; margin: 0 0 8mm 0; }
}
</style>
