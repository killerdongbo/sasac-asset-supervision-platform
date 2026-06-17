<template>
  <view class="project-progress">
    <!-- 项目选择器 -->
    <view class="project-selector" @click="showProjectPicker">
      <text class="selector-label">{{ selectedProject || '请选择项目' }}</text>
      <text class="selector-arrow">▼</text>
    </view>

    <!-- 进度条 -->
    <view class="progress-card">
      <view class="progress-header">
        <text class="progress-title">总体进度</text>
        <text class="progress-pct">{{ progress }}%</text>
      </view>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: progress + '%' }"></view>
      </view>

      <view class="progress-dates">
        <text>开始: 2025-01-15</text>
        <text>计划结束: 2025-12-31</text>
      </view>
    </view>

    <!-- 完成工作 -->
    <view class="section-card">
      <text class="section-title">本期完成工作</text>
      <textarea class="input-area" v-model="completedWork" placeholder="请输入本期完成的工作内容"></textarea>
    </view>

    <!-- 存在问题 -->
    <view class="section-card">
      <text class="section-title">存在问题</text>
      <textarea class="input-area" v-model="issues" placeholder="请输入当前存在的问题"></textarea>
    </view>

    <!-- 下期计划 -->
    <view class="section-card">
      <text class="section-title">下期计划</text>
      <textarea class="input-area" v-model="nextPlan" placeholder="请输入下期工作计划"></textarea>
    </view>

    <!-- 提交按钮 -->
    <button class="submit-btn" @click="handleSubmit">提交进度</button>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/index'
import { queryProjects, recordProgress, getProgressHistory } from '@/api/project'

const authStore = useAuthStore()

const selectedProject = ref('')
const selectedProjectId = ref(null)
const projects = ref([])
const progress = ref(0)
const completedWork = ref('')
const issues = ref('')
const nextPlan = ref('')
const loading = ref(false)

async function fetchProjects() {
  try {
    const res = await queryProjects({ limit: 50 })
    if (res.success && res.data) {
      projects.value = res.data
      if (res.data.length > 0) {
        selectedProject.value = res.data[0].name
        selectedProjectId.value = res.data[0].id
        progress.value = res.data[0].progressPct || 0
      }
    }
  } catch (err) {
    console.error('Failed to fetch projects:', err)
  }
}

function showProjectPicker() {
  if (projects.value.length === 0) return
  const itemList = projects.value.map(p => p.name)
  uni.showActionSheet({
    itemList,
    success: async (res) => {
      const idx = res.tapIndex
      selectedProject.value = projects.value[idx].name
      selectedProjectId.value = projects.value[idx].id
      progress.value = projects.value[idx].progressPct || 0
      completedWork.value = ''
      issues.value = ''
      nextPlan.value = ''
      try {
        const historyRes = await getProgressHistory(selectedProjectId.value, { limit: 1 })
        if (historyRes.success && historyRes.data && historyRes.data.length > 0) {
          const last = historyRes.data[0]
          completedWork.value = last.completedWork || ''
          issues.value = last.issues || ''
          nextPlan.value = last.nextPlan || ''
        }
      } catch (err) {
        // ignore history load failure
      }
    }
  })
}

async function handleSubmit() {
  if (!selectedProjectId.value) {
    uni.showToast({ title: '请选择项目', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res = await recordProgress(selectedProjectId.value, {
      projectId: selectedProjectId.value,
      tenantId: Number(authStore.tenantId),
      reportDate: new Date().toISOString().slice(0, 10),
      completedWork: completedWork.value,
      issues: issues.value,
      nextPlan: nextPlan.value
    })
    if (res.success) {
      uni.showToast({ title: '进度提交成功', icon: 'success' })
    }
  } catch (err) {
    uni.showToast({ title: '提交失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProjects()
})
</script>

<style lang="scss">
.project-progress {
  padding: 16px;
}
.project-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 16px;
}
.selector-label {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}
.selector-arrow {
  font-size: 12px;
  color: #999;
}
.progress-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 16px;
  margin-bottom: 16px;
}
.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.progress-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}
.progress-pct {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
}
.progress-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 12px;
}
.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #00d4ff);
  border-radius: 4px;
  transition: width 0.3s;
}
.progress-dates {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
.section-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8px;
}
.input-area {
  width: 100%;
  min-height: 80px;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
}
.submit-btn {
  width: 100%;
  height: 44px;
  background: #001529;
  color: #fff;
  font-size: 16px;
  border-radius: 22px;
  margin-top: 8px;
}
</style>
