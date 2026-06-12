<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPurchaseAcceptances } from '@/api/procurement'

const list = ref<any[]>([])
const loading = ref(false)

async function fetch() { loading.value = true; try { const res = await getPurchaseAcceptances(); list.value = res.data } finally { loading.value = false } }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>采购验收记录</h3></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="acceptanceResult" label="验收结果" width="100">
        <template #default="{ row }"><el-tag :type="row.acceptanceResult === 'PASS' ? 'success' : 'danger'">{{ row.acceptanceResult === 'PASS' ? '通过' : '不通过' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="actualQuantity" label="实收数量" width="100" />
      <el-table-column label="实收金额(元)" width="140"><template #default="{ row }">{{ row.actualAmount?.toLocaleString() }}</template></el-table-column>
      <el-table-column prop="checkRemark" label="验收说明" min-width="200" />
      <el-table-column prop="createdAt" label="验收时间" width="180" />
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
