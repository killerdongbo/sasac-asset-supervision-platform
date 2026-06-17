<template>
  <div class="portfolio-list">
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="项目名称/编号/标的企业" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="投资类型">
          <el-select v-model="queryForm.investType" placeholder="全部类型" clearable style="width: 140px">
            <el-option label="全部类型" value="" />
            <el-option v-for="(label, key) in investTypeMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段">
          <el-select v-model="queryForm.phase" placeholder="全部阶段" clearable style="width: 140px">
            <el-option label="全部阶段" value="" />
            <el-option v-for="(label, key) in phaseMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="全部状态" value="" />
            <el-option v-for="(label, key) in statusMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="toolbar">
      <el-button type="primary" @click="router.push('/investments/create')">新增投资项目</el-button>
    </div>

    <el-table :data="projects" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="projectNo" label="项目编号" width="160" />
      <el-table-column prop="projectName" label="项目名称" min-width="180" />
      <el-table-column prop="targetCompany" label="标的企业" width="140" />
      <el-table-column prop="investAmount" label="投资金额" width="130" align="right">
        <template #default="{ row }">
          {{ row.investAmount ? '¥' + Number(row.investAmount).toLocaleString() : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="equityPct" label="股权比例" width="100" align="right">
        <template #default="{ row }">
          {{ row.equityPct ? row.equityPct + '%' : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="expectedRoi" label="预期收益率" width="110" align="right">
        <template #default="{ row }">
          {{ row.expectedRoi ? row.expectedRoi + '%' : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="investType" label="投资类型" width="110">
        <template #default="{ row }">
          {{ investTypeMap[row.investType] || row.investType }}
        </template>
      </el-table-column>
      <el-table-column prop="phase" label="阶段" width="110">
        <template #default="{ row }">
          <el-tag :type="phaseTagType(row.phase)">
            {{ phaseMap[row.phase] || row.phase }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="router.push(`/investments/${row.id}`)">详情</el-button>
          <el-button link type="primary" size="small" @click="router.push(`/investments/${row.id}/edit`)">编辑</el-button>
          <el-button link type="primary" size="small" @click="router.push(`/investments/${row.id}/equity-tree`)">股权穿透</el-button>
          <el-popconfirm title="确定删除该项目吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button link type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { queryInvestmentProjects, deleteInvestmentProject } from '@/api/investment'
import type { InvestProject } from '@/api/investment'

const router = useRouter()

const queryForm = reactive({
  keyword: '',
  investType: '',
  phase: '',
  status: ''
})

const projects = ref<InvestProject[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const investTypeMap: Record<string, string> = {
  EQUITY: '股权投资',
  DEBT: '债权投资',
  FUND: '基金投资',
  OTHER: '其他'
}

const phaseMap: Record<string, string> = {
  PRE_INVEST: '投前',
  INVESTING: '投资中',
  POST_INVEST: '投后管理',
  EXITED: '已退出'
}

const statusMap: Record<string, string> = {
  ACTIVE: '正常',
  INACTIVE: '已停用'
}

function phaseTagType(phase?: string): string {
  const map: Record<string, string> = {
    PRE_INVEST: 'info',
    INVESTING: 'primary',
    POST_INVEST: 'warning',
    EXITED: 'success'
  }
  return map[phase || ''] || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      page: currentPage.value,
      limit: pageSize.value
    }
    if (queryForm.keyword) params.keyword = queryForm.keyword
    if (queryForm.investType) params.investType = queryForm.investType
    if (queryForm.phase) params.phase = queryForm.phase
    if (queryForm.status) params.status = queryForm.status

    const response = await queryInvestmentProjects(params as any)
    projects.value = (response as any).data || []
    total.value = Number((response as any).meta?.total) || 0
  } catch {
    projects.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchData()
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.investType = ''
  queryForm.phase = ''
  queryForm.status = ''
  currentPage.value = 1
  fetchData()
}

async function handleDelete(id: string) {
  try {
    await deleteInvestmentProject(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // Error handled by interceptor
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.portfolio-list {
  padding: 0;
}

.search-card {
  margin-bottom: 16px;
}

.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
