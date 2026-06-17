<template>
  <div class="indicator-dashboard">
    <div class="page-header">
      <h2>财务指标仪表盘</h2>
      <div class="header-actions">
        <el-date-picker v-model="queryYear" type="year" placeholder="选择年份" value-format="YYYY" style="width: 140px; margin-right: 8px;" />
        <el-select v-model="queryMonth" placeholder="选择月份" style="width: 120px; margin-right: 8px;">
          <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m" />
        </el-select>
        <el-button type="primary" @click="handleCalculate" :loading="calculating">重新计算</el-button>
      </div>
    </div>

    <!-- Gauge Charts -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :lg="6" v-for="indicator in indicators" :key="indicator.indicatorCode">
        <el-card shadow="never" class="gauge-card">
          <div class="gauge-title">{{ indicator.indicatorName }}</div>
          <div class="gauge-value" :style="{ color: statusColor(indicator.status) }">
            {{ formatValue(indicator) }}
          </div>
          <div class="gauge-status">
            <el-tag :type="statusTag(indicator.status)" size="small">
              {{ statusLabel(indicator.status) }}
            </el-tag>
          </div>
          <div class="gauge-thresholds">
            <span>预警线: {{ indicator.thresholdWarn }}</span>
            <span>报警线: {{ indicator.thresholdAlarm }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Indicator Table -->
    <el-card shadow="never" style="margin-top: 16px;">
      <template #header>
        <span>指标明细</span>
      </template>
      <el-table :data="indicatorList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="indicatorName" label="指标名称" width="160" />
        <el-table-column prop="indicatorCode" label="指标编码" width="180" />
        <el-table-column prop="indicatorValue" label="当前值" width="140">
          <template #default="{ row }">
            <span :style="{ color: statusColor(row.status), fontWeight: 'bold' }">
              {{ formatValue(row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="thresholdWarn" label="预警阈值" width="120" />
        <el-table-column prop="thresholdAlarm" label="报警阈值" width="120" />
        <el-table-column prop="periodYear" label="年份" width="80" />
        <el-table-column prop="periodMonth" label="月份" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
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
import { ElMessage } from 'element-plus'
import { queryIndicators, calculateIndicators, type FinIndicator } from '@/api/finance'

const loading = ref(false)
const calculating = ref(false)
const indicators = ref<FinIndicator[]>([])
const indicatorList = ref<FinIndicator[]>([])
const total = ref(0)
const queryYear = ref(new Date().getFullYear())
const queryMonth = ref(new Date().getMonth() + 1)

const query = ref({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  indicatorCode: undefined as string | undefined,
  periodYear: undefined as number | undefined,
  page: 1,
  limit: 10,
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await queryIndicators(query.value)
    indicatorList.value = res.data
    if (res.meta) total.value = res.meta.total
    // Extract the 4 main indicators for the gauge cards
    const codes = ['assetLiabilityRatio', 'currentRatio', 'roe', 'receivableTurnover']
    indicators.value = codes.map(code => res.data.find(i => i.indicatorCode === code)).filter(Boolean) as FinIndicator[]
  } finally {
    loading.value = false
  }
}

const handleCalculate = async () => {
  calculating.value = true
  try {
    const res = await calculateIndicators({
      tenantId: Number(localStorage.getItem('tenantId') || 1),
      orgId: Number(localStorage.getItem('orgId') || 1),
      periodYear: queryYear.value as number,
      periodMonth: queryMonth.value,
    })
    indicators.value = res.data
    ElMessage.success('指标计算完成')
    fetchData()
  } finally {
    calculating.value = false
  }
}

const formatValue = (ind: FinIndicator) => {
  if (ind.indicatorValue === null || ind.indicatorValue === undefined) return '-'
  if (ind.indicatorCode === 'assetLiabilityRatio' || ind.indicatorCode === 'currentRatio') {
    return (ind.indicatorValue * 100).toFixed(2) + '%'
  }
  return ind.indicatorValue.toFixed(4)
}

const statusColor = (status: string) => {
  const map: Record<string, string> = { NORMAL: '#67c23a', WARN: '#e6a23c', ALARM: '#f56c6c' }
  return map[status || 'NORMAL'] || '#909399'
}

const statusTag = (s: string) => {
  const map: Record<string, string> = { NORMAL: 'success', WARN: 'warning', ALARM: 'danger' }
  return map[s] || 'info'
}

const statusLabel = (s: string) => {
  const map: Record<string, string> = { NORMAL: '正常', WARN: '预警', ALARM: '报警' }
  return map[s] || s
}

onMounted(fetchData)
</script>

<style scoped>
.indicator-dashboard {
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
.header-actions {
  display: flex;
  align-items: center;
}
.gauge-card {
  margin-bottom: 16px;
  text-align: center;
}
.gauge-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.gauge-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}
.gauge-status {
  margin-bottom: 8px;
}
.gauge-thresholds {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
