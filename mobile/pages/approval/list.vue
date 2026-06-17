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
import { ref, computed } from 'vue'

const tabs = [
  { key: 'all', label: '全部' },
  { key: 'ASSET', label: '资产' },
  { key: 'HR', label: '人事' },
  { key: 'PROJECT', label: '项目' },
  { key: 'INVEST', label: '投资' },
  { key: 'DECISION', label: '三重一大' }
]

const activeTab = ref('all')

const approvalList = ref([
  { id: 1, title: '服务器采购入库审批', type: 'ASSET', typeLabel: '资产', department: '信息中心', time: '2025-06-17 10:30', status: 'PENDING' },
  { id: 2, title: '员工转正申请 - 张三', type: 'HR', typeLabel: '人事', department: '人事部', time: '2025-06-17 09:15', status: 'PENDING' },
  { id: 3, title: '智慧园区项目进度确认', type: 'PROJECT', typeLabel: '项目', department: '工程部', time: '2025-06-16 14:00', status: 'PENDING' },
  { id: 4, title: '投资方案审批 - 新能源基金', type: 'INVEST', typeLabel: '投资', department: '投资部', time: '2025-06-16 11:20', status: 'PENDING' },
  { id: 5, title: '董事会决议执行确认', type: 'DECISION', typeLabel: '三重一大', department: '办公室', time: '2025-06-15 16:00', status: 'PENDING' }
])

const filteredList = computed(() => {
  if (activeTab.value === 'all') return approvalList.value
  return approvalList.value.filter(item => item.type === activeTab.value)
})

function handleApprove(item) {
  uni.showActionSheet({
    itemList: ['同意', '拒绝', '查看详情'],
    success: (res) => {
      if (res.tapIndex === 0) {
        uni.showToast({ title: '已同意', icon: 'success' })
      } else if (res.tapIndex === 1) {
        uni.showToast({ title: '已拒绝', icon: 'none' })
      } else if (res.tapIndex === 2) {
        // 跳转详情
      }
    }
  })
}
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
