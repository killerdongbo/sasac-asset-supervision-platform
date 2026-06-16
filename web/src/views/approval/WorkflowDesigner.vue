<template>
  <div class="workflow-designer">
    <!-- Top toolbar -->
    <div class="designer-toolbar">
      <el-input v-model="defName" placeholder="流程名称" style="width:200px" />
      <el-select v-model="bizType" placeholder="业务类型" style="width:160px">
        <el-option v-for="b in bizTypes" :key="b.value" :label="b.label" :value="b.value" />
      </el-select>
      <el-button type="primary" @click="saveWorkflow">保存</el-button>
      <el-button @click="$router.push('/approval/defs')">返回列表</el-button>
    </div>

    <!-- Main area -->
    <div class="designer-main">
      <!-- Left: Node palette -->
      <div class="node-palette">
        <div
          v-for="node in nodeTypes"
          :key="node.type"
          class="palette-node"
          :style="{ background: node.bg, borderColor: node.border }"
          draggable="true"
          @dragstart="onDragStart($event, node)"
        >
          {{ node.icon }} {{ node.label }}
        </div>
      </div>

      <!-- Center: Vue Flow canvas -->
      <div class="canvas-wrapper" @drop="onDrop" @dragover.prevent>
        <VueFlow
          ref="vueFlowRef"
          v-model="elements"
          :default-viewport="{ zoom: 1, x: 0, y: 0 }"
          :node-types="customNodeTypes"
          fit-view-on-init
          @pane-click="deselectNode"
          @node-click="onNodeClick"
          @edge-click="onEdgeClick"
          @connect="onConnect"
        >
          <Background />
          <Controls />
          <MiniMap />
        </VueFlow>
      </div>

      <!-- Right: Properties panel -->
      <div class="props-panel" v-if="selectedNode">
        <h4>节点属性</h4>
        <el-form v-if="selectedNode.type === 'approval'" label-width="90px" size="small">
          <el-form-item label="节点名称">
            <el-input v-model="selectedNode.data.label" @input="onPropChanged" />
          </el-form-item>
          <el-form-item label="审批角色">
            <el-input v-model="selectedNode.data.approverRole" @input="onPropChanged" />
          </el-form-item>
          <el-form-item label="审批模式">
            <el-radio-group v-model="selectedNode.data.approveMode" @change="onPropChanged">
              <el-radio value="SINGLE">单人</el-radio>
              <el-radio value="COUNTER_SIGN">会签</el-radio>
              <el-radio value="OR_SIGN">或签</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="超时(小时)">
            <el-input-number v-model="selectedNode.data.timeoutHours" :min="0" @change="onPropChanged" />
          </el-form-item>
          <el-form-item label="超时动作">
            <el-select v-model="selectedNode.data.timeoutAction" @change="onPropChanged">
              <el-option value="ESCALATE" label="升级" />
              <el-option value="NOTIFY" label="通知" />
              <el-option value="AUTO_APPROVE" label="自动通过" />
              <el-option value="AUTO_REJECT" label="自动驳回" />
            </el-select>
          </el-form-item>
          <el-form-item label="允许加签">
            <el-switch v-model="selectedNode.data.allowAddSign" @change="onPropChanged" />
          </el-form-item>
          <el-form-item label="允许驳回">
            <el-switch v-model="selectedNode.data.canReject" @change="onPropChanged" />
          </el-form-item>
        </el-form>
        <el-form v-else-if="selectedNode.type === 'gateway'" label-width="90px" size="small">
          <el-form-item label="节点名称">
            <el-input v-model="selectedNode.data.label" @input="onPropChanged" />
          </el-form-item>
          <el-form-item label="条件表达式">
            <el-input v-model="selectedNode.data.conditionExpr" placeholder="amount > 100000" @input="onPropChanged" />
          </el-form-item>
        </el-form>
        <div v-else-if="selectedNode.type !== 'edge-default'">
          <el-form label-width="90px" size="small">
            <el-form-item label="节点名称">
              <el-input v-model="selectedNode.data.label" @input="onPropChanged" />
            </el-form-item>
          </el-form>
        </div>
        <div v-else>
          <el-form label-width="90px" size="small">
            <el-form-item label="连线标签">
              <el-input v-model="selectedNode.label" @input="onPropChanged" placeholder="是/否" />
            </el-form-item>
          </el-form>
        </div>
        <el-button type="danger" size="small" @click="deleteSelectedNode" style="margin-top:12px">删除</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, markRaw, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { VueFlow, Handle, Position } from '@vue-flow/core'
