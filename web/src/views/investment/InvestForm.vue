<template>
  <div class="invest-form">
    <el-card shadow="never">
      <template #header>
        <span>{{ isEdit ? '编辑投资项目' : '新增投资项目' }}</span>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" style="max-width: 720px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>

        <el-form-item label="投资类型" prop="investType">
          <el-select v-model="form.investType" placeholder="请选择投资类型" style="width: 100%">
            <el-option v-for="(label, key) in investTypeMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属行业">
          <el-input v-model="form.industry" placeholder="请输入所属行业" />
        </el-form-item>

        <el-form-item label="所在地区">
          <el-input v-model="form.region" placeholder="请输入所在地区" />
        </el-form-item>

        <el-form-item label="投资金额" prop="investAmount">
          <el-input-number v-model="form.investAmount" :min="0" :precision="2" style="width: 100%" placeholder="请输入投资金额" />
        </el-form-item>

        <el-form-item label="股权比例">
          <el-input-number v-model="form.equityPct" :min="0" :max="100" :precision="2" style="width: 100%">
            <template #suffix>%</template>
          </el-input-number>
        </el-form-item>

        <el-form-item label="标的企业" prop="targetCompany">
          <el-input v-model="form.targetCompany" placeholder="请输入标的企业名称" />
        </el-form-item>

        <el-form-item label="标的企业描述">
          <el-input v-model="form.targetDescription" type="textarea" :rows="3" placeholder="请输入标的企业描述" />
        </el-form-item>

        <el-form-item label="预期收益率">
          <el-input-number v-model="form.expectedRoi" :min="0" :max="100" :precision="2" style="width: 100%">
            <template #suffix>%</template>
          </el-input-number>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createInvestmentProject, getInvestmentProject, updateInvestmentProject } from '@/api/investment'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const route = useRoute()
const isEdit = !!route.params.id
const formRef = ref<FormInstance>()
const submitting = ref(false)

const investTypeMap: Record<string, string> = {
  EQUITY: '股权投资',
  DEBT: '债权投资',
  FUND: '基金投资',
  OTHER: '其他'
}

const form = reactive({
  projectName: '',
  investType: '',
  industry: '',
  region: '',
  investAmount: undefined as number | undefined,
  equityPct: undefined as number | undefined,
  targetCompany: '',
  targetDescription: '',
  expectedRoi: undefined as number | undefined
})

const rules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  investType: [{ required: true, message: '请选择投资类型', trigger: 'change' }],
  investAmount: [{ required: true, message: '请输入投资金额', trigger: 'blur' }],
  targetCompany: [{ required: true, message: '请输入标的企业', trigger: 'blur' }]
}

async function loadProject() {
  if (!isEdit) return
  try {
    const res = await getInvestmentProject(route.params.id as string)
    const data = (res as any).data
    Object.assign(form, {
      projectName: data.projectName,
      investType: data.investType,
      industry: data.industry,
      region: data.region,
      investAmount: data.investAmount,
      equityPct: data.equityPct,
      targetCompany: data.targetCompany,
      targetDescription: data.targetDescription,
      expectedRoi: data.expectedRoi
    })
  } catch {
    ElMessage.error('加载项目信息失败')
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit) {
      await updateInvestmentProject(route.params.id as string, form as any)
      ElMessage.success('更新成功')
    } else {
      await createInvestmentProject({
        ...form,
        orgId: 1,
        tenantId: 1
      } as any)
      ElMessage.success('创建成功')
    }
    router.push('/investments')
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadProject()
})
</script>

<style scoped>
.invest-form {
  padding: 0;
}
</style>
