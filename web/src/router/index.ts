import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        // ===== 工作台 =====
        { path: '', name: 'Dashboard', component: () => import('@/views/dashboard/DashboardView.vue') },

        // ===== 资产基础资料 =====
        { path: 'categories', name: 'CategoryList', component: () => import('@/views/basic/CategoryList.vue') },
        { path: 'locations', name: 'LocationList', component: () => import('@/views/basic/LocationList.vue') },
        { path: 'suppliers', name: 'SupplierList', component: () => import('@/views/basic/SupplierList.vue') },
        { path: 'maintenance-providers', name: 'ProviderList', component: () => import('@/views/basic/ProviderList.vue') },

        // ===== 资产台账管理 =====
        { path: 'assets', name: 'AssetList', component: () => import('@/views/asset/AssetList.vue') },
        { path: 'assets/create', name: 'AssetCreate', component: () => import('@/views/asset/AssetForm.vue') },
        { path: 'assets/import', name: 'AssetImport', component: () => import('@/views/asset/AssetImport.vue') },
        { path: 'assets/:id', name: 'AssetDetail', component: () => import('@/views/asset/AssetDetail.vue') },
        { path: 'assets/:id/edit', name: 'AssetEdit', component: () => import('@/views/asset/AssetForm.vue') },

        // ===== 资产流转管理 =====
        { path: 'stock-ins', name: 'StockInList', component: () => import('@/views/circulation/StockInList.vue') },
        { path: 'assignments', name: 'AssignmentList', component: () => import('@/views/circulation/AssignmentList.vue') },
        { path: 'transfers', name: 'TransferList', component: () => import('@/views/circulation/TransferList.vue') },
        { path: 'disposals', name: 'DisposalList', component: () => import('@/views/circulation/DisposalList.vue') },

        // ===== 采购管理 =====
        { path: 'purchase-requests', name: 'PurchaseRequestList', component: () => import('@/views/procurement/PurchaseRequestList.vue') },
        { path: 'purchase-acceptances', name: 'PurchaseAcceptanceList', component: () => import('@/views/procurement/PurchaseAcceptanceList.vue') },

        // ===== 巡检管理 =====
        { path: 'inspection-tasks', name: 'InspectionTaskList', component: () => import('@/views/inspection/TaskList.vue') },
        { path: 'inspection-tasks/:id/execute', name: 'InspectionExecute', component: () => import('@/views/inspection/TaskExecute.vue') },
        { path: 'inspection-anomalies', name: 'InspectionAnomalyList', component: () => import('@/views/inspection/AnomalyList.vue') },

        // ===== 盘点管理 =====
        { path: 'inventory-tasks', name: 'InventoryTaskList', component: () => import('@/views/inventory/TaskList.vue') },
        { path: 'inventory-tasks/:id/execute', name: 'InventoryExecute', component: () => import('@/views/inventory/TaskExecute.vue') },
        { path: 'inventory-diffs', name: 'InventoryDiffList', component: () => import('@/views/inventory/DiffList.vue') },

        // ===== 维保维修管理 =====
        { path: 'maintenance-requests', name: 'MaintenanceRequestList', component: () => import('@/views/maintenance/RequestList.vue') },

        // ===== 财务折旧管理 =====
        { path: 'depreciation', name: 'DepreciationList', component: () => import('@/views/depreciation/DepreciationList.vue') },

        // ===== 审批工作流 =====
        { path: 'approval/pending', name: 'ApprovalPending', component: () => import('@/views/approval/PendingList.vue') },
        { path: 'approval/my-requests', name: 'ApprovalMyRequests', component: () => import('@/views/approval/MyRequestList.vue') },
        { path: 'approval/history', name: 'ApprovalHistory', component: () => import('@/views/approval/HistoryList.vue') },
        { path: 'approval/defs', name: 'ApprovalDefList', component: () => import('@/views/approval/DefList.vue') },

        // ===== 预警中心 =====
        { path: 'alerts', name: 'AlertList', component: () => import('@/views/alert/AlertList.vue') },

        // ===== 监管上报 =====
        { path: 'reports', name: 'ReportList', component: () => import('@/views/report/ReportList.vue') },
        { path: 'reports/:id', name: 'ReportDetail', component: () => import('@/views/report/ReportDetail.vue') },

        // ===== 统计报表 =====
        { path: 'statistics/assets', name: 'AssetStatistics', component: () => import('@/views/statistics/AssetStats.vue') },
        { path: 'statistics/depreciation', name: 'DepreciationStatistics', component: () => import('@/views/statistics/DepreciationStats.vue') },

        // ===== 审计追溯 =====
        { path: 'audit-logs', name: 'AuditLogList', component: () => import('@/views/audit/LogList.vue') },
        { path: 'audit-lifecycle', name: 'AssetLifecycle', component: () => import('@/views/audit/Lifecycle.vue') },

        // ===== 系统管理 =====
        { path: 'users', name: 'UserManage', component: () => import('@/views/system/UserManage.vue') },
        { path: 'roles', name: 'RoleManage', component: () => import('@/views/system/RoleManage.vue') },
        { path: 'organizations', name: 'OrgManage', component: () => import('@/views/system/OrgManage.vue') },
        { path: 'dicts', name: 'DictManage', component: () => import('@/views/system/DictManage.vue') }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth !== false && !userStore.token) {
    next({ name: 'Login' })
  } else {
    next()
  }
})

export default router