import { Background } from '@vue-flow/background'
import { Controls } from '@vue-flow/controls'
import { MiniMap } from '@vue-flow/minimap'
import '@vue-flow/core/dist/style.css'
import '@vue-flow/core/dist/theme-default.css'
import '@vue-flow/controls/dist/style.css'
import '@vue-flow/minimap/dist/style.css'
import { getWorkflow, createWorkflow, updateWorkflow } from '@/api/workflow'
import type { WorkflowDef } from '@/api/workflow'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const defId = ref<string | null>(null)
const defName = ref('')
const bizType = ref('')
const elements = ref<any[]>([])
const selectedNode = ref<any>(null)
const vueFlowRef = ref()
let idCounter = 0

const bizTypes = [
  { value: 'DISPOSAL', label: '资产处置' },
  { value: 'TRANSFER', label: '资产调拨' },
  { value: 'PURCHASE', label: '采购审批' },
  { value: 'INVENTORY_DIFF', label: '盘点差异' },
  { value: 'MAINTENANCE', label: '维保维修' },
]

const nodeTypes = [
  { type: 'start', label: '开始', icon: '▶', bg: '#dbeafe', border: '#3b82f6' },
  { type: 'approval', label: '审批', icon: '👤', bg: '#f1f5f9', border: '#94a3b8' },
  { type: 'gateway', label: '判断', icon: '◇', bg: '#fef9c3', border: '#eab308' },
  { type: 'parallel', label: '并行', icon: '⫸', bg: '#f3e8ff', border: '#a855f7' },
  { type: 'join', label: '汇聚', icon: '⫷', bg: '#f3e8ff', border: '#a855f7' },
  { type: 'subflow', label: '子流程', icon: '↗', bg: '#fce7f3', border: '#ec4899' },
  { type: 'end', label: '结束', icon: '⏹', bg: '#dcfce7', border: '#22c55e' },
]

