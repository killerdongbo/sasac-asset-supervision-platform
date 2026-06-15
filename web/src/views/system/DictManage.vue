<template>
  <div class="dict-page">
    <div class="page-header"><h2>数据字典</h2><el-button type="primary" @click="openTypeForm">新建字典类型</el-button></div>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card header="字典类型">
          <div v-for="t in types" :key="t.id" class="type-item" :class="{active: currentType===t.dictCode}" @click="selectType(t.dictCode)">
            <span class="type-name">{{t.dictName}}</span>
            <span class="type-code">{{t.dictCode}}</span>
            <el-button link type="danger" size="small" @click.stop="doDeleteType(t)">删除</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span>字典项 - {{currentType || '请选择类型'}}</span>
              <el-button type="primary" size="small" @click="openItemForm(null)" :disabled="!currentType">新增条目</el-button>
            </div>
          </template>
          <el-table :data="items" stripe size="small">
            <el-table-column prop="itemKey" label="键" width="160"/>
            <el-table-column prop="itemValue" label="值" min-width="200"/>
            <el-table-column prop="sortOrder" label="排序" width="80" align="center"/>
            <el-table-column prop="remark" label="备注" width="150"/>
            <el-table-column label="操作" width="120">
              <template #default="{row}">
                <el-button link type="primary" size="small" @click="openItemForm(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="doDeleteItem(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 字典类型表单 -->
    <el-dialog v-model="showTypeForm" title="新建字典类型" width="400px">
      <el-form :model="typeForm" label-width="90px">
        <el-form-item label="编码"><el-input v-model="typeForm.dictCode"/></el-form-item>
        <el-form-item label="名称"><el-input v-model="typeForm.dictName"/></el-form-item>
        <el-form-item label="备注"><el-input v-model="typeForm.remark"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="showTypeForm=false">取消</el-button><el-button type="primary" @click="doCreateType">保存</el-button></template>
    </el-dialog>

    <!-- 字典项表单 -->
    <el-dialog v-model="showItemForm" :title="editingItemId?'编辑条目':'新增条目'" width="420px">
      <el-form :model="itemForm" label-width="80px">
        <el-form-item label="键"><el-input v-model="itemForm.itemKey"/></el-form-item>
        <el-form-item label="值"><el-input v-model="itemForm.itemValue"/></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="itemForm.sortOrder" :min="0"/></el-form-item>
        <el-form-item label="备注"><el-input v-model="itemForm.remark"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="showItemForm=false">取消</el-button><el-button type="primary" @click="doSaveItem">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDictTypes, createDictType, deleteDictType, listDictItems, createDictItem, updateDictItem, deleteDictItem } from '@/api/dict'
import type { DictType, DictItem } from '@/api/dict'

const types = ref<DictType[]>([]); const currentType = ref('')
const items = ref<DictItem[]>([])

const showTypeForm = ref(false)
const typeForm = ref({ dictCode:'', dictName:'', remark:'' })
function openTypeForm() { showTypeForm.value = true }

const showItemForm = ref(false); const editingItemId = ref<string|null>(null)
const itemForm = ref<DictItem>({ dictCode:'', itemKey:'', itemValue:'', sortOrder:0, remark:'' })

async function loadTypes() { const r = await listDictTypes(); types.value = r.data || [] }
async function selectType(code: string) { currentType.value = code; const r = await listDictItems(code); items.value = r.data || [] }

async function doCreateType() {
  await createDictType(typeForm.value); ElMessage.success('已创建')
  showTypeForm.value = false; typeForm.value = { dictCode:'', dictName:'', remark:'' }; loadTypes()
}

async function doDeleteType(t: DictType) {
  await ElMessageBox.confirm('确定删除？','确认'); await deleteDictType(t.id!); ElMessage.success('已删除'); loadTypes()
}

function openItemForm(row: DictItem|null) {
  if (row && row.id) { editingItemId.value = row.id; itemForm.value = { ...row } }
  else { editingItemId.value = null; itemForm.value = { dictCode:currentType.value, itemKey:'', itemValue:'', sortOrder:0, remark:'' } }
  showItemForm.value = true
}

async function doSaveItem() {
  if (editingItemId.value) await updateDictItem(editingItemId.value, itemForm.value)
  else await createDictItem(itemForm.value)
  ElMessage.success('已保存'); showItemForm.value = false
  if (currentType.value) selectType(currentType.value)
}

async function doDeleteItem(item: DictItem) {
  await ElMessageBox.confirm('确定删除？','确认'); await deleteDictItem(item.id!); ElMessage.success('已删除')
  if (currentType.value) selectType(currentType.value)
}

onMounted(loadTypes)
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.type-item { display:flex; align-items:center; padding:10px 12px; border-bottom:1px solid #f0f0f0; cursor:pointer; border-radius:4px; }
.type-item:hover { background:#f5f5f5; }
.type-item.active { background:#ecf5ff; border-left:3px solid #409EFF; }
.type-name { flex:1; font-weight:500; }
.type-code { color:#999; font-size:12px; margin-right:8px; }
</style>
