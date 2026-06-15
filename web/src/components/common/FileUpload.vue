<template>
  <div class="file-upload">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="headers"
      :data="uploadData"
      :file-list="fileList"
      :before-upload="beforeUpload"
      :on-success="onSuccess"
      :on-error="onError"
      :on-remove="onRemove"
      :on-preview="onPreview"
      :limit="limit"
      :multiple="true"
      :accept="accept"
      drag
      list-type="picture-card"
    >
      <el-icon><UploadFilled /></el-icon>
      <div class="upload-text">拖拽文件到此处或<em>点击上传</em></div>
      <template #tip>
        <div class="upload-tip">支持 {{accept || '所有格式'}}，单文件最大{{maxSizeMb}}MB</div>
      </template>
    </el-upload>

    <el-dialog v-model="previewVisible" title="预览" width="80%">
      <img v-if="previewUrl" :src="previewUrl" style="max-width:100%;max-height:70vh" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadInstance, UploadFile } from 'element-plus'
import { getPreviewUrl, deleteFile } from '@/api/file'

const props = defineProps<{
  bizType?: string; bizId?: number; limit?: number
  accept?: string; maxSizeMb?: number
  modelValue?: any[]
}>()
const emit = defineEmits(['update:modelValue', 'change'])

const uploadRef = ref<UploadInstance>()
const uploadUrl = '/api/files/upload'
const headers = { 'X-Tenant-Id': localStorage.getItem('tenantId') || '0' }
const uploadData = { bizType: props.bizType || 'general', bizId: props.bizId }
const previewVisible = ref(false); const previewUrl = ref('')
const fileList = ref<any[]>([])

const beforeUpload = (file: File) => {
  const max = (props.maxSizeMb || 50) * 1024 * 1024
  if (file.size > max) { ElMessage.error(`文件大小不能超过${props.maxSizeMb||50}MB`); return false }
  return true
}

const onSuccess = (resp: any, _file: UploadFile) => {
  if (resp?.data?.id) {
    fileList.value.push({ id: resp.data.id, name: resp.data.originalName, url: resp.data.objectPath })
    emit('change', fileList.value)
  }
}

const onError = () => { ElMessage.error('上传失败') }

const onRemove = async (file: UploadFile) => {
  const fid = (file as any).id || fileList.value.find(f => f.name === file.name)?.id
  if (fid) {
    try { await deleteFile(fid) } catch {}
    fileList.value = fileList.value.filter(f => f.id !== fid)
    emit('change', fileList.value)
  }
}

const onPreview = async (file: UploadFile) => {
  const fid = (file as any).id || fileList.value.find(f => f.name === file.name)?.id
  if (fid) {
    const r = await getPreviewUrl(fid); previewUrl.value = r.data.data; previewVisible.value = true
  }
}
</script>

<style scoped>
.file-upload { width: 100%; }
.upload-text { font-size: 14px; color: #999; margin-top: 8px; }
.upload-tip { font-size: 12px; color: #ccc; margin-top: 4px; }
</style>
