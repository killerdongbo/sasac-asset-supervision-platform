<template>
  <div class="asset-detail">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
      <el-button type="primary" @click="router.push(`/assets/${asset?.id}/edit`)" v-if="asset">编辑</el-button>
    </div>

    <el-card v-loading="loading" shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions v-if="asset" :column="2" border>
            <el-descriptions-item label="资产名称" :span="2">{{ asset.name }}</el-descriptions-item>
            <el-descriptions-item label="资产编号">{{ asset.assetCode }}</el-descriptions-item>
            <el-descriptions-item label="资产类别">{{ categoryMap[asset.category] || asset.category }}</el-descriptions-item>
            <el-descriptions-item label="使用状态">{{ statusMap[asset.useStatus] || asset.useStatus }}</el-descriptions-item>
            <el-descriptions-item label="所在地">{{ asset.location }}</el-descriptions-item>
            <el-descriptions-item label="保管人">{{ asset.custodian }}</el-descriptions-item>
            <el-descriptions-item label="购置日期">{{ asset.purchaseDate }}</el-descriptions-item>
            <el-descriptions-item label="登记日期">{{ asset.registrationDate }}</el-descriptions-item>
            <el-descriptions-item label="原值">{{ formatCurrency(asset.originalValue) }}</el-descriptions-item>
            <el-descriptions-item label="净值">{{ formatCurrency(asset.currentValue) }}</el-descriptions-item>
            <el-descriptions-item label="累计折旧">{{ formatCurrency(asset.accumulatedDepreciation) }}</el-descriptions-item>
            <el-descriptions-item label="折旧方法">{{ depreciationMethodMap[asset.depreciationMethod] || asset.depreciationMethod }}</el-descriptions-item>
            <el-descriptions-item label="使用年限(月)">{{ asset.usefulLifeMonths }}</el-descriptions-item>
            <el-descriptions-item label="规格型号">{{ asset.specification || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ asset.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
          <el-empty v-else-if="!loading" description="未找到资产信息" />
        </el-tab-pane>

        <el-tab-pane label="生命周期" name="lifecycle">
          <div class="lifecycle-panel" v-if="asset">
            <div class="lifecycle-summary" v-if="summary">
              <el-row :gutter="16">
                <el-col :span="6">
                  <el-statistic title="使用天数" :value="summary.daysInService" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="流转次数" :value="summary.transferCount" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="维修次数" :value="summary.maintenanceCount" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="巡检次数" :value="summary.inspectionCount" />
                </el-col>
              </el-row>
            </div>

            <div class="lifecycle-toolbar">
              <el-select v-model="eventTypeFilter" placeholder="全部事件" clearable style="width: 160px">
                <el-option v-for="t in eventTypes" :key="t.value" :label="t.label" :value="t.value" />
              </el-select>
              <el-button :icon="ascending ? 'SortUp' : 'SortDown'" @click="ascending = !ascending">
                {{ ascending ? '正序' : '倒序' }}
              </el-button>
            </div>

            <el-timeline v-if="lifecycleEvents.length" class="lifecycle-timeline">
              <el-timeline-item
                v-for="event in lifecycleEvents"
                :key="event.id"
                :timestamp="formatTime(event.eventTime)"
                :type="getEventColor(event.eventType)"
                placement="top"
              >
                <div class="timeline-content">
                  <div class="event-title">
                    <el-tag :type="getEventTagType(event.eventType)" size="small">
                      {{ eventTypeLabel(event.eventType) }}
                    </el-tag>
                    <span class="title-text">{{ event.eventTitle }}</span>
                  </div>
                  <div class="event-meta" v-if="event.operatorName">
                    操作人：{{ event.operatorName }}
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无生命周期记录" />

            <el-pagination
              v-if="lifecycleTotal > pageSize"
              :current-page="currentPage"
              :page-size="pageSize"
              :total="lifecycleTotal"
              layout="prev, pager, next"
              @current-change="handlePageChange"
              style="margin-top: 16px; justify-content: center;"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAsset } from '@/api/asset'
import { getAssetLifecycle, getAssetLifecycleSummary } from '@/api/lifecycle'
import type { Asset } from '@/api/asset'
import type { LifecycleEvent, LifecycleSummary } from '@/api/lifecycle'

const route = useRoute()
const router = useRouter()

const asset = ref<Asset | null>(null)
const loading = ref(false)
const activeTab = ref('info')

const lifecycleEvents = ref<LifecycleEvent[]>([])
const summary = ref<LifecycleSummary | null>(null)
const lifecycleTotal = ref(0)
const currentPage = ref(1)
const pageSize = 20
const eventTypeFilter = ref('')
const ascending = ref(false)

const eventTypes = [
  { value: 'STOCK_IN', label: '入库登记' },
  { value: 'LABEL_GEN', label: '标签生成' },
  { value: 'ASSIGNMENT', label: '领用分配' },
  { value: 'TRANSFER', label: '调拨转移' },
  { value: 'INSPECTION', label: '巡检记录' },
  { value: 'MAINTENANCE', label: '维修保养' },
  { value: 'INVENTORY', label: '盘点确认' },
  { value: 'DEPRECIATION', label: '折旧计提' },
  { value: 'VALUE_CHANGE', label: '价值变动' },
  { value: 'DISPOSAL', label: '处置报废' },
  { value: 'APPROVAL', label: '审批流程' },
]

const statusMap: Record<string, string> = {
  IN_USE: '使用中', IDLE: '闲置', RENTED: '已出租',
  MORTGAGED: '已抵押', DISPOSED: '已处置'
}
const categoryMap: Record<string, string> = {
  LAND: '土地', REAL_ESTATE: '房产', EQUIPMENT: '设备',
  VEHICLE: '车辆', FURNITURE: '家具', IT_EQUIPMENT: 'IT设备'
}
const depreciationMethodMap: Record<string, string> = {
  STRAIGHT_LINE: '直线法', DOUBLE_DECLINING: '双倍余额递减法',
  SUM_OF_YEARS: '年限总和法'
}

function formatCurrency(value: number): string {
  if (value == null) return '-'
  return value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY' })
}

function formatTime(time: string): string {
  return time?.replace('T', ' ').substring(0, 16) || ''
}

function eventTypeLabel(type: string): string {
  return eventTypes.find(t => t.value === type)?.label || type
}

function getEventColor(type: string): string {
  const colors: Record<string, string> = {
    STOCK_IN: 'primary', DISPOSAL: 'danger', MAINTENANCE: 'warning',
    INSPECTION: 'success', TRANSFER: 'primary', ASSIGNMENT: 'primary'
  }
  return colors[type] || ''
}

function getEventTagType(type: string): string {
  const types: Record<string, string> = {
    STOCK_IN: '', DISPOSAL: 'danger', MAINTENANCE: 'warning',
    INSPECTION: 'success', TRANSFER: 'info', DEPRECIATION: 'info'
  }
  return types[type] || 'info'
}

async function fetchAsset() {
  const id = route.params.id as string
  if (!id) return
  loading.value = true
  try {
    const response = await getAsset(id)
    asset.value = response.data ?? null
  } catch {
    asset.value = null
  } finally {
    loading.value = false
  }
}

async function fetchLifecycle() {
  const id = route.params.id as string
  if (!id) return
  try {
    const response = await getAssetLifecycle(id, {
      page: currentPage.value,
      size: pageSize,
      eventType: eventTypeFilter.value || undefined,
      ascending: ascending.value,
    })
    lifecycleEvents.value = response.data?.records || []
    lifecycleTotal.value = response.data?.total || 0
  } catch {
    lifecycleEvents.value = []
  }
}

async function fetchSummary() {
  const id = route.params.id as string
  if (!id) return
  try {
    const response = await getAssetLifecycleSummary(id)
    summary.value = response.data ?? null
  } catch {
    summary.value = null
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchLifecycle()
}

watch([eventTypeFilter, ascending], () => {
  currentPage.value = 1
  fetchLifecycle()
})

watch(activeTab, (tab) => {
  if (tab === 'lifecycle' && lifecycleEvents.value.length === 0) {
    fetchLifecycle()
    fetchSummary()
  }
})

onMounted(() => {
  fetchAsset()
})
</script>

<style scoped>
.asset-detail {
  padding: 0;
}
.page-header {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}
.lifecycle-panel {
  padding: 8px 0;
}
.lifecycle-summary {
  margin-bottom: 24px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}
.lifecycle-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.lifecycle-timeline {
  padding-left: 4px;
}
.timeline-content .event-title {
  display: flex;
  align-items: center;
  gap: 8px;
}
.timeline-content .title-text {
  font-size: 14px;
}
.timeline-content .event-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}
</style>
