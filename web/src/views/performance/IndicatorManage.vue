<template>
  <div class="indicator-manage">
    <div class="page-header">
      <h2>考核指标配置</h2>
      <div class="header-actions">
        <el-select v-model="queryYear" placeholder="选择年份" style="width: 140px; margin-right: 8px;">
          <el-option v-for="y in yearOptions" :key="y" :label="y" :value="y" />
        </el-select>
        <el-button type="primary" @click="handleBatchSave" :loading="saving">批量保存</el-button>
        <el-button type="success" @click="handleCalculate" :loading="calculating">计算得分</el-button>
        <el-button @click="addRow">添加指标</el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-alert title="权重之和必须为100%" :type="weightValid ? 'success' : 'warning'" :closable="false" show-icon style="margin-bottom: 16px;">
        <template #title>
          权重之和：{{ totalWeight }}% &nbsp;
          <span v-if="weightValid" style="color: #67c23a;">(验证通过)</span>
          <span v-else style="color: #e6a23c;">(请调整权重)</span>
        </template>
      </el-alert>

      <el-table :data="indicators" v-loading="loading" stripe style="width: 100%" max-height="600">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="indicatorCode" label="指标编码" width="150">
          <template #default="{ row, $index }">
            <el-input v-model="row.indicatorCode" size="small" placeholder="请输入编码" />
          </template>
        </el-table-column>
        <el-table-column prop="indicatorName" label="指标名称" min-width="160">
          <template #default="{ row }">
            <el-input v-model="row.indicatorName" size="small" placeholder="请输入名称" />
          </template>
        </el-table-column>
        <el-table-column prop="category" label="指标类别" width="140">
          <template #default="{ row }">
            <el-select v-model="row.category" size="small" style="width: 100%;">
              <el-option label="财务类" value="财务类" />
              <el-option label="运营类" value="运营类" />
              <el-option label="客户类" value="客户类" />
              <el-option label="成长类" value="成长类" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="权重(%)" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.weight" :min="0" :max="100" :precision="2" size="small" style="width: 100px;" />
          </template>
        </el-table-column>
        <el-table-column prop="targetValue" label="目标值" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.targetValue" :min="0" :precision="4" size="small" style="width: 120px;" />
          </template>
        </el-table-column>
        <el-table-column prop="actualValue" label="实际值" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.actualValue" :min="0" :precision="4" size="small" style="width: 120px;" />
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <el-tag :type="scoreTag(row.score)" size="small">{{ row.score ?? '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ $index }">
            <el-button link type="danger" size="small" @click="removeRow($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { defineIndicators, calculateScores, getIndicatorSummary, type PerfIndicatorDef, type IndicatorDefDTO } from '@/api/performance'

const loading = ref(false)
const saving = ref(false)
const calculating = ref(false)
const indicators = ref<any[]>([])
const queryYear = ref(new Date().getFullYear())

const yearOptions = computed(() => {
  const year = new Date().getFullYear()
  return [year - 2, year - 1, year, year + 1]
})

const totalWeight = computed(() => {
  const sum = indicators.value.reduce((acc, row) => acc + (Number(row.weight) || 0), 0)
  return sum.toFixed(2)
})

const weightValid = computed(() => {
  const sum = indicators.value.reduce((acc, row) => acc + (Number(row.weight) || 0), 0)
  return Math.abs(sum - 100) < 0.01
})

const scoreTag = (score: number) => {
  if (score == null) return 'info'
  if (score >= 90) return 'success'
  if (score >= 70) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

function addRow() {
  indicators.value.push({
    indicatorCode: '',
    indicatorName: '',
    category: '财务类',
    weight: 0,
    targetValue: 0,
    actualValue: undefined,
    score: undefined,
  })
}

function removeRow(index: number) {
  indicators.value.splice(index, 1)
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getIndicatorSummary({
      tenantId: Number(localStorage.getItem('tenantId') || 1),
      orgId: Number(localStorage.getItem('orgId') || 1),
      year: queryYear.value,
    })
    if (res.data && res.data.length > 0) {
      indicators.value = res.data.map((item: PerfIndicatorDef) => ({
        indicatorCode: item.indicatorCode,
        indicatorName: item.indicatorName,
        category: item.category || '财务类',
        weight: item.weight,
        targetValue: item.targetValue,
        actualValue: item.actualValue,
        score: item.score,
        _id: item.id,
      }))
    } else {
      // Pre-fill default rows if no data
      indicators.value = [
        { indicatorCode: 'FIN-001', indicatorName: '营业收入增长率', category: '财务类', weight: 25, targetValue: 10, actualValue: undefined, score: undefined },
        { indicatorCode: 'FIN-002', indicatorName: '净利润率', category: '财务类', weight: 20, targetValue: 15, actualValue: undefined, score: undefined },
        { indicatorCode: 'OPR-001', indicatorName: '运营效率', category: '运营类', weight: 20, targetValue: 95, actualValue: undefined, score: undefined },
        { indicatorCode: 'CUS-001', indicatorName: '客户满意度', category: '客户类', weight: 20, targetValue: 90, actualValue: undefined, score: undefined },
        { indicatorCode: 'GRW-001', indicatorName: '员工培训完成率', category: '成长类', weight: 15, targetValue: 100, actualValue: undefined, score: undefined },
      ]
    }
  } finally {
    loading.value = false
  }
}

const handleBatchSave = async () => {
  if (!weightValid.value) {
    ElMessage.warning('权重之和必须为100%')
    return
  }
  saving.value = true
  try {
    const dtos: IndicatorDefDTO[] = indicators.value.map((row: any) => ({
      tenantId: Number(localStorage.getItem('tenantId') || 1),
      orgId: Number(localStorage.getItem('orgId') || 1),
      indicatorCode: row.indicatorCode,
      indicatorName: row.indicatorName,
      category: row.category,
      weight: Number(row.weight),
      targetValue: Number(row.targetValue),
      actualValue: row.actualValue != null ? Number(row.actualValue) : undefined,
      cycleYear: queryYear.value,
    }))
    const res = await defineIndicators(dtos)
    indicators.value = res.data.map((item: PerfIndicatorDef) => ({
      indicatorCode: item.indicatorCode,
      indicatorName: item.indicatorName,
      category: item.category || '财务类',
      weight: item.weight,
      targetValue: item.targetValue,
      actualValue: item.actualValue,
      score: item.score,
      _id: item.id,
    }))
    ElMessage.success('指标保存成功')
  } finally {
    saving.value = false
  }
}

const handleCalculate = async () => {
  calculating.value = true
  try {
    const res = await calculateScores({
      tenantId: Number(localStorage.getItem('tenantId') || 1),
      orgId: Number(localStorage.getItem('orgId') || 1),
      year: queryYear.value,
    })
    indicators.value = res.data.map((item: PerfIndicatorDef) => ({
      indicatorCode: item.indicatorCode,
      indicatorName: item.indicatorName,
      category: item.category || '财务类',
      weight: item.weight,
      targetValue: item.targetValue,
      actualValue: item.actualValue,
      score: item.score,
      _id: item.id,
    }))
    ElMessage.success('得分计算完成')
  } finally {
    calculating.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.indicator-manage {
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
</style>
