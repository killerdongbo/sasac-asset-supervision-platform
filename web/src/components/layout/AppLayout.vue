<template>
  <el-container class="app-container">
    <!-- PC端侧边栏 -->
    <el-aside v-if="!isMobile" :width="isCollapse ? '64px' : '240px'" class="app-aside">
      <div class="aside-logo">
        <span v-if="!isCollapse">🏛 国资监管平台</span>
        <span v-else>🏛</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          background-color="#001529"
          text-color="rgba(255,255,255,0.85)"
          active-text-color="#00d4ff"
          @select="handleSelect"
        >

          <!-- ========== 国资监管域 ========== -->
          <div class="menu-domain" v-if="!isCollapse">
            <span class="domain-tag domain-supervision">国资监管域</span>
          </div>

          <el-menu-item index="/">
            <el-icon><Odometer /></el-icon><span>管理驾驶舱</span>
          </el-menu-item>

          <el-sub-menu index="finance">
            <template #title><el-icon><DataLine /></el-icon><span>财务监督</span></template>
            <el-menu-item index="/finance/reports"><span>财务快报</span></el-menu-item>
            <el-menu-item index="/finance/indicators"><span>财务指标</span></el-menu-item>
            <el-menu-item index="/finance/funds"><span>资金监控</span></el-menu-item>
            <el-menu-item index="/finance/budgets"><span>预算执行</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="property">
            <template #title><el-icon><CollectionTag /></el-icon><span>产权管理</span></template>
            <el-menu-item index="/property/registrations"><span>产权登记</span></el-menu-item>
            <el-menu-item index="/property/tree"><span>产权树</span></el-menu-item>
            <el-menu-item index="/property/transactions"><span>交易监测</span></el-menu-item>
            <el-menu-item index="/property/assessments"><span>资产评估</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="decision">
            <template #title><el-icon><DocumentChecked /></el-icon><span>三重一大</span></template>
            <el-menu-item index="/decision/items"><span>事项申报</span></el-menu-item>
            <el-menu-item index="/decision/supervisions"><span>决议督办</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="performance">
            <template #title><el-icon><TrendCharts /></el-icon><span>业绩考核与薪酬</span></template>
            <el-menu-item index="/performance/indicators"><span>考核指标</span></el-menu-item>
            <el-menu-item index="/performance/scoreboard"><span>评分看板</span></el-menu-item>
            <el-menu-item index="/performance/salary-budgets"><span>薪酬管控</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="supervision-accountability">
            <template #title><el-icon><WarningFilled /></el-icon><span>监督追责</span></template>
            <el-menu-item index="/supervision/audit-plans"><span>审计计划</span></el-menu-item>
            <el-menu-item index="/supervision/findings"><span>问题看板</span></el-menu-item>
            <el-menu-item index="/supervision/rectifications"><span>整改跟踪</span></el-menu-item>
            <el-menu-item index="/supervision/cases"><span>违规追责</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="majorevent">
            <template #title><el-icon><FolderChecked /></el-icon><span>重大事项</span></template>
            <el-menu-item index="/major-events"><span>事项列表</span></el-menu-item>
            <el-menu-item index="/major-events/lawsuits-guarantees"><span>诉讼担保</span></el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/reports">
            <el-icon><Files /></el-icon><span>监管上报</span>
          </el-menu-item>

          <!-- ========== 企业资产管理域 ========== -->
          <div class="menu-domain" v-if="!isCollapse" style="margin-top:8px">
            <span class="domain-tag domain-asset">企业资产管理域</span>
          </div>

          <el-sub-menu index="basic">
            <template #title><el-icon><Collection /></el-icon><span>资产基础资料</span></template>
            <el-menu-item index="/categories"><span>资产分类</span></el-menu-item>
            <el-menu-item index="/locations"><span>存放位置</span></el-menu-item>
            <el-menu-item index="/suppliers"><span>供应商档案</span></el-menu-item>
            <el-menu-item index="/maintenance-providers"><span>维保商档案</span></el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/assets">
            <el-icon><Notebook /></el-icon><span>资产台账</span>
          </el-menu-item>

          <el-sub-menu index="circulation">
            <template #title><el-icon><Refresh /></el-icon><span>资产流转</span></template>
            <el-menu-item index="/stock-ins"><span>入库管理</span></el-menu-item>
            <el-menu-item index="/assignments"><span>资产领用</span></el-menu-item>
            <el-menu-item index="/transfers"><span>资产调拨</span></el-menu-item>
            <el-menu-item index="/disposals"><span>资产报废</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="field">
            <template #title><el-icon><Monitor /></el-icon><span>现场管理</span></template>
            <el-menu-item index="/inspection-tasks"><span>巡检管理</span></el-menu-item>
            <el-menu-item index="/inventory-tasks"><span>盘点管理</span></el-menu-item>
            <el-menu-item index="/maintenance-requests"><span>维保维修</span></el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/depreciation">
            <el-icon><Money /></el-icon><span>财务折旧</span>
          </el-menu-item>

          <el-menu-item index="/projects">
            <el-icon><List /></el-icon><span>项目管理</span>
          </el-menu-item>

          <el-sub-menu index="investment">
            <template #title><el-icon><PieChart /></el-icon><span>投资管理</span></template>
            <el-menu-item index="/investments"><span>投资组合</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="warn-audit">
            <template #title><el-icon><Bell /></el-icon><span>预警与审计</span></template>
            <el-menu-item index="/alerts"><span>预警中心</span></el-menu-item>
            <el-menu-item index="/audit-logs"><span>操作日志</span></el-menu-item>
            <el-menu-item index="/audit-lifecycle"><span>资产追踪</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="statistics-group">
            <template #title><el-icon><DataAnalysis /></el-icon><span>统计报表</span></template>
            <el-menu-item index="/statistics/group"><span>集团穿透</span></el-menu-item>
            <el-menu-item index="/statistics/asset-summary"><span>资产汇总</span></el-menu-item>
            <el-menu-item index="/statistics/category"><span>分类统计</span></el-menu-item>
            <el-menu-item index="/statistics/disposal"><span>处置统计</span></el-menu-item>
            <el-menu-item index="/statistics/depreciation-report"><span>折旧分析</span></el-menu-item>
            <el-menu-item index="/statistics/idle"><span>闲置分析</span></el-menu-item>
            <el-menu-item index="/statistics/rental"><span>出租分析</span></el-menu-item>
            <el-menu-item index="/exports"><span>报表导出</span></el-menu-item>
          </el-sub-menu>

          <!-- ========== 平台基础域 ========== -->
          <div class="menu-domain" v-if="!isCollapse" style="margin-top:8px">
            <span class="domain-tag domain-platform">平台基础域</span>
          </div>

          <el-sub-menu index="hr">
            <template #title><el-icon><User /></el-icon><span>人力资源</span></template>
            <el-menu-item index="/hr/employees"><span>人员档案</span></el-menu-item>
            <el-menu-item index="/hr/contracts"><span>合同管理</span></el-menu-item>
            <el-menu-item index="/hr/changes"><span>异动记录</span></el-menu-item>
            <el-menu-item index="/hr/salaries"><span>薪酬管理</span></el-menu-item>
            <el-menu-item index="/hr/attendances"><span>考勤管理</span></el-menu-item>
            <el-menu-item index="/hr/performances"><span>绩效管理</span></el-menu-item>
            <el-menu-item index="/hr/recruitments"><span>招聘管理</span></el-menu-item>
            <el-menu-item index="/hr/trainings"><span>培训管理</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="approval">
            <template #title><el-icon><Select /></el-icon><span>审批工作流</span></template>
            <el-menu-item index="/approval/pending"><span>待我审批</span></el-menu-item>
            <el-menu-item index="/approval/my-requests"><span>我发起的</span></el-menu-item>
            <el-menu-item index="/approval/history"><span>审批历史</span></el-menu-item>
            <el-menu-item index="/approval/defs"><span>审批配置</span></el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title><el-icon><Setting /></el-icon><span>系统管理</span></template>
            <el-menu-item index="/organizations"><span>组织架构</span></el-menu-item>
            <el-menu-item index="/users"><span>用户管理</span></el-menu-item>
            <el-menu-item index="/roles"><span>角色管理</span></el-menu-item>
            <el-menu-item index="/dicts"><span>数据字典</span></el-menu-item>
            <el-menu-item index="/tenants"><span>租户管理</span></el-menu-item>
          </el-sub-menu>

        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 移动端抽屉菜单 -->
    <el-drawer v-if="isMobile" v-model="drawerVisible" direction="ltr" :size="260" :with-header="false">
      <div class="aside-logo" style="background: #001529; margin: -20px -20px 0; padding: 16px; text-align: center; color: #fff; font-weight: 700;">
        🏛 国资监管平台
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#fff"
        text-color="#333"
        active-text-color="#00b4d8"
        @select="handleMobileSelect"
      >
        <el-menu-item index="/">管理驾驶舱</el-menu-item>
        <el-menu-item index="/finance/reports">财务快报</el-menu-item>
        <el-menu-item index="/property/registrations">产权管理</el-menu-item>
        <el-menu-item index="/decision/items">三重一大</el-menu-item>
        <el-menu-item index="/performance/scoreboard">业绩考核</el-menu-item>
        <el-menu-item index="/supervision/rectifications">监督追责</el-menu-item>
        <el-menu-item index="/major-events">重大事项</el-menu-item>
        <el-menu-item index="/reports">监管上报</el-menu-item>
        <el-menu-item index="/assets">资产台账</el-menu-item>
        <el-menu-item index="/stock-ins">资产流转</el-menu-item>
        <el-menu-item index="/inspection-tasks">巡检管理</el-menu-item>
        <el-menu-item index="/inventory-tasks">盘点管理</el-menu-item>
        <el-menu-item index="/maintenance-requests">维保维修</el-menu-item>
        <el-menu-item index="/depreciation">折旧管理</el-menu-item>
        <el-menu-item index="/projects">项目管理</el-menu-item>
        <el-menu-item index="/investments">投资组合</el-menu-item>
        <el-menu-item index="/alerts">预警中心</el-menu-item>
        <el-menu-item index="/hr/employees">人力资源</el-menu-item>
        <el-menu-item index="/approval/pending">待我审批</el-menu-item>
      </el-menu>
    </el-drawer>

    <el-container>
      <el-header class="app-header">
        <div class="header-left">
          <el-button :icon="isMobile ? 'Menu' : (isCollapse ? 'Expand' : 'Fold')" text @click="toggleMenu" />
          <span class="header-title" v-if="!isMobile">湛江市国资国企一体化数字平台</span>
          <span class="header-title" v-else>国资监管</span>
        </div>
        <div class="header-right">
          <el-button text @click="$router.push('/screen')" title="数据大屏">
            <el-icon :size="20"><DataBoard /></el-icon>
          </el-button>
          <el-popover placement="bottom" :width="320" trigger="click" @show="loadNotifications">
            <template #reference>
              <el-badge :value="unreadCount" :hidden="unreadCount === 0">
                <el-icon :size="20" style="cursor:pointer"><Bell /></el-icon>
              </el-badge>
            </template>
            <div style="max-height:300px;overflow-y:auto">
              <div v-if="recentNotifs.length===0" style="text-align:center;color:#999;padding:20px">暂无未读消息</div>
              <div v-for="n in recentNotifs" :key="n.id" class="notif-item" @click="goNotification(n)">
                <div class="notif-title">{{n.title}}</div>
                <div class="notif-meta">
                  <el-tag size="small" :type="n.level==='CRITICAL'?'danger':n.level==='WARNING'?'warning':'info'">{{n.level}}</el-tag>
                  <span>{{n.createdAt?.slice(0,16)}}</span>
                </div>
              </div>
            </div>
            <div style="text-align:center;padding:8px;border-top:1px solid #f0f0f0">
              <el-button link size="small" @click="$router.push('/notifications')">查看全部</el-button>
              <el-button link size="small" @click="markAll">全部已读</el-button>
            </div>
          </el-popover>
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
import { useResponsive } from '@/composables/useResponsive'
import { listNotifications, getUnreadCount, markAllRead } from '@/api/notification'
import type { Notification } from '@/api/notification'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const unreadCount = ref(0)
const recentNotifs = ref<Notification[]>([])
const drawerVisible = ref(false)
const { isMobile } = useResponsive()

