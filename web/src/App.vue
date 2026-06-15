<template>
  <router-view />
</template>

<script setup lang="ts">
</script>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}

@media (max-width: 768px) {
  .el-dialog { width: 92% !important; margin: 0 auto; }
  .el-form--label-top .el-form-item__label { padding-bottom: 4px; }
  .el-card { border-radius: 8px; }
  .el-table { font-size: 13px; }
  .page-header { flex-direction: column; align-items: flex-start !important; gap: 8px; }
  .page-header h2 { font-size: 18px; margin: 0; }
  .filter-bar { flex-wrap: wrap; gap: 8px; }
  .el-statistic { text-align: center; }
  .el-row .el-col { margin-bottom: 8px; }
}
</style>
