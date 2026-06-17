<template>
  <view class="decision-approve">
    <!-- 加载中 -->
    <view v-if="loading" class="loading-tip">
      <text>加载中...</text>
    </view>

    <!-- 事项信息 -->
    <view class="detail-card" v-else-if="item">
      <view class="detail-header">
        <uni-tag text="三重一大" type="error" size="small"></uni-tag>
        <text class="detail-id">决议编号: {{ item.itemNo || item.id }}</text>
      </view>
      <text class="detail-title">{{ item.title }}</text>

      <view class="info-row">
        <text class="info-label">决议日期</text>
        <text class="info-value">{{ item.createdAt ? item.createdAt.slice(0, 10) : '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">责任部门</text>
        <text class="info-value">{{ item.department || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">项目金额</text>
        <text class="info-value">{{ item.amount ? '¥' + Number(item.amount).toLocaleString() : '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">状态</text>
        <text class="info-value warn">{{ item.status || 'PENDING' }}</text>
      </view>

      <view class="desc-section">
        <text class="desc-label">事项描述</text>
        <text class="desc-content">{{ item.description || '暂无描述' }}</text>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="loading-tip">
      <text>暂无待审批事项</text>
    </view>

    <!-- 审批操作 -->
    <view class="action-bar">
      <button class="btn-reject" @click="handleReject">拒绝</button>
      <button class="btn-approve" @click="handleApprove">同意</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPendingItems, approveItem } from '@/api/decision'

const item = ref(null)
const loading = ref(false)
const itemId = ref('')

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  if (currentPage?.options?.id) {
    itemId.value = currentPage.options.id
  }
  fetchDetail()
})

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getPendingItems({ limit: 10 })
    if (res.success && res.data && res.data.length > 0) {
      const found = itemId.value
        ? res.data.find(i => String(i.id) === itemId.value)
        : res.data[0]
      if (found) item.value = found
    }
  } catch (err) {
    console.error('Failed to fetch decision item:', err)
  } finally {
    loading.value = false
  }
}

async function handleApprove() {
  if (!item.value) return
  uni.showModal({
    title: '确认',
    content: '确认同意该决议事项？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await approveItem(item.value.id, { approved: true, remark: '移动端审批通过' })
          uni.showToast({ title: '审批通过', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 1500)
        } catch (err) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      }
    }
  })
}

async function handleReject() {
  if (!item.value) return
  uni.showModal({
    title: '确认',
    content: '确认拒绝该决议事项？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await approveItem(item.value.id, { approved: false, remark: '移动端拒绝' })
          uni.showToast({ title: '已拒绝', icon: 'none' })
          setTimeout(() => uni.navigateBack(), 1500)
        } catch (err) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      }
    }
  })
}
</script>

<style lang="scss">
.decision-approve {
  padding: 16px;
}
.detail-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 16px;
}
.detail-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.detail-id {
  font-size: 12px;
  color: #999;
}
.detail-title {
  font-size: 17px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
  line-height: 1.5;
}
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}
.info-label {
  font-size: 14px;
  color: #999;
}
.info-value {
  font-size: 14px;
  color: #333;
}
.info-value.warn {
  color: #e6a23c;
}
.desc-section {
  margin-top: 16px;
}
.desc-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8px;
}
.desc-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}
.action-bar {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}
.loading-tip {
  text-align: center;
  color: #999;
  padding: 40px 16px;
  font-size: 14px;
}
.btn-reject {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  border: 1px solid #ddd;
  background: #fff;
  color: #666;
  font-size: 16px;
}
.btn-approve {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  background: #001529;
  color: #fff;
  font-size: 16px;
}
</style>
