<template>
  <div class="equity-tree">
    <el-card shadow="never">
      <template #header>
        <span>股权穿透图</span>
      </template>

      <div v-if="loading" style="text-align:center;padding:40px">
        <el-icon class="is-loading" :size="32">
          <Loading />
        </el-icon>
        <p>加载中...</p>
      </div>

      <div v-else ref="chartRef" style="width:100%;height:600px"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getEquityTree } from '@/api/investment'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

const route = useRoute()
const chartRef = ref<HTMLElement>()
const loading = ref(true)

async function renderChart() {
  const projectId = route.params.id as string
  try {
    // Use tenantId from local storage or default to 1
    const tenantIdStr = localStorage.getItem('tenantId')
    const tenantId = tenantIdStr ? parseInt(tenantIdStr) : 1
    const res = await getEquityTree(projectId, tenantId)
    const data = (res as any).data

    if (!data) {
      ElMessage.warning('暂无股权穿透数据')
      loading.value = false
      return
    }

    // Transform to ECharts tree format
    const treeData = transformToEChartsTree(data)

    const chart = echarts.init(chartRef.value!)
    chart.setOption({
      tooltip: {
        trigger: 'item' as const,
        formatter: (params: any) => {
          return `${params.name}<br/>${params.value ? '持股: ' + params.value + '%' : ''}`
        }
      },
      series: [{
        type: 'tree',
        data: [treeData],
        top: '5%',
        left: '10%',
        bottom: '5%',
        right: '25%',
        symbolSize: 8,
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
        lineStyle: {
          color: '#409eff',
          width: 2
        }
      }]
    })

    // Resize with window
    window.addEventListener('resize', () => chart.resize())
  } catch {
    ElMessage.error('加载股权穿透数据失败')
  } finally {
    loading.value = false
  }
}

function transformToEChartsTree(node: any): any {
  const result: any = {
    name: node.companyName,
    value: node.equityPct,
    isActualController: node.isActualController,
    itemStyle: node.isActualController ? { color: '#e6a23c' } : undefined
  }

  if (node.children && node.children.length > 0) {
    result.children = node.children.map((child: any) => transformToEChartsTree(child))
  }

  return result
}

onMounted(() => {
  renderChart()
})
</script>

<style scoped>
.equity-tree {
  padding: 0;
}
</style>
