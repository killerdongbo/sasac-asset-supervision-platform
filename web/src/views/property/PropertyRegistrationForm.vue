<template>
  <div class="pr-registration-form">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
    </div>

    <el-card shadow="never">
      <template #header>
        <span class="card-title">新增产权登记</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        style="max-width: 960px"
      >
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="登记类型" prop="regType">
              <el-select v-model="form.regType" placeholder="请选择登记类型" clearable style="width: 100%">
                <el-option label="占有登记" value="POSSESSION" />
                <el-option label="变动登记" value="CHANGE" />
                <el-option label="注销登记" value="CANCEL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="企业名称" prop="enterpriseName">
              <el-input v-model="form.enterpriseName" placeholder="请输入企业名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="产权类型" prop="propertyType">
              <el-select v-model="form.propertyType" placeholder="请选择产权类型" clearable style="width: 100%">
                <el-option label="国有独资" value="STATE_OWNED" />
                <el-option label="国有控股" value="STATE_CONTROLLED" />
                <el-option label="国有参股" value="STATE_EQUITY" />
                <el-option label="集体企业" value="COLLECTIVE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="产权持有单位" prop="propertyOwner">
              <el-input v-model="form.propertyOwner" placeholder="请输入产权持有单位" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="股权比例(%)" prop="equityPct">
              <el-input-number v-model="form.equityPct" :min="0" :max="100" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="注册资本(万元)" prop="registeredCapital">
              <el-input-number v-model="form.registeredCapital" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="实收资本(万元)" prop="paidCapital">
              <el-input-number v-model="form.paidCapital" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="登记日期" prop="registrationDate">
              <el-date-picker
                v-model="form.registrationDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="产权证号" prop="certNo">
              <el-input v-model="form.certNo" placeholder="请输入产权证号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            创建登记
          </el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createRegistration } from '@/api/property'
import type { RegistrationDTO } from '@/api/property'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

interface RegistrationFormData {
  regType: string
  enterpriseName: string
  propertyType: string
  propertyOwner: string
  equityPct: number | undefined
  registeredCapital: number | undefined
  paidCapital: number | undefined
  registrationDate: string
  certNo: string
}

const form = reactive<RegistrationFormData>({
  regType: '',
  enterpriseName: '',
  propertyType: '',
  propertyOwner: '',
  equityPct: undefined,
  registeredCapital: undefined,
  paidCapital: undefined,
  registrationDate: '',
  certNo: ''
})

const rules: FormRules = {
  regType: [{ required: true, message: '请选择登记类型', trigger: 'change' }],
  enterpriseName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }]
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const tenantId = Number(localStorage.getItem('tenantId')) || 0
    const orgId = Number(localStorage.getItem('orgId')) || 0

    await createRegistration({
      ...form,
      tenantId,
      orgId
    } as RegistrationDTO)
    ElMessage.success('创建成功')
    router.push('/property/registrations')
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.pr-registration-form {
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
