import client from './client'

/** 综合驾驶舱统计数据 */
export interface EnhancedOverview {
  totalAssets: number                // 资产总额
  activeEmployees: number            // 在岗人数
  activeProjects: number             // 在建项目数
  investPortfolioValue: number       // 投资组合市值
  monthlyAlertCount: number          // 本月预警数
}

/** 预警项目 */
export interface AlertItem {
  id: number
  alertType: string
  title: string
  content: string
  severity: string
  status: string
  createdAt: string
}

/**
 * 获取综合驾驶舱概览数据
 * 聚合资产、人事、项目、投资、预警五个维度
 */
export function getEnhancedOverview(): Promise<{ data: EnhancedOverview }> {
  return client.get('/dashboard-enhanced/overview') as Promise<{ data: EnhancedOverview }>
}

/**
 * 获取最近5条预警
 */
export function getRecentAlerts(): Promise<{ data: AlertItem[] }> {
  return client.get('/dashboard-enhanced/recent-alerts') as Promise<{ data: AlertItem[] }>
}

/**
 * 综合态势 - 调用各模块API并行获取聚合数据
 * 如果后端API尚未ready，使用后端聚合接口
 */
export function getAggregatedData(): Promise<{
  overview: EnhancedOverview
  recentAlerts: AlertItem[]
}> {
  return client.get('/dashboard-enhanced/aggregated') as Promise<{
    data: { overview: EnhancedOverview; recentAlerts: AlertItem[] }
  }>.then(res => res.data)
}
