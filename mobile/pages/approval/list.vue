<template>
  <view class="approval-list">
    <!-- Tab 筛选 -->
    <view class="tab-bar">
      <view
        class="tab-item"
        v-for="tab in tabs"
        :key="tab.key"
        :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <!-- 列表 -->
    <uni-list>
      <uni-list-item
        v-for="item in filteredList"
        :key="item.id"
        :title="item.title"
        :note="item.department + ' | ' + item.time"
        :right-text="item.status === 'PENDING' ? '待审批' : '已处理'"
        clickable
        @click="handleApprove(item)"
      >
        <template v-slot:header>
          <uni-tag
            :text="item.typeLabel"
            :type="item.type === 'ASSET' ? 'primary' : item.type === 'HR' ? 'success' : item.type === 'PROJECT' ? 'warning' : item.type === 'INVEST' ? 'danger' : 'default'"
            size="small"
          />
        </template>
      </uni-list-item>
      <view class="empty-tip" v-if="filteredList.length === 0">
        <text>暂无待审批事项</text>
      </view>
    </uni-list>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getPendingApprovals, approve } from '@/api/approval'

const tabs = [
  { key: 'all', label: '全部' },
  { key: 'ASSET', label: '资产' },
  { key: 'HR', label: '人事' },
  { key: 'PROJECT', label: '项目' },
  { key: 'INVEST', label: '投资' },
  { key: 'DECISION', label: '三重一大' }
]

const activeTab = ref('all')

const approvalList = ref([])
const loading = ref(false)

const filteredList = computed(() => {
  if (activeTab.value === 'all') return approvalList.value
  return approvalList.value.filter(item => item.bizType === activeTab.value)
})

function getTypeLabel(type) {
  const map = { ASSET: '资产', HR: '人事', PROJECT: '项目', INVEST: '投资', DECISION: '三重一大' }
  return map[type] || type
}

function handleApprove(item) {
  uni.showActionSheet({
    itemList: ['同意', '拒绝', '查看详情'],
    success: async (res) => {
      if (res.tapIndex === 0) {
        try {
          await approve(item.id, { approved: true, remark: '移动端审批通过' })
          uni.showToast({ title: '已同意', icon: 'success' })
          approvalList.value = approvalList.value.filter(i => i.id !== item.id)
        } catch (err) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      } else if (res.tapIndex === 1) {
        uni.showToast({ title: '已拒绝', icon: 'none' })
      }
    }
  })
}

async function fetchApprovals() {
  loading.value = true
  try {
    const res = await getPendingApprovals()
    if (res.success && res.data) {
      approvalList.value = res.data.map(item => ({
        id: item.id,
        title: item.title || `审批事项 #${item.id}`,
        type: item.bizType || 'ASSET',
        typeLabel: getTypeLabel(item.bizType),
        department: item.department || '-',
        time: item.createdAt || '-',
        status: item.status || 'PENDING'
      }))
    }
  } catch (err) {
    console.error('Failed to fetch approvals:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchApprovals()
})
</script>

<style lang="scss">
.approval-list {
  padding: 0;
}
.tab-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}
.tab-item {
  padding: 4px 14px;
  border-radius: 16px;
  font-size: 13px;
  color: #666;
  background: #f5f5f5;
}
.tab-item.active {
  color: #fff;
  background: #001529;
}
.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}
</style>
