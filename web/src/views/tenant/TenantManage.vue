<template>
  <div class="tenant-manage">
    <div class="page-header">
      <h2>租户管理</h2>
      <el-button type="primary" @click="openForm(null)">新增租户</el-button>
    </div>

    <el-card>
      <div class="filter-bar">
        <el-input v-model="keyword" placeholder="搜索租户名称" clearable style="width: 200px" @keyup.enter="loadData" />
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px; margin-left: 12px" @change="loadData">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
          <el-option label="过期" :value="2" />
        </el-select>
        <el-button style="margin-left: 12px" @click="loadData">查询</el-button>
      </div>

      <el-table :data="tenants" v-loading="loading" stripe>
        <el-table-column prop="tenantCode" label="租户编码" width="120" />
        <el-table-column prop="tenantName" label="租户名称" min-width="180" />
        <el-table-column prop="edition" label="版本" width="100">
          <template #default="{ row }">
            <el-tag :type="editionType(row.edition)">{{ editionLabel(row.edition) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="电话" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'danger' : 'warning'">
              {{ row.status === 1 ? '正常' : row.status === 0 ? '禁用' : '过期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maxUsers" label="用户上限" width="90" align="right" />
        <el-table-column prop="maxAssets" label="资产上限" width="90" align="right" />
        <el-table-column prop="expireTime" label="到期时间" width="120">
          <template #default="{ row }">{{ row.expireTime?.split('T')[0] || '永久' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openForm(row)">编辑</el-button>
            <el-button link type="info" @click="openUsage(row)">用量</el-button>
            <el-button link type="warning" @click="openConfig(row)">配置</el-button>
            <el-button link :type="row.status === 1 ? 'danger' : 'success'" @click="doToggle(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        :page-size="20"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadData"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="showForm" :title="editingId ? '编辑租户' : '新增租户'" width="620px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="租户编码"><el-input v-model="form.tenantCode" :disabled="!!editingId" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租户名称"><el-input v-model="form.tenantName" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系人"><el-input v-model="form.contactPerson" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="版本">
              <el-select v-model="form.edition" style="width: 100%">
                <el-option label="基础版" value="BASIC" />
                <el-option label="标准版" value="STANDARD" />
                <el-option label="企业版" value="ENTERPRISE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期时间">
              <el-date-picker v-model="form.expireTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户上限"><el-input-number v-model="form.maxUsers" :min="1" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产上限"><el-input-number v-model="form.maxAssets" :min="1" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 用量对话框 -->
    <el-dialog v-model="showUsage" title="租户用量" width="400px">
      <el-descriptions :column="1" border v-if="usage">
        <el-descriptions-item label="用户数">{{ usage.userCount }} / {{ currentTenant?.maxUsers }}</el-descriptions-item>
        <el-descriptions-item label="资产数">{{ usage.assetCount }} / {{ currentTenant?.maxAssets }}</el-descriptions-item>
        <el-descriptions-item label="存储用量">{{ usage.storageUsedMb }} MB</el-descriptions-item>
        <el-descriptions-item label="最后登录">{{ usage.lastLoginTime || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="doRefreshUsage">刷新统计</el-button>
      </template>
    </el-dialog>

    <!-- 配置对话框 -->
    <el-dialog v-model="showConfig" title="租户配置" width="550px">
      <el-table :data="configs" size="small">
        <el-table-column prop="configKey" label="配置项" width="160" />
        <el-table-column prop="configValue" label="值" />
        <el-table-column prop="description" label="说明" width="150" />
      </el-table>
      <el-divider />
      <el-form :inline="true" :model="configForm" size="small">
        <el-form-item label="Key"><el-input v-model="configForm.key" style="width: 120px" /></el-form-item>
        <el-form-item label="Value"><el-input v-model="configForm.value" style="width: 140px" /></el-form-item>
        <el-form-item><el-button type="primary" @click="doSaveConfig">保存</el-button></el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listTenants, createTenant, updateTenant, toggleTenantStatus, getTenantConfigs, saveTenantConfig, getTenantUsage, refreshTenantUsage } from '@/api/tenant'
import type { Tenant, TenantConfig, TenantUsage } from '@/api/tenant'

const loading = ref(false)
const tenants = ref<Tenant[]>([])
const keyword = ref('')
const statusFilter = ref<number | undefined>(undefined)
const page = ref(1)
const total = ref(0)

const showForm = ref(false)
const editingId = ref<string | null>(null)
const form = ref({ tenantCode: '', tenantName: '', contactPerson: '', contactPhone: '', edition: 'STANDARD', expireTime: '', maxUsers: 50, maxAssets: 10000, remark: '' })

const showUsage = ref(false)
const usage = ref<TenantUsage | null>(null)
const currentTenant = ref<Tenant | null>(null)

const showConfig = ref(false)
const configs = ref<TenantConfig[]>([])
const configForm = ref({ key: '', value: '' })

const editionType = (e: string) => ({ BASIC: 'info', STANDARD: '', ENTERPRISE: 'warning' }[e] || 'info')
const editionLabel = (e: string) => ({ BASIC: '基础版', STANDARD: '标准版', ENTERPRISE: '企业版' }[e] || e)

async function loadData() {
  loading.value = true
  try {
    const res = await listTenants({ keyword: keyword.value || undefined, status: statusFilter.value, page: page.value, size: 20 })
    tenants.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

function openForm(row: Tenant | null) {
  if (row) {
    editingId.value = row.id
    form.value = { tenantCode: row.tenantCode, tenantName: row.tenantName, contactPerson: row.contactPerson, contactPhone: row.contactPhone, edition: row.edition, expireTime: row.expireTime || '', maxUsers: row.maxUsers, maxAssets: row.maxAssets, remark: row.remark || '' }
  } else {
    editingId.value = null
    form.value = { tenantCode: '', tenantName: '', contactPerson: '', contactPhone: '', edition: 'STANDARD', expireTime: '', maxUsers: 50, maxAssets: 10000, remark: '' }
  }
  showForm.value = true
}

async function doSave() {
  if (editingId.value) {
    await updateTenant(editingId.value, form.value as any)
  } else {
    await createTenant(form.value as any)
  }
  ElMessage.success('保存成功')
  showForm.value = false
  loadData()
}

async function doToggle(row: Tenant) {
  const newStatus = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确定${newStatus === 0 ? '禁用' : '启用'}该租户？`, '确认')
  await toggleTenantStatus(row.id, newStatus)
  ElMessage.success('操作成功')
  loadData()
}

async function openUsage(row: Tenant) {
  currentTenant.value = row
  const res = await getTenantUsage(row.id)
  usage.value = res.data
  showUsage.value = true
}

async function doRefreshUsage() {
  if (!currentTenant.value) return
  const res = await refreshTenantUsage(currentTenant.value.id)
  usage.value = res.data
  ElMessage.success('已刷新')
}

async function openConfig(row: Tenant) {
  currentTenant.value = row
  const res = await getTenantConfigs(row.id)
  configs.value = res.data || []
  showConfig.value = true
}

async function doSaveConfig() {
  if (!currentTenant.value) return
  await saveTenantConfig(currentTenant.value.id, configForm.value.key, configForm.value.value)
  ElMessage.success('配置已保存')
  configForm.value = { key: '', value: '' }
  const res = await getTenantConfigs(currentTenant.value.id)
  configs.value = res.data || []
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
