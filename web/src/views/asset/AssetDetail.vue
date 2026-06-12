<template>
  <div class="asset-detail">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
      <el-button type="primary" @click="router.push(`/assets/${asset?.id}/edit`)" v-if="asset">编辑</el-button>
    </div>

    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span class="card-title">资产详细信息</span>
      </template>

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
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAsset } from '@/api/asset'
import type { Asset } from '@/api/asset'

const route = useRoute()
const router = useRouter()

const asset = ref<Asset | null>(null)
const loading = ref(false)

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

const depreciationMethodMap: Record<string, string> = {
  STRAIGHT_LINE: '直线法',
  DOUBLE_DECLINING: '双倍余额递减法',
  SUM_OF_YEARS: '年限总和法'
}

function formatCurrency(value: number): string {
  if (value == null) return '-'
  return value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY' })
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

.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
