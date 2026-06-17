<template>
  <div class="contract-form">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
    </div>

    <el-card shadow="never">
      <template #header>
        <span class="card-title">{{ isEdit ? '编辑合同' : '新增合同' }}</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 960px"
      >
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="人员ID" prop="employeeId" v-if="!route.query.employeeId">
              <el-input-number v-model="form.employeeId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合同类型" prop="contractType">
              <el-select v-model="form.contractType" placeholder="请选择合同类型" style="width: 100%">
                <el-option label="劳动合同" value="LABOR" />
                <el-option label="实习合同" value="INTERNSHIP" />
                <el-option label="项目合同" value="PROJECT" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="签订日期" prop="signDate">
              <el-date-picker
                v-model="form.signDate"
                type="date"
                placeholder="选择签订日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="form.endDate"
                type="date"
                placeholder="选择结束日期"
                value-format="YYYY-MM-DD"
                :disabled="form.isUnlimited"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="无固定期限">
              <el-switch v-model="form.isUnlimited" @change="handleUnlimitedChange" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="条款摘要" prop="termsSummary">
          <el-input
            v-model="form.termsSummary"
            type="textarea"
            :rows="4"
            placeholder="请输入合同条款摘要"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '创建合同' }}
          </el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getContract, createContract, updateContract } from '@/api/hr'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref<FormInstance>()
const submitting = ref(false)

interface ContractFormData {
  employeeId: number | null
  contractNo: string
  contractType: string
  signDate: string
  startDate: string
  endDate: string
  isUnlimited: boolean
  termsSummary: string
}

const form = reactive<ContractFormData>({
  employeeId: route.query.employeeId ? Number(route.query.employeeId) : null,
  contractNo: '',
  contractType: 'LABOR',
  signDate: '',
  startDate: '',
  endDate: '',
  isUnlimited: false,
  termsSummary: ''
})

const rules: FormRules = {
  contractNo: [{ required: true, message: '请输入合同编号', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }]
}

function handleUnlimitedChange(val: boolean) {
  if (val) {
    form.endDate = ''
  }
}

async function fetchContract() {
  const id = route.params.id as string
  if (!id) return

  try {
    const response = await getContract(id)
    const data = response.data
    if (data) {
      form.employeeId = data.employeeId
      form.contractNo = data.contractNo || ''
      form.contractType = data.contractType || 'LABOR'
      form.signDate = data.signDate || ''
      form.startDate = data.startDate || ''
      form.endDate = data.endDate || ''
      form.isUnlimited = data.isUnlimited || false
      form.termsSummary = data.termsSummary || ''
    }
  } catch {
    ElMessage.error('获取合同信息失败')
    router.back()
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const tenantIdStr = localStorage.getItem('tenantId')
    if (tenantIdStr === null) {
      ElMessage.error('缺少组织信息，无法提交')
      submitting.value = false
      return
    }
    const tenantId = Number(tenantIdStr)

    const payload = {
      ...form,
      tenantId,
      employeeId: route.query.employeeId ? Number(route.query.employeeId) : form.employeeId
    }

    if (isEdit.value) {
      const id = route.params.id as string
      await updateContract(id, payload)
      ElMessage.success('保存成功')
    } else {
      await createContract(payload)
      ElMessage.success('创建成功')
    }
    router.push('/hr/contracts')
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    fetchContract()
  }
})
</script>

<style scoped>
.contract-form {
  padding: 0;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