// Custom Vue Flow node component
const CustomNode = markRaw({
  props: ['data', 'type', 'sourcePosition', 'targetPosition'],
  setup(props: any) {
    const typeStyles: Record<string, any> = {
      start: { borderRadius: '20px', background: '#dbeafe', border: '2px solid #3b82f6', padding: '8px 20px', fontSize: '13px', fontWeight: '700', minWidth: '80px', textAlign: 'center' },
      approval: { borderRadius: '8px', background: '#f8fafc', border: '2px solid #64748b', padding: '10px 16px', fontSize: '12px', minWidth: '160px' },
      gateway: { borderRadius: '8px', background: '#fef9c3', border: '2px solid #eab308', padding: '8px 14px', fontSize: '11px', minWidth: '110px' },
      parallel: { borderRadius: '6px', background: '#f3e8ff', border: '2px solid #a855f7', padding: '8px 14px', fontSize: '12px', minWidth: '100px', textAlign: 'center' },
      join: { borderRadius: '6px', background: '#f3e8ff', border: '2px solid #a855f7', padding: '8px 14px', fontSize: '12px', minWidth: '80px', textAlign: 'center' },
      subflow: { borderRadius: '8px', background: '#fce7f3', border: '2px dashed #ec4899', padding: '10px 14px', fontSize: '12px', minWidth: '140px' },
      end: { borderRadius: '20px', background: '#dcfce7', border: '2px solid #22c55e', padding: '8px 20px', fontSize: '13px', fontWeight: '700', minWidth: '80px', textAlign: 'center' },
    }
    const icons: Record<string, string> = { start: '▶', approval: '👤', gateway: '◇', parallel: '⫸', join: '⫷', subflow: '↗', end: '⏹' }

    // Helper to build sub-info line with a label + value
    const subInfo = (label: string, value: any, color = '#64748b') => {
      if (value === undefined || value === null || value === '' || value === 0) return null
      return h('div', { style: { fontSize: '10px', color, marginTop: '2px', lineHeight: '1.4', wordBreak: 'break-all' } }, label + String(value))
    }

    return () => {
      const children: any[] = []
      const nodeType = props.type || 'approval'
      const d = props.data || {}
      const style = typeStyles[nodeType] || typeStyles.approval

      // Target handle (input)
      if (nodeType !== 'start') {
        children.push(h(Handle, { type: 'target', position: Position.Top, style: { background: '#555', width: '10px', height: '10px', border: '2px solid #fff' } }))
      }

      // ---- Node header ----
      children.push(
        h('div', { style: { display: 'flex', alignItems: 'center', gap: '6px', fontWeight: '600', fontSize: style.fontSize || '12px', marginBottom: (nodeType === 'approval' || nodeType === 'gateway' || nodeType === 'subflow') ? '4px' : '0' } }, [
          h('span', null, icons[nodeType] || ''),
          h('span', null, d.label || nodeType),
        ])
      )

      // ---- Node details (approval node) ----
      if (nodeType === 'approval') {
        // Approval mode badge
        const modeMap: Record<string, string> = { SINGLE: '单人审批', COUNTER_SIGN: '会签', OR_SIGN: '或签' }
        const modeLabel = modeMap[d.approveMode] || ''
        children.push(subInfo('✅ 审批角色：', d.approverRole || '未设置', '#1e40af'))
        if (modeLabel) children.push(subInfo('📋 审批模式：', modeLabel, '#6366f1'))
        if (d.timeoutHours > 0) {
          const actionMap: Record<string, string> = { ESCALATE: '升级', NOTIFY: '通知', AUTO_APPROVE: '自动通过', AUTO_REJECT: '自动驳回' }
          children.push(subInfo('⏰ 超时：', `${d.timeoutHours}h ` + (actionMap[d.timeoutAction] || ''), '#dc2626'))
        }
        // Additional features
        const features: string[] = []
        if (d.allowAddSign) features.push('可加签')
        if (d.canReject) features.push('可驳回')
        if (features.length) children.push(subInfo('🔧 ', features.join(' | '), '#059669'))
      }

      // ---- Node details (gateway node) ----
      if (nodeType === 'gateway') {
        children.push(subInfo('⚖️ 条件：', d.conditionExpr || '未设置条件', '#b45309'))
        children.push(h('div', { style: { fontSize: '9px', color: '#94a3b8', marginTop: '2px' } }, 'true/false 分支'))
      }

      // ---- Node details (subflow node) ----
      if (nodeType === 'subflow') {
        children.push(subInfo('📄 引用流程ID：', d.refDefId || '未设置', '#be185d'))
      }

      // Source handle (output)
      if (nodeType !== 'end') {
        children.push(h(Handle, { type: 'source', position: Position.Bottom, style: { background: '#555', width: '10px', height: '10px', border: '2px solid #fff' } }))
      }

      return h('div', { style }, children)
    }
  },
})
const customNodeTypes: Record<string, any> = {}
for (const nt of nodeTypes) {
  customNodeTypes[nt.type] = CustomNode
}

// Drag from palette
function onDragStart(event: DragEvent, node: any) {
  event.dataTransfer!.setData('application/vueflow-node', JSON.stringify(node))
  event.dataTransfer!.effectAllowed = 'move'
}

