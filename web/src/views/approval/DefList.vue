<template>
  <div class="def-list">
    <div class="page-header"><h2>审批流程配置</h2><el-button type="primary" @click="showCreateDef=true">新建流程</el-button></div>
    <el-card>
      <el-table :data="defs" stripe>
        <el-table-column prop="defName" label="流程名称" min-width="180" />
        <el-table-column prop="bizType" label="业务类型" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}"><el-tag :type="row.status==='ACTIVE'?'success':'info'">{{row.status}}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button link type="primary" @click="openNodes(row)">节点配置</el-button>
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

    <el-drawer v-model="showNodes" title="节点配置" :size="600" direction="rtl">
      <template v-if="currentDef">
        <div v-for="n in sortedNodes" :key="n.id" class="node-item">
          <div class="node-header">
            <span><b>第{{n.nodeOrder}}步：{{n.approverRole}}</b></span>
            <el-tag size="small">{{modeLabel(n.approveMode)}}</el-tag>
            <el-tag v-if="n.timeoutHours" size="small" type="warning">超时{{n.timeoutHours}}h</el-tag>
          </div>
          <div class="node-actions">
            <el-button link size="small" type="primary" @click="editNode(n)">编辑</el-button>
            <el-button link size="small" type="danger" @click="doDeleteNode(n.id!)">删除</el-button>
          </div>
        </div>
        <el-button type="primary" @click="openAddNode" style="margin-top:12px">+ 添加节点</el-button>
      </template>
    </el-drawer>

    <el-dialog v-model="showNodeForm" :title="editingNodeId ? '编辑节点' : '新增节点'" width="520px">
      <el-form :model="nodeForm" label-width="110px">
        <el-form-item label="顺序"><el-input-number v-model="nodeForm.nodeOrder" :min="1"/></el-form-item>
        <el-form-item label="审批角色"><el-input v-model="nodeForm.approverRole"/></el-form-item>
        <el-form-item label="审批模式">
          <el-radio-group v-model="nodeForm.approveMode">
            <el-radio value="SINGLE">单人</el-radio><el-radio value="COUNTER_SIGN">会签</el-radio><el-radio value="OR_SIGN">或签</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="允许加签"><el-switch v-model="nodeForm.allowAddSign"/></el-form-item>
        <el-form-item label="允许驳回"><el-switch v-model="nodeForm.canReject"/></el-form-item>
        <el-form-item label="超时(小时)"><el-input-number v-model="nodeForm.timeoutHours" :min="0"/></el-form-item>
        <el-form-item label="超时动作">
          <el-select v-model="nodeForm.timeoutAction" style="width:100%">
            <el-option label="升级" value="ESCALATE"/><el-option label="通知" value="NOTIFY"/>
            <el-option label="自动通过" value="AUTO_APPROVE"/><el-option label="自动驳回" value="AUTO_REJECT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="升级角色"><el-input v-model="nodeForm.escalateRole"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="showNodeForm=false">取消</el-button><el-button type="primary" @click="doSaveNode">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDefs, createDef, getNodes, addNode, updateNode, deleteNode } from '@/api/approval'
import type { ApprovalDef, ApprovalNode } from '@/api/approval'

const bizTypes = [
  { label: '资产处置', value: 'DISPOSAL' },{ label: '资产调拨', value: 'TRANSFER' },
  { label: '采购申请', value: 'PURCHASE' },{ label: '盘点差异', value: 'INVENTORY_DIFF' },
  { label: '维保申请', value: 'MAINTENANCE' },{ label: '监管上报', value: 'REPORT' }
]
const defs = ref<ApprovalDef[]>([])
const showCreateDef = ref(false)
const defForm = ref({ defName: '', bizType: 'DISPOSAL', status: 'DRAFT', description: '' })
const showNodes = ref(false)
const currentDef = ref<ApprovalDef | null>(null)
const nodes = ref<ApprovalNode[]>([])
const sortedNodes = computed(() => [...nodes.value].sort((a,b) => (a.nodeOrder||0) - (b.nodeOrder||0)))
const showNodeForm = ref(false)
const editingNodeId = ref<string | null>(null)
const nodeForm = ref<ApprovalNode>({ nodeOrder: 1, approverRole: '', approveMode: 'SINGLE', allowAddSign: true, canReject: true, timeoutHours: 0, timeoutAction: 'ESCALATE', escalateRole: '' })

function modeLabel(m?: string) { return { SINGLE: '单人', COUNTER_SIGN: '会签', OR_SIGN: '或签' }[m || 'SINGLE'] }

async function loadDefs() { const r = await getDefs(); defs.value = r.data || [] }

async function doCreateDef() {
  await createDef(defForm.value); ElMessage.success('已创建')
  showCreateDef.value = false; defForm.value = { defName: '', bizType: 'DISPOSAL', status: 'DRAFT', description: '' }; loadDefs()
}

async function openNodes(def: ApprovalDef) {
  currentDef.value = def
  const r = await getNodes(def.id!); nodes.value = r.data || []
  showNodes.value = true
}

function openAddNode() {
  editingNodeId.value = null
  nodeForm.value = { nodeOrder: nodes.value.length + 1, approverRole: '', approveMode: 'SINGLE', allowAddSign: true, canReject: true, timeoutHours: 0, timeoutAction: 'ESCALATE', escalateRole: '' }
  showNodeForm.value = true
}

function editNode(n: ApprovalNode) { editingNodeId.value = n.id!; nodeForm.value = { ...n }; showNodeForm.value = true }

async function doSaveNode() {
  if (editingNodeId.value) await updateNode(currentDef.value!.id!, editingNodeId.value, nodeForm.value)
  else await addNode(currentDef.value!.id!, nodeForm.value)
  ElMessage.success('已保存'); showNodeForm.value = false
  if (currentDef.value) openNodes(currentDef.value)
}

async function doDeleteNode(nodeId: string) {
  await deleteNode(currentDef.value!.id!, nodeId); ElMessage.success('已删除')
  if (currentDef.value) openNodes(currentDef.value)
}

async function toggleStatus(def: ApprovalDef) {
  await createDef({ ...def, status: def.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' }); loadDefs()
}

onMounted(loadDefs)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.node-item { background:#f9fafb; padding:12px; margin-bottom:8px; border-radius:6px; border:1px solid #eee; }
.node-header { display:flex; align-items:center; gap:8px; margin-bottom:8px; }
.node-actions { display:flex; gap:8px; }
</style>
