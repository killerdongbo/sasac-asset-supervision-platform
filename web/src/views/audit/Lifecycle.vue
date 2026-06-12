<script setup lang="ts">
import { ref } from 'vue'
import { getAssetLifecycle } from '@/api/audit'
import { ElMessage } from 'element-plus'

const assetId = ref<number | null>(null)
const timeline = ref<any[]>([])

async function handleSearch() {
  if (!assetId.value) { ElMessage.warning('请输入资产ID'); return }
  try { const res = await getAssetLifecycle(assetId.value); timeline.value = res.data || []; } catch { timeline.value = [] }
}
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>资产全生命周期追踪</h3></div>
    <div class="toolbar">
      <span>资产ID：</span><el-input-number v-model="assetId" style="width:200px" />
      <el-button type="primary" @click="handleSearch">追踪</el-button>
    </div>

    <el-timeline v-if="timeline.length">
      <el-timeline-item
        v-for="(item, idx) in timeline"
        :key="idx"
        :timestamp="item.time"
        :color="item.type === 'CHANGE' ? '#2196f3' : '#4caf50'"
      >
        <el-card shadow="hover" size="small">
          <strong>{{ item.type === 'CHANGE' ? '变更' : '操作' }}</strong> — {{ item.field }}<br>
          <span style="font-size:12px;color:#888">{{ item.details }}</span>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="请输入资产ID查询生命周期" />
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
.toolbar { display: flex; gap: 12px; align-items: center; margin-bottom: 24px; }
</style>
