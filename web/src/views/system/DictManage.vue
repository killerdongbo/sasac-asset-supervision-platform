<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDicts, saveDict } from '@/api/system'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([]); const loading = ref(false); const dialogVisible = ref(false)
const form = ref({ dictType: '', dictCode: '', dictValue: '', sortOrder: 0 })

async function fetch() { loading.value = true; try { const res = await getDicts(); list.value = res.data } finally { loading.value = false } }
async function handleCreate() { await saveDict(form.value); ElMessage.success('字典项创建成功'); dialogVisible.value = false; fetch() }
onMounted(fetch)
</script>

<template>
  <div class="page">
    <div class="page-header"><h3>数据字典</h3><el-button type="primary" @click="dialogVisible = true">新增字典项</el-button></div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="dictType" label="字典类型" width="180" />
      <el-table-column prop="dictCode" label="编码" width="160" />
      <el-table-column prop="dictValue" label="值" min-width="200" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
    </el-table>
    <el-dialog title="新增字典项" v-model="dialogVisible" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型"><el-input v-model="form.dictType" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.dictCode" /></el-form-item>
        <el-form-item label="值"><el-input v-model="form.dictValue" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleCreate">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page { background: #fff; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { margin: 0; }
</style>
