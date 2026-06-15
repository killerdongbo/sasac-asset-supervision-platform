<template>
  <div class="quotation-compare">
    <div class="page-header">
      <h2>报价对比 - {{ inquiry?.title }}</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-row :gutter="16" class="info-row">
      <el-col :span="6"><el-statistic title="品类" :value="inquiry?.category || '-'" /></el-col>
      <el-col :span="6"><el-statistic title="规格" :value="inquiry?.specification || '-'" /></el-col>
      <el-col :span="6"><el-statistic title="需求数量" :value="inquiry?.quantity || 0" /></el-col>
      <el-col :span="6"><el-statistic title="预算" :value="inquiry?.budgetAmount ? `¥${Number(inquiry.budgetAmount).toLocaleString()}` : '未设置'" /></el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>供应商报价列表</span>
          <el-button type="primary" size="small" @click="showQuoteDialog = true" :disabled="inquiry?.status !== 'OPEN'">新增报价</el-button>
        </div>
      </template>

      <el-table :data="quotations" stripe v-loading="loading">
        <el-table-column prop="supplierName" label="供应商" width="180" />
        <el-table-column prop="unitPrice" label="单价" width="120" align="right">
          <template #default="{ row }">¥{{ Number(row.unitPrice).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="140" align="right">
          <template #default="{ row }">¥{{ Number(row.totalPrice).toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="taxRate" label="税率" width="80" align="right">
          <template #default="{ row }">{{ row.taxRate ? `${row.taxRate}%` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="deliveryDays" label="交货天数" width="100" align="center" />
        <el-table-column prop="warrantyMonths" label="质保(月)" width="100" align="center" />
        <el-table-column prop="isSelected" label="中选" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isSelected === 1" type="success">中选</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="doSelect(row)" :disabled="row.isSelected === 1">选定</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 16px" v-if="quotations.length > 1">
      <template #header>报价对比图</template>
      <div ref="chartRef" style="height: 300px"></div>
    </el-card>

    <el-dialog v-model="showQuoteDialog" title="新增报价" width="500px">
      <el-form :model="quoteForm" label-width="100px">
        <el-form-item label="供应商名称">
          <el-input v-model="quoteForm.supplierName" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="quoteForm.unitPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="税率(%)">
          <el-input-number v-model="quoteForm.taxRate" :min="0" :max="100" :precision="1" />
        </el-form-item>
        <el-form-item label="交货天数">
          <el-input-number v-model="quoteForm.deliveryDays" :min="1" />
        </el-form-item>
        <el-form-item label="质保(月)">
          <el-input-number v-model="quoteForm.warrantyMonths" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showQuoteDialog = false">取消</el-button>
        <el-button type="primary" @click="doSubmitQuote">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getInquiry, listQuotations, submitQuotation, selectQuotation } from '@/api/quotation'
import type { Inquiry, Quotation } from '@/api/quotation'

const route = useRoute()
const inquiryId = route.params.id as string
const loading = ref(false)
const inquiry = ref<Inquiry | null>(null)
const quotations = ref<Quotation[]>([])
const showQuoteDialog = ref(false)
const chartRef = ref<HTMLElement>()

const quoteForm = ref({ supplierName: '', unitPrice: 0, taxRate: 13, deliveryDays: 7, warrantyMonths: 12 })

async function loadData() {
  loading.value = true
  try {
    const [inqRes, quoteRes] = await Promise.all([getInquiry(inquiryId), listQuotations(inquiryId)])
    inquiry.value = inqRes.data
    quotations.value = quoteRes.data || []
    await nextTick()
    renderChart()
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!chartRef.value || quotations.value.length < 2) return
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['单价', '交货天数'] },
    xAxis: { type: 'category', data: quotations.value.map(q => q.supplierName) },
    yAxis: [
      { type: 'value', name: '单价(元)', position: 'left' },
      { type: 'value', name: '交货天数', position: 'right' }
    ],
    series: [
      { name: '单价', type: 'bar', data: quotations.value.map(q => q.unitPrice), itemStyle: { color: '#409EFF' } },
      { name: '交货天数', type: 'line', yAxisIndex: 1, data: quotations.value.map(q => q.deliveryDays), itemStyle: { color: '#E6A23C' } }
    ]
  })
}

async function doSubmitQuote() {
  await submitQuotation({ ...quoteForm.value, inquiryId, supplierId: Date.now() })
  ElMessage.success('报价已提交')
  showQuoteDialog.value = false
  quoteForm.value = { supplierName: '', unitPrice: 0, taxRate: 13, deliveryDays: 7, warrantyMonths: 12 }
  loadData()
}

async function doSelect(row: Quotation) {
  await selectQuotation(row.id)
  ElMessage.success('已选定')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.info-row { margin-bottom: 8px; }
</style>