const activeMenu = computed(() => route.path)

function handleSelect(index: string) {
  router.push(index)
}

function handleMobileSelect(index: string) {
  router.push(index)
  drawerVisible.value = false
}

function toggleMenu() {
  if (isMobile.value) {
    drawerVisible.value = true
  } else {
    isCollapse.value = !isCollapse.value
  }
}

async function loadNotifications() {
  const userId = userStore.userInfo?.userId
  if (!userId) return
  const [countRes, notifsRes] = await Promise.all([
    getUnreadCount(userId),
    listNotifications({ userId, size: 5 })
  ])
  unreadCount.value = (countRes as any).data || 0
  recentNotifs.value = ((notifsRes as any).data?.records || []).filter((n: Notification) => n.isRead === 0)
}

async function markAll() {
  const userId = userStore.userInfo?.userId
  if (!userId) return
  await markAllRead(userId)
  unreadCount.value = 0
  recentNotifs.value = []
}

function goNotification(n: Notification) {
  if (n.bizType === 'APPROVAL') router.push('/approval/pending')
  else if (n.bizType === 'ALERT') router.push('/alerts')
  else router.push('/notifications')
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

/* 三大域标题 */
.menu-domain {
  padding: 12px 20px 4px;
}
.domain-tag {
  font-size: 11px; font-weight: 600; letter-spacing: 1px;
  padding: 2px 8px; border-radius: 3px;
}
.domain-supervision { color: #ffa940; background: rgba(255,169,64,0.12); }
.domain-asset { color: #36cfc9; background: rgba(54,207,201,0.12); }
.domain-platform { color: #597ef7; background: rgba(89,126,247,0.12); }

.app-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 20px; background: #fff; border-bottom: 1px solid #f0f0f0; height: 56px;
}
.header-left { display: flex; align-items: center; gap: 8px; }
.header-title { font-size: 15px; font-weight: 600; color: #333; }
.header-right { display: flex; align-items: center; gap: 20px; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; }
.app-main { background: #f0f2f5; min-height: calc(100vh - 56px); padding: 20px; }

/* 侧边栏菜单样式 */
:deep(.el-menu) { border-right: none; }
:deep(.el-menu-item) {
  color: rgba(255,255,255,0.85) !important;
  transition: all 0.25s ease;
  border-left: 3px solid transparent;
}
:deep(.el-sub-menu__title) {
  color: rgba(255,255,255,0.85) !important;
  transition: all 0.25s ease;
}
:deep(.el-sub-menu__title):hover {
  background-color: rgba(0,212,255,0.12) !important;
  color: #00d4ff !important;
}
:deep(.el-menu-item):hover {
  background-color: rgba(0,212,255,0.15) !important;
  color: #00d4ff !important;
  border-left-color: #00d4ff;
}
:deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(0,212,255,0.25) 0%, rgba(0,180,216,0.08) 100%) !important;
  color: #00d4ff !important;
  border-left: 3px solid #00d4ff;
  font-weight: 600;
}
:deep(.el-menu--popup) {
  background-color: #001b3a !important;
}
:deep(.el-menu--popup .el-menu-item) {
  background-color: #001b3a !important;
  color: rgba(255,255,255,0.85) !important;
}
:deep(.el-menu--popup .el-menu-item):hover {
  background-color: rgba(0,212,255,0.15) !important;
  color: #00d4ff !important;
}
:deep(.el-menu--collapse .el-sub-menu.is-active .el-sub-menu__title) {
  color: #00d4ff !important;
  border-bottom: 2px solid #00d4ff;
}

@media (max-width: 768px) {
  .app-header { padding: 0 12px; height: 48px; }
  .header-title { font-size: 14px; }
  .app-main { padding: 12px; min-height: calc(100vh - 48px); }
  .header-right { gap: 12px; }
}

.notif-item { padding: 8px 0; border-bottom: 1px solid #f5f5f5; cursor: pointer; }
.notif-item:hover { background: #f5f7fa; }
.notif-title { font-size: 13px; color: #333; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.notif-meta { display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: #999; }
</style>
