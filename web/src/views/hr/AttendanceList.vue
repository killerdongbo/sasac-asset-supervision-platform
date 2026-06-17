<template>
  <div class="attendance-list">
    <div class="toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="员工ID">
          <el-input-number v-model="queryForm.employeeId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="年份">
          <el-input-number v-model="queryForm.year" :min="2020" :max="2030" controls-position="right" />
        </el-form-item>
        <el-form-item label="月份">
          <el-select v-model="queryForm.month" style="width: 100px">
            <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="showRecordDialog">录入考勤</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="attendances" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="employeeId" label="员工ID" width="80" />
      <el-table-column prop="attDate" label="日期" width="120" />
      <el-table-column prop="checkIn" label="签到时间" width="120" />
      <el-table-column prop="checkOut" label="签退时间" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- Record Dialog -->
    <el-dialog v-model="recordDialogVisible" title="录入考勤" width="400px">
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="100px">
        <el-form-item label="员工ID" prop="employeeId">
          <el-input-number v-model="recordForm.employeeId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="日期" prop="attDate">
          <el-date-picker
            v-model="recordForm.attDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="签到时间" prop="checkIn">
          <el-time-picker
            v-model="recordForm.checkIn"
            placeholder="选择时间"
            value-format="HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="签退时间" prop="checkOut">
          <el-time-picker
            v-model="recordForm.checkOut"
            placeholder="选择时间"
            value-format="HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecord" :loading="recording">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { queryAttendances, recordAttendance } from '@/api/hr'
import type { HrAttendance } from '@/api/hr'

const attendances = ref<HrAttendance[]>([])
const loading = ref(false)

const queryForm = reactive({
  employeeId: 1,
  year: new Date().getFullYear(),
  month: new Date().getMonth() + 1
})

const statusMap: Record<string, string> = {
  NORMAL: '正常',
  ABSENT: '缺勤',
  LATE: '迟到',
  EARLY: '早退'
}

function statusType(status: string): string {
  const map: Record<string, string> = {
    NORMAL: 'success',
    ABSENT: 'danger',
    LATE: 'warning',
    EARLY: 'warning'
  }
  return map[status] || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    if (tenantIdStr === null) return
    const response = await queryAttendances(
      queryForm.employeeId,
      Number(tenantIdStr),
      queryForm.year,
      queryForm.month
    )
    attendances.value = (response as any).data || []
  } catch {
    attendances.value = []
  } finally {
    loading.value = false
  }
}

// Record dialog
const recordDialogVisible = ref(false)
const recording = ref(false)
const recordFormRef = ref<FormInstance>()

const recordForm = reactive({
  employeeId: null as number | null,
  attDate: '',
  checkIn: '',
  checkOut: ''
})

const recordRules: FormRules = {
  employeeId: [{ required: true, message: '请输入员工ID', trigger: 'blur' }],
  attDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

function showRecordDialog() {
  recordDialogVisible.value = true
}

async function handleRecord() {
  if (!recordFormRef.value) return
  const valid = await recordFormRef.value.validate().catch(() => false)
  if (!valid) return

  recording.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    if (tenantIdStr === null) {
      ElMessage.error('缺少组织信息')
      recording.value = false
      return
    }
    await recordAttendance({
      employeeId: recordForm.employeeId!,
      tenantId: Number(tenantIdStr),
      attDate: recordForm.attDate,
      checkIn: recordForm.checkIn || undefined,
      checkOut: recordForm.checkOut || undefined
    })
    ElMessage.success('考勤记录已保存')
    recordDialogVisible.value = false
    fetchData()
  } catch {
    // Error handled by interceptor
  } finally {
    recording.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.attendance-list {
  padding: 0;
}

.toolbar {
  margin-bottom: 16px;
}
</style>