function onDrop(event: DragEvent) {
  const nodeData = JSON.parse(event.dataTransfer!.getData('application/vueflow-node'))
  const rect = (event.target as HTMLElement)
    .closest('.canvas-wrapper')
    ?.getBoundingClientRect()
  const position = rect
    ? { x: event.clientX - rect.left - 80, y: event.clientY - rect.top - 25 }
    : { x: event.clientX - 80, y: event.clientY - 25 }
  idCounter++
  const newNode = {
    id: `${nodeData.type}_${idCounter}`,
    type: nodeData.type,
    position,
    data: {
      label: nodeData.label,
      approverRole: '',
      approveMode: 'SINGLE',
      timeoutHours: 0,
      timeoutAction: 'ESCALATE',
      allowAddSign: true,
      canReject: true,
      conditionExpr: '',
      mergeMode: 'ALL',
      refDefId: 0,
    },
  }
  elements.value.push(newNode)
}

function onConnect(connection: any) {
  elements.value.push({
    id: `edge_${connection.source}_${connection.target}_${++idCounter}`,
    source: connection.source,
    target: connection.target,
    label: '',
    type: 'smoothstep',
    animated: false,
  })
}

function onNodeClick({ node }: any) {
  selectedNode.value = node
}

function onEdgeClick({ edge }: any) {
  selectedNode.value = edge
}

function deselectNode() {
  selectedNode.value = null
}

function onPropChanged() {
  // Vue Flow reactivity: force re-render of elements
  elements.value = [...elements.value]
}

function deleteSelectedNode() {
  if (!selectedNode.value) return
  const id = selectedNode.value.id
  // Remove node and all connected edges
  elements.value = elements.value.filter(
    (el: any) => el.id !== id && el.source !== id && el.target !== id,
  )
  selectedNode.value = null
  ElMessage.success('已删除')
}

async function saveWorkflow() {
  if (!defName.value.trim()) {
    ElMessage.warning('请输入流程名称')
    return
  }
  const graphJson = JSON.stringify({
    nodes: elements.value.filter((e: any) => e.position !== undefined),
    edges: elements.value.filter((e: any) => e.source !== undefined),
  })
  const def: WorkflowDef = {
    name: defName.value,
    bizType: bizType.value || 'DISPOSAL',
    graphJson,
  }
  try {
    if (defId.value) {
      await updateWorkflow(defId.value, def)
    } else {
      const res = await createWorkflow(def)
      defId.value = res.data!.id!
      router.replace(`/approval/designer/${defId.value}`)
    }
    ElMessage.success('流程已保存')
  } catch {
    /* handled by interceptor */
  }
}

onMounted(async () => {
  const id = route.params.defId as string
  if (id) {
    defId.value = id
    const res = await getWorkflow(id)
    const def = res.data!
    defName.value = def.name
    bizType.value = def.bizType
    if (def.graphJson) {
      try {
        const graph = JSON.parse(def.graphJson)
        elements.value = [...(graph.nodes || []), ...(graph.edges || [])]
        idCounter = elements.value.filter((e: any) => e.position).length
      } catch {
        elements.value = []
      }
    }
  }
})
</script>

<style scoped>
.workflow-designer {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
}

.designer-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 8px 16px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
}

.designer-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.node-palette {
  width: 100px;
  background: #fff;
  border-right: 1px solid #e2e8f0;
  padding: 8px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.palette-node {
  padding: 6px 8px;
  border-radius: 4px;
  font-size: 11px;
  cursor: grab;
  text-align: center;
  border: 2px solid;
}

.canvas-wrapper {
  flex: 1;
}

.props-panel {
  width: 220px;
  background: #fff;
  border-left: 1px solid #e2e8f0;
  padding: 12px;
  overflow-y: auto;
}

.props-panel h4 {
  margin: 0 0 12px;
  font-size: 14px;
}
</style>
