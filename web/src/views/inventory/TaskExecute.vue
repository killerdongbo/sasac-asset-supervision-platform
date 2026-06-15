<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getRecords, createRecord } from '@/api/inventory'
import { ElMessage } from 'element-plus'

const route = useRoute()
const task = ref<any>(null)
const records = ref<any[]>([])
const form = ref({ assetId: null as number | null, exists: true, actualLocation: '', actualStatus: 'IN_USE', remark: '' })

async function fetch() {
  const id = Number(route.params.id)
  const res = await getRecords(id)
  task.value = { id, taskName: '盘点任务 #' + id }
  records.value = res.data || []
}

async function handleSubmit() {
  await createRecord({ taskId: Number(route.params.id), ...form.value, operatorId: 1 })
  ElMessage.success('盘点记录已保存'); form.value = { assetId: null, exists: true, actualLocation: '', actualStatus: 'IN_USE', remark: '' }; fetch()
}

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>执行盘点：{{ task?.taskName }}</h3></div>
    <el-card style="margin-bottom:16px">
      <el-form :model="form" inline>
        <el-form-item label="资产ID"><el-input-number v-model="form.assetId" /></el-form-item>
        <el-form-item label="实物存在"><el-switch v-model="form.exists" /></el-form-item>
        <el-form-item label="实际位置"><el-input v-model="form.actualLocation" style="width:180px" /></el-form-item>
        <el-form-item label="实际状态">
          <el-select v-model="form.actualStatus" style="width:140px">
            <el-option label="使用中" value="IN_USE" /><el-option label="闲置" value="IDLE" /><el-option label="已处置" value="DISPOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" style="width:180px" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSubmit">提交</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-table :data="records" border stripe size="small">
      <el-table-column prop="assetId" label="资产ID" width="100" />
      <el-table-column label="实物存在" width="100"><template #default="{ row }"><el-tag :type="row.exists ? 'success' : 'danger'">{{ row.exists ? '是' : '否' }}</el-tag></template></el-table-column>
      <el-table-column prop="actualLocation" label="实际位置" width="160" />
      <el-table-column prop="actualStatus" label="实际状态" width="100" />
      <el-table-column prop="remark" label="备注" min-width="200" />
    </el-table>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
