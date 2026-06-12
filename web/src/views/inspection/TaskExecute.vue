<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTask, getRecords, createRecord, completeTask } from '@/api/inspection'
import { ElMessage } from 'element-plus'

const route = useRoute(); const router = useRouter()
const task = ref<any>(null)
const records = ref<any[]>([])
const form = ref({ assetId: null as number | null, isNormal: true, actualLocation: '', actualStatus: 'IN_USE', anomalyType: '', description: '' })

async function fetch() {
  const id = Number(route.params.id); task.value = (await getTask(id)).data; records.value = (await getRecords(id)).data || []
}

async function handleSubmit() {
  await createRecord({ taskId: Number(route.params.id), ...form.value, isNormal: form.value.isNormal, inspectorId: 1 })
  ElMessage.success('巡检记录已保存'); fetch()
}

async function handleComplete() {
  await completeTask(Number(route.params.id)); ElMessage.success('任务已完成'); router.push('/inspection-tasks')
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>执行巡检：{{ task?.taskName }}</h3><el-button type="success" @click="handleComplete">完成任务</el-button></div>
    <el-card style="margin-bottom:16px">
      <el-form :model="form" inline>
        <el-form-item label="资产ID"><el-input-number v-model="form.assetId" /></el-form-item>
        <el-form-item label="是否正常"><el-switch v-model="form.isNormal" /></el-form-item>
        <el-form-item label="实际位置"><el-input v-model="form.actualLocation" style="width:200px" /></el-form-item>
        <el-form-item v-if="!form.isNormal" label="异常类型">
          <el-select v-model="form.anomalyType" style="width:160px">
            <el-option label="资产损坏" value="DAMAGED" />
            <el-option label="实物未找到" value="NOT_FOUND" />
            <el-option label="位置不符" value="WRONG_LOCATION" />
            <el-option label="状态异常" value="STATUS_ABNORMAL" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!form.isNormal" label="描述"><el-input v-model="form.description" style="width:200px" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSubmit">提交记录</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-table :data="records" border stripe size="small">
      <el-table-column prop="assetId" label="资产ID" width="100" />
      <el-table-column label="是否正常" width="100"><template #default="{ row }"><el-tag :type="row.isNormal ? 'success' : 'danger'">{{ row.isNormal ? '正常' : '异常' }}</el-tag></template></el-table-column>
      <el-table-column prop="actualLocation" label="实际位置" width="160" />
      <el-table-column prop="anomalyType" label="异常类型" width="140" />
      <el-table-column prop="description" label="描述" min-width="200" />
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
