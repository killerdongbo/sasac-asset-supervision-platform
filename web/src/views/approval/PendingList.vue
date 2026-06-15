<template>
  <div class="pending-page">
    <div class="page-header"><h2>待我审批</h2></div>
    <el-card>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="业务类型" width="120"><template #default="{row}">{{bizLabel(row.bizType)}}</template></el-table-column>
        <el-table-column prop="bizId" label="业务单号" width="120" />
        <el-table-column prop="submitterId" label="提交人" width="80" />
        <el-table-column label="当前节点" width="100"><template #default="{row}">第{{row.currentNode}}步</template></el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="170" />
        <el-table-column label="操作" width="280">
          <template #default="{row}">
            <el-button link type="success" @click="handleApprove(row)">通过</el-button>
            <el-button link type="danger" @click="handleReject(row)">驳回</el-button>
            <el-button link type="warning" @click="openAddSign(row)">加签</el-button>
            <el-button link type="info" @click="openFlow(row)">流程图</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 加签对话框 -->
    <el-dialog v-model="showAddSign" title="加签" width="400px">
      <el-form label-width="90px">
        <el-form-item label="加签人ID"><el-input-number v-model="addSignUserId" :min="1"/></el-form-item>
        <el-form-item label="加签人姓名"><el-input v-model="addSignUserName"/></el-form-item>
        <el-form-item label="加签原因"><el-input v-model="addSignReason" type="textarea" :rows="2"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="showAddSign=false">取消</el-button><el-button type="primary" @click="doAddSign">确认加签</el-button></template>
    </el-dialog>

    <!-- 流程可视化 -->
    <el-dialog v-model="showFlow" title="审批流程" width="700px">
      <div class="flow-container">
        <template v-if="flowDef && flowNodes.length">
          <div class="flow-steps">
            <template v-for="(n, i) in flowNodes" :key="n.id || i">
              <div class="flow-step" :class="{'active': flowInstance && n.nodeOrder === flowInstance.currentNode, 'done': flowInstance && n.nodeOrder < flowInstance.currentNode, 'rejected': flowStatus === 'REJECTED' && flowInstance && n.nodeOrder === flowInstance.currentNode}">
                <div class="step-circle">{{n.nodeOrder}}</div>
                <div class="step-label">{{n.approverRole}}</div>
                <div class="step-mode">{{modeLabel(n.approveMode)}}</div>
              </div>
              <div v-if="i < flowNodes.length - 1" class="flow-arrow">→</div>
            </template>
          </div>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingApprovals, approve, addSign, getInstance, getNodes } from '@/api/approval'
import type { ApprovalInstance, ApprovalDef, ApprovalNode } from '@/api/approval'

const list = ref<any[]>([]); const loading = ref(false)
const showAddSign = ref(false)
const currentRow = ref<any>(null)
const addSignUserId = ref(0); const addSignUserName = ref(''); const addSignReason = ref('')

const showFlow = ref(false)
const flowInstance = ref<ApprovalInstance | null>(null)
const flowDef = ref<ApprovalDef | null>(null)
const flowNodes = ref<ApprovalNode[]>([])
const flowStatus = ref('')

const bizLabel = (t: string) => ({ DISPOSAL:'资产处置',TRANSFER:'资产调拨',PURCHASE:'采购申请',INVENTORY_DIFF:'盘点差异',MAINTENANCE:'维保',REPORT:'监管上报' }[t] || t)
const modeLabel = (m?: string) => ({ SINGLE:'单人',COUNTER_SIGN:'会签',OR_SIGN:'或签' }[m||'SINGLE'])

async function fetch() {
  loading.value = true
  try { const r = await getPendingApprovals(undefined, undefined); list.value = r.data || [] } finally { loading.value = false }
}

async function handleApprove(row: any) {
  await approve(row.id, { approverId: 1, approved: true, remark: '同意' })
  ElMessage.success('已通过'); fetch()
}

async function handleReject(row: any) {
  await approve(row.id, { approverId: 1, approved: false, remark: '退回' })
  ElMessage.warning('已驳回'); fetch()
}

function openAddSign(row: any) { currentRow.value = row; addSignUserId.value = 0; addSignUserName.value = ''; addSignReason.value = ''; showAddSign.value = true }

async function doAddSign() {
  if (!currentRow.value) return
  await addSign(currentRow.value.id, { approverId: 1, addSignUserId: addSignUserId.value, addSignUserName: addSignUserName.value, reason: addSignReason.value })
  ElMessage.success('加签成功'); showAddSign.value = false
}

async function openFlow(row: any) {
  flowStatus.value = row.status
  const [instRes, nodesRes] = await Promise.all([getInstance(row.id), getNodes(String(row.defId))])
  flowInstance.value = instRes.data
  flowNodes.value = (nodesRes.data || []).sort((a:ApprovalNode,b:ApprovalNode)=>(a.nodeOrder||0)-(b.nodeOrder||0))
  showFlow.value = true
}

onMounted(fetch)
</script>

<style scoped>
.pending-page { padding:4px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.flow-container { padding:20px 0; }
.flow-steps { display:flex; align-items:center; overflow-x:auto; gap:4px; }
.flow-step { display:flex; flex-direction:column; align-items:center; min-width:80px; padding:8px; border-radius:8px; border:2px solid #e0e0e0; }
.flow-step.active { border-color:#409EFF; background:#ecf5ff; }
.flow-step.done { border-color:#67C23A; background:#f0f9eb; }
.flow-step.rejected { border-color:#F56C6C; background:#fef0f0; }
.step-circle { width:32px; height:32px; border-radius:50%; background:#e0e0e0; display:flex; align-items:center; justify-content:center; font-weight:700; }
.active .step-circle { background:#409EFF; color:#fff; }
.done .step-circle { background:#67C23A; color:#fff; }
.rejected .step-circle { background:#F56C6C; color:#fff; }
.step-label { font-size:12px; margin-top:4px; }
.step-mode { font-size:11px; color:#999; }
.flow-arrow { font-size:20px; color:#ccc; flex-shrink:0; }
</style>
