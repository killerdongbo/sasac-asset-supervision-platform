<template>
  <div class="property-tree">
    <el-card shadow="never">
      <template #header>
        <span class="card-title">产权关系树状图</span>
      </template>

      <div v-if="loading" v-loading="loading" style="height: 500px" />
      <div v-else ref="chartRef" style="width: 100%; height: 600px" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { buildPropertyTree } from '@/api/property'
import type { PropertyTreeNode } from '@/api/property'

const chartRef = ref<HTMLDivElement>()
const loading = ref(false)

function getTenantId(): number {
  return Number(localStorage.getItem('tenantId')) || 0
}

function renderChart(treeData: PropertyTreeNode[]) {
  if (!chartRef.value || treeData.length === 0) return

  // Build ECharts tree data
  const chartData = {
    name: '产权结构',
    children: treeData.map(org => ({
      name: org.label,
      children: (org.children || []).map(child => ({
        name: child.label,
        value: child.regNo || '',
        itemStyle: child.regType === 'CANCEL' ? { color: '#f56c6c' } : undefined
      }))
    }))
  }

  // Dynamically load ECharts
  const script = document.createElement('script')
  script.src = 'https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js'
  script.onload = () => {
    const echarts = (window as any).echarts
    if (!echarts || !chartRef.value) return

    const myChart = echarts.init(chartRef.value)
    myChart.setOption({
      tooltip: {
        trigger: 'item',
        triggerOn: 'mousemove',
        formatter: (params: any) => {
          return params.name + (params.value ? '<br/>编号: ' + params.value : '')
        }
      },
      series: [
        {
          type: 'tree',
          data: [chartData],
          top: '5%',
          left: '7%',
          bottom: '5%',
          right: '15%',
          symbolSize: 10,
          label: {
            position: 'left',
            verticalAlign: 'middle',
            align: 'right',
            fontSize: 13
          },
          leaves: {
            label: {
              position: 'right',
              verticalAlign: 'middle',
              align: 'left'
            }
          },
          expandAndCollapse: true,
          animationDuration: 550,
          animationDurationUpdate: 750
        }
      ]
    })

    // Resize handler
    const resizeHandler = () => myChart.resize()
    window.addEventListener('resize', resizeHandler)
  }
  document.head.appendChild(script)
}

async function fetchTree() {
  loading.value = true
  try {
    const tenantId = getTenantId()
    if (!tenantId) {
      ElMessage.warning('请先登录')
      return
    }

    const response = await buildPropertyTree(tenantId)
    const treeData = (response as any).data || []

    await nextTick()
    renderChart(treeData)
  } catch {
    ElMessage.error('加载产权树失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTree()
})
</script>

<style scoped>
.property-tree {
  padding: 0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
