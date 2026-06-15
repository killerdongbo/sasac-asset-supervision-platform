<template>
  <div class="history-page">
    <div class="page-header"><h2>审批历史</h2></div>
    <el-card>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="bizType" label="业务类型" width="120">
          <template #default="{row}">{{bizLabel(row.bizType)}}</template>
        </el-table-column>
        <el-table-column prop="bizId" label="业务单号" width="120" />
        <el-table-column prop="submitterId" label="提交人" width="80" />
        <el-table-column label="结果" width="100">
          <template #default="{row}">
            <el-tag :type="row.status==='APPROVED'?'success':row.status==='REJECTED'?'danger':'info'">
              {{row.status==='APPROVED'?'通过':row.status==='REJECTED'?'驳回':row.status}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="通过节点数" width="100">
          <template #default="{row}">第{{row.currentNode}}步</template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="完成时间" width="170" />
        <el-table-column label="操作" width="100">
          <template #default="{row}"><el-button link type="primary" @click="openFlow(row)">流程图</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showFlow" title="审批流程" width="700px">
      <div class="flow-container">
        <div class="flow-steps" v-if="flowNodes.length">
          <template v-for="(n, i) in flowNodes" :key="n.id || i">
            <div class="flow-step done">
              <div class="step-circle">{{n.nodeOrder}}</div>
              <div class="step-label">{{n.approverRole}}</div>
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
import { getMyRequests, getNodes } from '@/api/approval'
import type { ApprovalNode } from '@/api/approval'

const list = ref<any[]>([]); const loading = ref(false)
const showFlow = ref(false); const flowNodes = ref<ApprovalNode[]>([])

const bizLabel = (t: string) => ({ DISPOSAL:'资产处置',TRANSFER:'资产调拨',PURCHASE:'采购',INVENTORY_DIFF:'盘点差异',MAINTENANCE:'维保',REPORT:'监管上报' }[t] || t)

async function fetch() {
  loading.value = true
  try { const r = await getMyRequests(1); list.value = (r.data || []).filter((i:any) => i.status !== 'PENDING') } finally { loading.value = false }
}

async function openFlow(row: any) {
  const nodesRes = await getNodes(String(row.defId))
  flowNodes.value = (nodesRes.data || []).sort((a:ApprovalNode,b:ApprovalNode)=>(a.nodeOrder||0)-(b.nodeOrder||0))
  showFlow.value = true
}

onMounted(fetch)
</script>

<style scoped>
.history-page { padding:4px; }
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.flow-container { padding:20px 0; }
.flow-steps { display:flex; align-items:center; overflow-x:auto; gap:4px; }
.flow-step { display:flex; flex-direction:column; align-items:center; min-width:80px; padding:8px; border-radius:8px; border:2px solid #e0e0e0; }
.flow-step.done { border-color:#67C23A; background:#f0f9eb; }
.step-circle { width:32px; height:32px; border-radius:50%; background:#e0e0e0; display:flex; align-items:center; justify-content:center; font-weight:700; }
.done .step-circle { background:#67C23A; color:#fff; }
.step-label { font-size:12px; margin-top:4px; }
.flow-arrow { font-size:20px; color:#ccc; flex-shrink:0; }
</style>
