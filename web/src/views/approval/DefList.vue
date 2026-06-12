<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDefs, createDef, addNode } from '@/api/approval'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ defName: '', bizType: 'DISPOSAL', description: '' })

async function fetch() { loading.value = true; try { const res = await getDefs(); list.value = res.data || [] } finally { loading.value = false } }
async function handleCreate() { const saved = (await createDef(form.value)).data; await addNode(saved.id, { nodeOrder: 1, approverRole: 'DEPT_MANAGER', canReject: true }); ElMessage.success('审批流创建成功'); dialogVisible.value = false; fetch() }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>审批流程配置</h3><el-button type="primary" @click="dialogVisible = true">新增审批流</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="defName" label="审批名称" min-width="200" />
      <el-table-column prop="bizType" label="业务类型" width="140" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '启用' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
    </el-table>

    <el-dialog title="新增审批流" v-model="dialogVisible" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.defName" /></el-form-item>
        <el-form-item label="业务类型"><el-select v-model="form.bizType"><el-option label="资产报废" value="DISPOSAL" /><el-option label="资产调拨" value="TRANSFER" /><el-option label="采购申请" value="PURCHASE" /><el-option label="盘点差异" value="INVENTORY_DIFF" /><el-option label="维保维修" value="MAINTENANCE" /><el-option label="监管上报" value="REPORT" /></el-select></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleCreate">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
