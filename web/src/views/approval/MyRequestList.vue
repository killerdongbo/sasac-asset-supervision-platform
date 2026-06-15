<template>
  <div class="my-req-page">
    <div class="page-header"><h2>我发起的申请</h2></div>
    <el-card>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="bizType" label="业务类型" width="120" />
        <el-table-column prop="bizId" label="业务单号" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{row}"><el-tag :type="statusTag(row.status)">{{statusText(row.status)}}</el-tag></template>
        </el-table-column>
        <el-table-column label="当前节点" width="80"><template #default="{row}">第{{row.currentNode}}步</template></el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="170" />
        <el-table-column label="操作" width="100">
          <template #default="{row}"><el-button link type="primary" @click="openFlow(row)">查看流程</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showFlow" title="审批流程" width="700px">
      <div class="flow-container">
        <div class="flow-steps" v-if="flowNodes.length">
          <template v-for="(n, i) in flowNodes" :key="n.id || i">
            <div class="flow-step" :class="{'active': flowInst && n.nodeOrder === flowInst.currentNode, 'done': flowInst && n.nodeOrder < flowInst.currentNode, 'rejected': flowInst && flowInst.status==='REJECTED' && n.nodeOrder === flowInst.currentNode}">
              <div class="step-circle">{{n.nodeOrder}}</div>
              <div class="step-label">{{n.approverRole}}</div>
              <div class="step-mode">{{n.approveMode === 'COUNTER_SIGN' ? '会签' : n.approveMode === 'OR_SIGN' ? '或签' : '单人'}}</div>
            </div>
            <div v-if="i < flowNodes.length - 1" class="flow-arrow">→</div>
          </template>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyRequests, getInstance, getNodes } from '@/api/approval'
import type { ApprovalInstance, ApprovalNode } from '@/api/approval'

const list = ref<any[]>([]); const loading = ref(false)
const showFlow = ref(false)
const flowInst = ref<ApprovalInstance | null>(null)
const flowNodes = ref<ApprovalNode[]>([])

function statusTag(s: string) { return { PENDING:'warning', APPROVED:'success', REJECTED:'danger', CANCELLED:'info' }[s] || 'info' }
function statusText(s: string) { return { PENDING:'审批中', APPROVED:'已通过', REJECTED:'已驳回', CANCELLED:'已取消' }[s] || s }

async function fetch() {
  loading.value = true
  try { const r = await getMyRequests(1); list.value = r.data || [] } finally { loading.value = false }
}

async function openFlow(row: any) {
  const [instRes, nodesRes] = await Promise.all([getInstance(row.id), getNodes(String(row.defId))])
  flowInst.value = instRes.data
  flowNodes.value = (nodesRes.data || []).sort((a:ApprovalNode,b:ApprovalNode)=>(a.nodeOrder||0)-(b.nodeOrder||0))
  showFlow.value = true
}

onMounted(fetch)
</script>

<style scoped>
.my-req-page { padding:4px; }
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
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
