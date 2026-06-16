<template>
  <div class="def-list">
    <div class="page-header"><h2>审批流程配置</h2><el-button type="primary" @click="router.push('/approval/designer')">新建流程</el-button></div>
    <el-card>
      <el-table :data="defs" stripe>
        <el-table-column prop="name" label="流程名称" min-width="180" />
        <el-table-column prop="bizType" label="业务类型" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}"><el-tag :type="row.status==='ACTIVE'?'success':'info'">{{row.status}}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button link type="primary" @click="router.push(`/approval/designer/${row.id}`)">编辑流程</el-button>
            <el-button link type="warning" @click="toggleStatus(row)">{{row.status==='ACTIVE'?'禁用':'启用'}}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDef" title="新建审批流程" width="450px">
      <el-form :model="defForm" label-width="90px">
        <el-form-item label="流程名称"><el-input v-model="defForm.defName"/></el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="defForm.bizType" style="width:100%">
            <el-option v-for="b in bizTypes" :key="b.value" :label="b.label" :value="b.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="defForm.description" type="textarea" :rows="2"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="showCreateDef=false">取消</el-button><el-button type="primary" @click="doCreateDef">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listWorkflows, createWorkflow, updateWorkflow } from '@/api/workflow'
import type { WorkflowDef } from '@/api/workflow'

const router = useRouter()

const bizTypes = [
  { label: '资产处置', value: 'DISPOSAL' },{ label: '资产调拨', value: 'TRANSFER' },
  { label: '采购申请', value: 'PURCHASE' },{ label: '盘点差异', value: 'INVENTORY_DIFF' },
  { label: '维保申请', value: 'MAINTENANCE' },{ label: '监管上报', value: 'REPORT' }
]
const defs = ref<WorkflowDef[]>([])
const showCreateDef = ref(false)
const defForm = ref({ defName: '', bizType: 'DISPOSAL', status: 'DRAFT', description: '' })

async function loadDefs() { const r = await listWorkflows(); defs.value = r.data || [] }

async function doCreateDef() {
  await createWorkflow({ name: defForm.value.defName, bizType: defForm.value.bizType, status: defForm.value.status, description: defForm.value.description } as WorkflowDef)
  ElMessage.success('已创建')
  showCreateDef.value = false; defForm.value = { defName: '', bizType: 'DISPOSAL', status: 'DRAFT', description: '' }; loadDefs()
}

async function toggleStatus(def: WorkflowDef) {
  await updateWorkflow(def.id!, { ...def, status: def.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' }); loadDefs()
}

onMounted(loadDefs)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
