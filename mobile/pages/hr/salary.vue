<template>
  <view class="salary-page">
    <!-- 月份选择 -->
    <view class="month-picker">
      <text class="month-label">{{ currentMonth }}</text>
      <uni-dateformat :date="new Date()" format="yyyy年MM月"></uni-dateformat>
    </view>

    <!-- 工资卡片 -->
    <view class="salary-card">
      <view class="card-header">
        <text class="card-title">工资明细</text>
        <text class="card-period">{{ currentMonth }}</text>
      </view>

      <view class="salary-item" v-for="item in earnings" :key="item.label">
        <text class="item-label">{{ item.label }}</text>
        <text class="item-value">{{ formatMoney(item.value) }}</text>
      </view>

      <view class="divider"></view>

      <view class="salary-item deduction" v-for="item in deductions" :key="item.label">
        <text class="item-label">{{ item.label }}</text>
        <text class="item-value">{{ formatMoney(item.value) }}</text>
      </view>

      <view class="divider"></view>

      <view class="salary-item total">
        <text class="item-label">实发金额</text>
        <text class="item-value total-value">{{ formatMoney(netSalary) }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/index'
import { getSalaryRecords } from '@/api/hr'

const authStore = useAuthStore()

const currentMonth = ref('')
const salaryData = ref(null)
const loading = ref(false)

const earnings = ref([])
const deductions = ref([])
const netSalary = ref(0)

async function fetchSalary() {
  loading.value = true
  try {
    const now = new Date()
    const year = now.getFullYear()
    const month = now.getMonth() + 1
    currentMonth.value = `${year}-${String(month).padStart(2, '0')}`

    const res = await getSalaryRecords({
      tenantId: authStore.tenantId,
      employeeId: authStore.userInfo?.id,
      salaryYear: year,
      salaryMonth: month
    })

    if (res.success && res.data && res.data.length > 0) {
      const record = res.data[0]
      salaryData.value = record
      earnings.value = [
        { label: '基本工资', value: record.baseSalary || 0 },
        { label: '岗位津贴', value: record.allowance || 0 },
        { label: '绩效奖金', value: record.performancePay || 0 },
        { label: '加班补贴', value: record.overtimePay || 0 }
      ]
      deductions.value = [
        { label: '养老保险', value: -(record.socialInsurance || 0) },
        { label: '医疗保险', value: -(record.medicalInsurance || 0) },
        { label: '住房公积金', value: -(record.housingFund || 0) },
        { label: '个人所得税', value: -(record.tax || 0) }
      ]
      netSalary.value = record.netSalary || 0
    }
  } catch (err) {
    console.error('Failed to fetch salary:', err)
  } finally {
    loading.value = false
  }
}

function formatMoney(val) {
  if (!val && val !== 0) return '¥0.00'
  return '¥' + Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

onMounted(() => {
  fetchSalary()
})
</script>

<style lang="scss">
.salary-page {
  padding: 16px;
}
.month-picker {
  text-align: center;
  margin-bottom: 16px;
}
.month-label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.salary-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}
.card-period {
  font-size: 13px;
  color: #999;
}
.salary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}
.item-label {
  font-size: 14px;
  color: #666;
}
.item-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.deduction .item-value {
  color: #f56c6c;
}
.divider {
  height: 1px;
  background: #f0f0f0;
  margin: 4px 0;
}
.total {
  margin-top: 8px;
}
.total-value {
  font-size: 20px;
  font-weight: 700;
  color: #67c23a;
}
</style>
