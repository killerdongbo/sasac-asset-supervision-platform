<template>
  <div class="notif-page">
    <div class="page-header"><h2>消息通知</h2><el-button @click="doMarkAll">全部已读</el-button></div>
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="load">
        <el-tab-pane label="未读" name="0"/>
        <el-tab-pane label="全部" name="all"/>
      </el-tabs>
      <div class="notif-list">
        <div v-if="notifs.length===0" style="text-align:center;color:#999;padding:40px">暂无消息</div>
        <div v-for="n in notifs" :key="n.id" class="notif-card" :class="{unread: n.isRead===0}" @click="doRead(n)">
          <div class="n-title">
            <el-tag size="small" :type="n.level==='CRITICAL'?'danger':n.level==='WARNING'?'warning':'info'" style="margin-right:8px">{{n.type}}</el-tag>
            {{n.title}}
          </div>
          <div class="n-content">{{n.content}}</div>
          <div class="n-footer">
            <span class="n-time">{{n.createdAt}}</span>
            <span v-if="n.isRead===0" class="n-dot">●</span>
          </div>
        </div>
      </div>
      <el-pagination v-model:current-page="page" :page-size="20" :total="total" layout="total,prev,pager,next" @current-change="load" style="margin-top:16px;justify-content:flex-end"/>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { listNotifications, markRead, markAllRead } from '@/api/notification'
import type { Notification } from '@/api/notification'

const userStore = useUserStore()
const notifs = ref<Notification[]>([])
const activeTab = ref('0')
const page = ref(1); const total = ref(0)

async function load() {
  const userId = userStore.userInfo?.userId
  if (!userId) return
  const isRead = activeTab.value === 'all' ? undefined : 0
  const r = await listNotifications({ userId, isRead, page: page.value, size: 20 })
  notifs.value = r.data.records || []
  total.value = r.data.total || 0
}

async function doRead(n: Notification) {
  if (n.isRead === 1) return
  const userId = userStore.userInfo?.userId
  if (!userId) return
  await markRead(n.id, userId)
  load()
}

async function doMarkAll() {
  const userId = userStore.userInfo?.userId
  if (!userId) return
  await markAllRead(userId)
  ElMessage.success('已全部标记为已读')
  load()
}

onMounted(load)
</script>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.notif-list { display:flex; flex-direction:column; gap:8px; }
.notif-card { padding:12px; border:1px solid #eee; border-radius:8px; cursor:pointer; }
.notif-card.unread { border-left:3px solid #409EFF; background:#f9fafc; }
.notif-card:hover { background:#f0f5ff; }
.n-title { font-weight:500; margin-bottom:6px; }
.n-content { font-size:13px; color:#666; margin-bottom:6px; }
.n-footer { display:flex; justify-content:space-between; align-items:center; font-size:12px; color:#999; }
.n-dot { color:#409EFF; }
</style>
