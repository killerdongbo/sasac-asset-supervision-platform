<template>
  <div class="invest-dashboard">
    <el-card shadow="never" style="margin-bottom:16px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>项目信息</span>
          <div>
            <el-button type="primary" size="small" @click="router.push(`/investments/${route.params.id}/edit`)">编辑</el-button>
            <el-button size="small" @click="router.push(`/investments/${route.params.id}/equity-tree`)">股权穿透</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="3" border v-if="project">
        <el-descriptions-item label="项目编号">{{ project.projectNo }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ project.projectName }}</el-descriptions-item>
        <el-descriptions-item label="标的企业">{{ project.targetCompany }}</el-descriptions-item>
        <el-descriptions-item label="投资类型">{{ investTypeMap[project.investType!] || project.investType }}</el-descriptions-item>
        <el-descriptions-item label="投资金额">{{ project.investAmount ? '¥' + Number(project.investAmount).toLocaleString() : '-' }}</el-descriptions-item>
        <el-descriptions-item label="股权比例">{{ project.equityPct ? project.equityPct + '%' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属行业">{{ project.industry || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所在地区">{{ project.region || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预期收益率">{{ project.expectedRoi ? project.expectedRoi + '%' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="阶段" :span="3">
          <el-tag :type="phaseTagType(project.phase)">{{ phaseMap[project.phase!] || project.phase }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标的企业描述" :span="3">
          {{ project.targetDescription || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="尽职调查" name="dd">
          <el-button type="primary" size="small" style="margin-bottom:12px" @click="showDDDialog = true">新增尽调</el-button>
          <el-table :data="ddRecords" border stripe size="small" style="width:100%">
            <el-table-column prop="ddType" label="尽调类型" width="100" />
            <el-table-column prop="lawFirm" label="律师事务所" width="160" />
            <el-table-column prop="accountingFirm" label="会计师事务所" width="160" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'COMPLETED' ? '已完成' : '进行中' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="交易记录" name="deals">
          <el-button type="primary" size="small" style="margin-bottom:12px" @click="showDealDialog = true">新增交易</el-button>
          <el-table :data="dealRecords" border stripe size="small" style="width:100%">
            <el-table-column prop="dealDate" label="交易日期" width="110" />
            <el-table-column prop="dealAmount" label="交易金额" width="130" align="right">
              <template #default="{ row }">{{ row.dealAmount ? '¥' + Number(row.dealAmount).toLocaleString() : '-' }}</template>
            </el-table-column>
            <el-table-column prop="equityAcquired" label="获得股权" width="100" align="right">
              <template #default="{ row }">{{ row.equityAcquired ? row.equityAcquired + '%' : '-' }}</template>
            </el-table-column>
            <el-table-column prop="agreementNo" label="协议编号" width="140" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="投后管理" name="post">
          <el-button type="primary" size="small" style="margin-bottom:12px" @click="showPostDialog = true">新增投后报告</el-button>
          <el-table :data="postRecords" border stripe size="small" style="width:100%">
            <el-table-column prop="reportDate" label="报告日期" width="110" />
            <el-table-column prop="revenue" label="营业收入" width="130" align="right">
              <template #default="{ row }">{{ row.revenue ? '¥' + Number(row.revenue).toLocaleString() : '-' }}</template>
            </el-table-column>
            <el-table-column prop="netProfit" label="净利润" width="130" align="right">
              <template #default="{ row }">{{ row.netProfit ? '¥' + Number(row.netProfit).toLocaleString() : '-' }}</template>
            </el-table-column>
            <el-table-column prop="debtRatio" label="资产负债率" width="110" align="right">
              <template #default="{ row }">{{ row.debtRatio ? row.debtRatio + '%' : '-' }}</template>
            </el-table-column>
            <el-table-column prop="riskLevel" label="风险等级" width="100">
              <template #default="{ row }">
                <el-tag :type="riskTagType(row.riskLevel)" size="small">{{ riskLevelMap[row.riskLevel!] || row.riskLevel }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="退出记录" name="exit">
          <el-button type="primary" size="small" style="margin-bottom:12px" @click="showExitDialog = true">新增退出</el-button>
          <el-table :data="exitRecords" border stripe size="small" style="width:100%">
            <el-table-column prop="exitDate" label="退出日期" width="110" />
            <el-table-column prop="exitAmount" label="退出金额" width="130" align="right">
              <template #default="{ row }">{{ row.exitAmount ? '¥' + Number(row.exitAmount).toLocaleString() : '-' }}</template>
            </el-table-column>
            <el-table-column prop="exitMethod" label="退出方式" width="110" />
            <el-table-column prop="returnRate" label="回报率" width="100" align="right">
              <template #default="{ row }">{{ row.returnRate ? row.returnRate + '%' : '-' }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- DD Dialog -->
    <el-dialog v-model="showDDDialog" title="新增尽职调查" width="500px">
      <el-form :model="ddForm" label-width="120px">
        <el-form-item label="尽调类型">
          <el-input v-model="ddForm.ddType" />
        </el-form-item>
        <el-form-item label="律师事务所">
          <el-input v-model="ddForm.lawFirm" />
        </el-form-item>
        <el-form-item label="会计师事务所">
          <el-input v-model="ddForm.accountingFirm" />
        </el-form-item>
        <el-form-item label="报告摘要">
          <el-input v-model="ddForm.reportSummary" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="风险发现">
          <el-input v-model="ddForm.riskFindings" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDDDialog = false">取消</el-button>
        <el-button type="primary" :loading="ddLoading" @click="handleSaveDD">保存</el-button>
      </template>
    </el-dialog>

    <!-- Deal Dialog -->
    <el-dialog v-model="showDealDialog" title="新增交易记录" width="500px">
      <el-form :model="dealForm" label-width="120px">
        <el-form-item label="交易日期">
          <el-date-picker v-model="dealForm.dealDate" type="date" style="width:100%" />
        </el-form-item>
        <el-form-item label="交易金额">
          <el-input-number v-model="dealForm.dealAmount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="获得股权(%)">
          <el-input-number v-model="dealForm.equityAcquired" :min="0" :max="100" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="协议编号">
          <el-input v-model="dealForm.agreementNo" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDealDialog = false">取消</el-button>
        <el-button type="primary" :loading="dealLoading" @click="handleSaveDeal">保存</el-button>
      </template>
    </el-dialog>

    <!-- Post-investment Dialog -->
    <el-dialog v-model="showPostDialog" title="新增投后报告" width="500px">
      <el-form :model="postForm" label-width="120px">
        <el-form-item label="报告日期">
          <el-date-picker v-model="postForm.reportDate" type="date" style="width:100%" />
        </el-form-item>
        <el-form-item label="营业收入">
          <el-input-number v-model="postForm.revenue" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="净利润">
          <el-input-number v-model="postForm.netProfit" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="净资产">
          <el-input-number v-model="postForm.netAssets" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="资产负债率(%)">
          <el-input-number v-model="postForm.debtRatio" :min="0" :max="100" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="重大事项">
          <el-input v-model="postForm.majorEvents" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="postForm.riskLevel" style="width:100%">
            <el-option label="低风险" value="LOW" />
            <el-option label="中风险" value="MEDIUM" />
            <el-option label="高风险" value="HIGH" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPostDialog = false">取消</el-button>
        <el-button type="primary" :loading="postLoading" @click="handleSavePost">保存</el-button>
      </template>
    </el-dialog>

    <!-- Exit Dialog -->
    <el-dialog v-model="showExitDialog" title="新增退出记录" width="500px">
      <el-form :model="exitForm" label-width="120px">
        <el-form-item label="退出日期">
          <el-date-picker v-model="exitForm.exitDate" type="date" style="width:100%" />
        </el-form-item>
        <el-form-item label="退出金额">
          <el-input-number v-model="exitForm.exitAmount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="退出方式">
          <el-select v-model="exitForm.exitMethod" style="width:100%">
            <el-option label="股权转让" value="TRANSFER" />
            <el-option label="上市退出" value="IPO" />
            <el-option label="回购" value="REPURCHASE" />
            <el-option label="清算" value="LIQUIDATION" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showExitDialog = false">取消</el-button>
        <el-button type="primary" :loading="exitLoading" @click="handleSaveExit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getInvestmentProject,
  getDDRecords, recordDD,
  getDealRecords, recordDeal,
  getPostRecords, recordPost,
  getExitRecords, recordExit
} from '@/api/investment'
import type { InvestProject, InvestDD, InvestDeal, InvestPost, InvestExit } from '@/api/investment'

const router = useRouter()
const route = useRoute()

const project = ref<InvestProject | null>(null)
const activeTab = ref('dd')

// Record lists
const ddRecords = ref<InvestDD[]>([])
const dealRecords = ref<InvestDeal[]>([])
const postRecords = ref<InvestPost[]>([])
const exitRecords = ref<InvestExit[]>([])

// Dialog visibility
const showDDDialog = ref(false)
const showDealDialog = ref(false)
const showPostDialog = ref(false)
const showExitDialog = ref(false)

// Loading states
const ddLoading = ref(false)
const dealLoading = ref(false)
const postLoading = ref(false)
const exitLoading = ref(false)

// Forms
const ddForm = reactive({ ddType: '', lawFirm: '', accountingFirm: '', reportSummary: '', riskFindings: '' })
const dealForm = reactive({ dealDate: '', dealAmount: undefined as number | undefined, equityAcquired: undefined as number | undefined, agreementNo: '' })
const postForm = reactive({ reportDate: '', revenue: undefined as number | undefined, netProfit: undefined as number | undefined, netAssets: undefined as number | undefined, debtRatio: undefined as number | undefined, majorEvents: '', riskLevel: '' })
const exitForm = reactive({ exitDate: '', exitAmount: undefined as number | undefined, exitMethod: '' })

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

const riskLevelMap: Record<string, string> = {
  LOW: '低风险',
  MEDIUM: '中风险',
  HIGH: '高风险'
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

function riskTagType(level?: string): string {
  const map: Record<string, string> = {
    LOW: 'success',
    MEDIUM: 'warning',
    HIGH: 'danger'
  }
  return map[level || ''] || 'info'
}

function getTenantId(): number {
  const id = localStorage.getItem('tenantId')
  return id ? parseInt(id) : 1
}

async function loadProject() {
  try {
    const res = await getInvestmentProject(route.params.id as string)
    project.value = (res as any).data
  } catch {
    ElMessage.error('加载项目信息失败')
  }
}

async function loadRecords() {
  const id = route.params.id as string
  const tenantId = getTenantId()
  try {
    const [ddRes, dealRes, postRes, exitRes] = await Promise.all([
      getDDRecords(id, tenantId),
      getDealRecords(id, tenantId),
      getPostRecords(id, tenantId),
      getExitRecords(id, tenantId)
    ])
    ddRecords.value = (ddRes as any).data || []
    dealRecords.value = (dealRes as any).data || []
    postRecords.value = (postRes as any).data || []
    exitRecords.value = (exitRes as any).data || []
  } catch {
    // Partial failures handled individually
  }
}

async function handleSaveDD() {
  ddLoading.value = true
  try {
    await recordDD(route.params.id as string, { ...ddForm, tenantId: getTenantId() } as any)
    ElMessage.success('保存成功')
    showDDDialog.value = false
    loadRecords()
  } catch {
    // Error handled by interceptor
  } finally {
    ddLoading.value = false
  }
}

async function handleSaveDeal() {
  dealLoading.value = true
  try {
    await recordDeal(route.params.id as string, { ...dealForm, tenantId: getTenantId() } as any)
    ElMessage.success('保存成功')
    showDealDialog.value = false
    loadRecords()
  } catch {
    // Error handled by interceptor
  } finally {
    dealLoading.value = false
  }
}

async function handleSavePost() {
  postLoading.value = true
  try {
    await recordPost(route.params.id as string, { ...postForm, tenantId: getTenantId() } as any)
    ElMessage.success('保存成功')
    showPostDialog.value = false
    loadRecords()
  } catch {
    // Error handled by interceptor
  } finally {
    postLoading.value = false
  }
}

async function handleSaveExit() {
  exitLoading.value = true
  try {
    await recordExit(route.params.id as string, { ...exitForm, tenantId: getTenantId() } as any)
    ElMessage.success('保存成功')
    showExitDialog.value = false
    loadRecords()
  } catch {
    // Error handled by interceptor
  } finally {
    exitLoading.value = false
  }
}

onMounted(() => {
  loadProject()
  loadRecords()
})
</script>

<style scoped>
.invest-dashboard {
  padding: 0;
}
</style>
