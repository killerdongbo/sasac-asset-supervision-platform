<script setup lang="ts">
// TODO: getAllDiffs and adjustDiff need backend endpoints; currently requires taskId
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>盘点差异报告</h3></div>
    <el-empty description="请从盘点任务页面查看差异">
      <template #extra>差异数据按任务展示，请在具体盘点任务中查看</template>
    </el-empty>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
