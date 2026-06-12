<template>
  <el-container class="app-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="app-aside">
      <div class="aside-logo">
        <span v-if="!isCollapse">🏛 国资监管平台</span>
        <span v-else>🏛</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          background-color="#001529"
          text-color="#ffffffb3"
          active-text-color="#409EFF"
          @select="handleSelect"
        >
          <el-sub-menu index="workbench">
            <template #title><el-icon><Odometer /></el-icon><span>工作台</span></template>
            <el-menu-item index="/"><el-icon><DataAnalysis /></el-icon><span>管理驾驶舱</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="basic">
            <template #title><el-icon><Collection /></el-icon><span>资产基础资料</span></template>
            <el-menu-item index="/categories"><span>资产分类</span></el-menu-item>
            <el-menu-item index="/locations"><span>存放位置</span></el-menu-item>
            <el-menu-item index="/suppliers"><span>供应商档案</span></el-menu-item>
            <el-menu-item index="/maintenance-providers"><span>维保商档案</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="ledger">
            <template #title><el-icon><Notebook /></el-icon><span>资产台账</span></template>
            <el-menu-item index="/assets"><span>资产清单</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="circulation">
            <template #title><el-icon><Refresh /></el-icon><span>资产流转</span></template>
            <el-menu-item index="/stock-ins"><span>入库管理</span></el-menu-item>
            <el-menu-item index="/assignments"><span>资产领用</span></el-menu-item>
            <el-menu-item index="/transfers"><span>资产调拨</span></el-menu-item>
            <el-menu-item index="/disposals"><span>资产报废</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="procurement">
            <template #title><el-icon><ShoppingCart /></el-icon><span>采购管理</span></template>
            <el-menu-item index="/purchase-requests"><span>采购申请</span></el-menu-item>
            <el-menu-item index="/purchase-acceptances"><span>采购验收</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="field">
            <template #title><el-icon><Monitor /></el-icon><span>现场管理</span></template>
            <el-menu-item index="/inspection-tasks"><span>巡检管理</span></el-menu-item>
            <el-menu-item index="/inventory-tasks"><span>盘点管理</span></el-menu-item>
            <el-menu-item index="/maintenance-requests"><span>维保维修</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="depreciation-group">
            <template #title><el-icon><Money /></el-icon><span>财务折旧</span></template>
            <el-menu-item index="/depreciation"><span>折旧管理</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="approval">
            <template #title><el-icon><DocumentChecked /></el-icon><span>审批工作流</span></template>
            <el-menu-item index="/approval/pending"><span>待我审批</span></el-menu-item>
            <el-menu-item index="/approval/my-requests"><span>我发起的</span></el-menu-item>
            <el-menu-item index="/approval/history"><span>审批历史</span></el-menu-item>
            <el-menu-item index="/approval/defs"><span>审批配置</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="supervision">
            <template #title><el-icon><DataLine /></el-icon><span>监管上报</span></template>
            <el-menu-item index="/reports"><span>报表管理</span></el-menu-item>
            <el-menu-item index="/statistics/assets"><span>资产统计</span></el-menu-item>
            <el-menu-item index="/statistics/depreciation"><span>折旧统计</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="warn-audit">
            <template #title><el-icon><Warning /></el-icon><span>预警与审计</span></template>
            <el-menu-item index="/alerts"><span>预警中心</span></el-menu-item>
            <el-menu-item index="/audit-logs"><span>操作日志</span></el-menu-item>
            <el-menu-item index="/audit-lifecycle"><span>资产追踪</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title><el-icon><Setting /></el-icon><span>系统管理</span></template>
            <el-menu-item index="/organizations"><span>组织架构</span></el-menu-item>
            <el-menu-item index="/users"><span>用户管理</span></el-menu-item>
            <el-menu-item index="/roles"><span>角色管理</span></el-menu-item>
            <el-menu-item index="/dicts"><span>数据字典</span></el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="header-left">
          <el-button :icon="isCollapse ? 'Expand' : 'Fold'" text @click="isCollapse = !isCollapse" />
          <span class="header-title">湛江市国资国企一体化数字平台</span>
        </div>
        <div class="header-right">
          <el-badge :value="alertCount" :hidden="alertCount === 0">
            <el-icon :size="20" style="cursor:pointer" @click="$router.push('/alerts')"><Bell /></el-icon>
          </el-badge>
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="28" icon="UserFilled" />
              <span>{{ userStore.userInfo?.realName || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const alertCount = ref(5)

const activeMenu = computed(() => route.path)

function handleSelect(index: string) {
  router.push(index)
}

function handleLogout() {
  userStore.logout()
}
</script>

<style scoped>
.app-container { height: 100vh; }
.app-aside { background-color: #001529; overflow: hidden; transition: width 0.3s; }
.aside-logo {
  height: 60px; line-height: 60px; text-align: center;
  font-size: 17px; font-weight: 700; color: #fff;
  border-bottom: 1px solid rgba(255,255,255,0.1); white-space: nowrap;
}
.app-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 20px; background: #fff; border-bottom: 1px solid #f0f0f0; height: 56px;
}
.header-left { display: flex; align-items: center; gap: 8px; }
.header-title { font-size: 15px; font-weight: 600; color: #333; }
.header-right { display: flex; align-items: center; gap: 20px; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; }
.app-main { background: #f0f2f5; min-height: calc(100vh - 56px); padding: 20px; }
:deep(.el-menu) { border-right: none; }
:deep(.el-menu-item.is-active) { background-color: #1890ff !important; }
</style>
