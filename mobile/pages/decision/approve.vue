<template>
  <view class="decision-approve">
    <!-- 事项信息 -->
    <view class="detail-card">
      <view class="detail-header">
        <uni-tag text="三重一大" type="error" size="small"></uni-tag>
        <text class="detail-id">决议编号: JY-2025-006</text>
      </view>
      <text class="detail-title">关于智慧园区二期建设项目投资的决议</text>

      <view class="info-row">
        <text class="info-label">决议日期</text>
        <text class="info-value">2025-06-10</text>
      </view>
      <view class="info-row">
        <text class="info-label">责任部门</text>
        <text class="info-value">战略发展部</text>
      </view>
      <view class="info-row">
        <text class="info-label">项目金额</text>
        <text class="info-value">¥2,500万</text>
      </view>
      <view class="info-row">
        <text class="info-label">截止日期</text>
        <text class="info-value warn">2025-07-10（剩余23天）</text>
      </view>

      <view class="desc-section">
        <text class="desc-label">事项描述</text>
        <text class="desc-content">
          同意启动智慧园区二期项目建设，总投资预算2500万元，包含智慧安防、智慧能源、智慧办公三大模块，建设周期12个月。
        </text>
      </view>
    </view>

    <!-- 审批操作 -->
    <view class="action-bar">
      <button class="btn-reject" @click="handleReject">拒绝</button>
      <button class="btn-approve" @click="handleApprove">同意</button>
    </view>
  </view>
</template>

<script setup>
function handleApprove() {
  uni.showModal({
    title: '确认',
    content: '确认同意该决议事项？',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '审批通过', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 1500)
      }
    }
  })
}

function handleReject() {
  uni.showModal({
    title: '确认',
    content: '确认拒绝该决议事项？',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '已拒绝', icon: 'none' })
        setTimeout(() => uni.navigateBack(), 1500)
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
