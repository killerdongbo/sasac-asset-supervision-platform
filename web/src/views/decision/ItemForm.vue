<template>
  <div class="decision-item-form">
    <div class="page-header">
      <h2>新增决策事项</h2>
    </div>

    <el-card shadow="never">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="事项类型" prop="itemType">
          <el-select v-model="form.itemType" placeholder="请选择事项类型" style="width: 300px">
            <el-option label="重大决策" value="重大决策" />
            <el-option label="重要人事任免" value="重要人事任免" />
            <el-option label="重大项目安排" value="重大项目安排" />
            <el-option label="大额资金使用" value="大额资金使用" />
          </el-select>
        </el-form-item>

        <el-form-item label="事项标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入事项标题" maxlength="200" show-word-limit style="width: 600px" />
        </el-form-item>

        <el-form-item label="事项描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请描述事项内容" style="width: 600px" />
        </el-form-item>

        <el-form-item label="涉及金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" placeholder="金额（元）" style="width: 300px" />
        </el-form-item>

        <el-form-item label="提议人" prop="proposerName">
          <el-input v-model="form.proposerName" placeholder="请输入提议人姓名" style="width: 300px" />
        </el-form-item>

        <el-form-item label="部门" prop="department">
          <el-input v-model="form.department" placeholder="请输入部门名称" style="width: 300px" />
        </el-form-item>

        <el-form-item label="紧急程度" prop="urgency">
          <el-select v-model="form.urgency" placeholder="请选择紧急程度" style="width: 300px">
            <el-option label="普通" value="NORMAL" />
            <el-option label="加急" value="URGENT" />
            <el-option label="紧急" value="CRITICAL" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { submitDecisionItem } from '@/api/decision'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)

const form = reactive({
  tenantId: Number(localStorage.getItem('tenantId') || 1),
  orgId: Number(localStorage.getItem('orgId') || 0),
  itemType: '',
  title: '',
  description: '',
  amount: undefined as number | undefined,
  proposerName: '',
  department: '',
  urgency: 'NORMAL',
})

const rules = {
  itemType: [{ required: true, message: '请选择事项类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入事项标题', trigger: 'blur' }],
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await submitDecisionItem(form)
    ElMessage.success('事项提交成功')
    router.push({ name: 'DecisionItemList' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.decision-item-form {
  padding: 16px;
}
.page-header {
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
</style>
