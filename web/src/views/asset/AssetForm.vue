<template>
  <div class="asset-form">
    <div class="page-header">
      <el-button @click="router.back()">返回</el-button>
    </div>

    <el-card shadow="never">
      <template #header>
        <span class="card-title">{{ isEdit ? '编辑资产' : '新增资产' }}</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 720px"
      >
        <el-form-item label="资产名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入资产名称" />
        </el-form-item>

        <el-form-item label="资产编号" prop="assetCode">
          <el-input v-model="form.assetCode" placeholder="请输入资产编号" :disabled="isEdit" />
        </el-form-item>

        <el-form-item label="资产类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择资产类别" style="width: 100%">
            <el-option label="土地" value="LAND" />
            <el-option label="房产" value="REAL_ESTATE" />
            <el-option label="设备" value="EQUIPMENT" />
            <el-option label="车辆" value="VEHICLE" />
            <el-option label="家具" value="FURNITURE" />
            <el-option label="IT设备" value="IT_EQUIPMENT" />
          </el-select>
        </el-form-item>

        <el-form-item label="原值" prop="originalValue">
          <el-input-number v-model="form.originalValue" :min="0" :precision="2" style="width: 100%" placeholder="请输入原值" />
        </el-form-item>

        <el-form-item label="折旧方法" prop="depreciationMethod">
          <el-select v-model="form.depreciationMethod" placeholder="请选择折旧方法" style="width: 100%">
            <el-option label="直线法" value="STRAIGHT_LINE" />
            <el-option label="双倍余额递减法" value="DOUBLE_DECLINING" />
            <el-option label="年限总和法" value="SUM_OF_YEARS" />
          </el-select>
        </el-form-item>

        <el-form-item label="使用年限(月)" prop="usefulLifeMonths">
          <el-input-number v-model="form.usefulLifeMonths" :min="1" :max="600" style="width: 100%" placeholder="请输入使用年限" />
        </el-form-item>

        <el-form-item label="使用状态" prop="useStatus">
          <el-select v-model="form.useStatus" placeholder="请选择使用状态" style="width: 100%">
            <el-option label="使用中" value="IN_USE" />
            <el-option label="闲置" value="IDLE" />
            <el-option label="已出租" value="RENTED" />
            <el-option label="已抵押" value="MORTGAGED" />
            <el-option label="已处置" value="DISPOSED" />
          </el-select>
        </el-form-item>

        <el-form-item label="所在地" prop="location">
          <el-input v-model="form.location" placeholder="请输入所在地" />
        </el-form-item>

        <el-form-item label="保管人" prop="custodian">
          <el-input v-model="form.custodian" placeholder="请输入保管人" />
        </el-form-item>

        <el-form-item label="购置日期" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="选择购置日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '创建资产' }}
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
import { getAsset, createAsset, updateAsset } from '@/api/asset'
import type { Asset, AssetCreateDTO } from '@/api/asset'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref<FormInstance>()
const submitting = ref(false)

interface AssetFormData {
  name: string
  assetCode: string
  category: string
  originalValue: number | undefined
  depreciationMethod: string
  usefulLifeMonths: number | undefined
  useStatus: string
  location: string
  custodian: string
  purchaseDate: string
  remark: string
}

const form = reactive<AssetFormData>({
  name: '',
  assetCode: '',
  category: '',
  originalValue: undefined,
  depreciationMethod: 'STRAIGHT_LINE',
  usefulLifeMonths: undefined,
  useStatus: 'IN_USE',
  location: '',
  custodian: '',
  purchaseDate: '',
  remark: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  assetCode: [{ required: true, message: '请输入资产编号', trigger: 'blur' }],
  category: [{ required: true, message: '请选择资产类别', trigger: 'change' }]
}

async function fetchAsset() {
  const id = route.params.id as string
  if (!id) return

  try {
    const response = await getAsset(id)
    const data = response.data
    if (data) {
      form.name = data.name
      form.assetCode = data.assetCode
      form.category = data.category
      form.originalValue = data.originalValue
      form.depreciationMethod = data.depreciationMethod || 'STRAIGHT_LINE'
      form.usefulLifeMonths = data.usefulLifeMonths
      form.useStatus = data.useStatus || 'IN_USE'
      form.location = data.location || ''
      form.custodian = data.custodian || ''
      form.purchaseDate = data.purchaseDate || ''
      form.remark = data.remark || ''
    }
  } catch {
    ElMessage.error('获取资产信息失败')
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
    const orgIdStr = localStorage.getItem('orgId')

    if (tenantIdStr === null || orgIdStr === null) {
      ElMessage.error('缺少组织信息，无法提交')
      submitting.value = false
      return
    }

    const tenantId = Number(tenantIdStr)
    const orgId = Number(orgIdStr)

    if (isEdit.value) {
      const id = route.params.id as string
      await updateAsset(id, { ...form } as Partial<Asset>)
      ElMessage.success('保存成功')
    } else {
      await createAsset({
        ...form,
        tenantId,
        orgId
      } as AssetCreateDTO)
      ElMessage.success('创建成功')
    }
    router.push('/assets')
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    fetchAsset()
  }
})
</script>

<style scoped>
.asset-form {
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
