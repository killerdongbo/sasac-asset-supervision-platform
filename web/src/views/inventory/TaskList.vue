<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyTasks, createTask } from '@/api/inventory'
import { ElMessage } from 'element-plus'

const router = useRouter()
const list = ref<any[]>([])
const loading = ref(false)
const tab = ref('all')
const dialogVisible = ref(false)
const form = ref({ taskName: '', assigneeId: null as number | null, scopeType: 'ORG', scopeValue: '', startDate: '', endDate: '' })

async function fetch() { loading.value = true; try { const res = await getMyTasks(); list.value = res.data || [] } finally { loading.value = false } }
async function handleCreate() { await createTask(form.value); ElMessage.success('盘点任务已创建'); dialogVisible.value = false; fetch() }

onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>盘点管理</h3><el-button type="primary" @click="dialogVisible = true">制定盘点计划</el-button></div>
    <el-tabs v-model="tab" @tab-change="fetch">
      <el-tab-pane label="全部任务" name="all" />
      <el-tab-pane label="我的盘点任务" name="my" />
    </el-tabs>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="taskName" label="任务名称" min-width="200" />
      <el-table-column prop="startDate" label="开始" width="120" />
      <el-table-column prop="endDate" label="截止" width="120" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'">{{ row.status === 'COMPLETED' ? '已完成' : '进行中' }}</el-tag></template></el-table-column>
      <el-table-column label="差异数" width="80"><template #default="{ row }"><el-badge :value="row.diffCount || 0" :type="row.diffCount > 0 ? 'danger' : 'success'" /></template></el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="router.push(`/inventory-tasks/${row.id}/execute`)">执行盘点</el-button>
          <el-button link type="warning" @click="router.push(`/inventory-diffs?taskId=${row.id}`)">查看差异</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="制定盘点计划" v-model="dialogVisible" width="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="任务名称"><el-input v-model="form.taskName" /></el-form-item>
        <el-form-item label="范围类型"><el-select v-model="form.scopeType"><el-option label="按部门" value="ORG" /><el-option label="按位置" value="LOCATION" /><el-option label="按分类" value="CATEGORY" /></el-select></el-form-item>
        <el-form-item label="范围值"><el-input v-model="form.scopeValue" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" /></el-form-item>
        <el-form-item label="截止日期"><el-date-picker v-model="form.endDate" type="date" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleCreate">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
