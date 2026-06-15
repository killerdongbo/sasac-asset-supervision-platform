package com.sasac.platform.system.export;

public final class ExportType {

    private ExportType() {}

    // ---- 已有 ----
    public static final String ASSET_LIST = "ASSET_LIST";
    public static final String INVENTORY_REPORT = "INVENTORY_REPORT";
    public static final String DEPRECIATION_LIST = "DEPRECIATION_LIST";

    // ---- 综合报表（1-4） ----
    public static final String BALANCE_SHEET = "BALANCE_SHEET";
    public static final String ASSET_BASE_LIST = "ASSET_BASE_LIST";
    public static final String PROBLEM_ASSET_LIST = "PROBLEM_ASSET_LIST";
    public static final String REVITALIZATION_LIST = "REVITALIZATION_LIST";

    // ---- 资产清查明细（5-13） ----
    public static final String LAND_ASSET_DETAIL = "LAND_ASSET_DETAIL";
    public static final String BUILDING_ASSET_DETAIL = "BUILDING_ASSET_DETAIL";
    public static final String EQUITY_INVESTMENT_DETAIL = "EQUITY_INVESTMENT_DETAIL";
    public static final String CREDITOR_RIGHTS_DETAIL = "CREDITOR_RIGHTS_DETAIL";
    public static final String PE_FUND_INVESTMENT_DETAIL = "PE_FUND_INVESTMENT_DETAIL";
    public static final String INVENTORY_DETAIL = "INVENTORY_DETAIL";
    public static final String INTANGIBLE_ASSET_DETAIL = "INTANGIBLE_ASSET_DETAIL";
    public static final String FRANCHISE_RIGHT_DETAIL = "FRANCHISE_RIGHT_DETAIL";
    public static final String DATA_ASSET_DETAIL = "DATA_ASSET_DETAIL";

    // ---- 实物与资金（14-18） ----
    public static final String NATURAL_RESOURCE_DETAIL = "NATURAL_RESOURCE_DETAIL";
    public static final String MACHINERY_EQUIP_DETAIL = "MACHINERY_EQUIP_DETAIL";
    public static final String INFRASTRUCTURE_DETAIL = "INFRASTRUCTURE_DETAIL";
    public static final String MONETARY_FUND_DETAIL = "MONETARY_FUND_DETAIL";
    public static final String OTHER_FIXED_ASSET_DETAIL = "OTHER_FIXED_ASSET_DETAIL";

    // ---- 稽核（19） ----
    public static final String RECONCILIATION_TABLE = "RECONCILIATION_TABLE";

    /**
     * Human-readable label for each export type.
     */
    public static String label(String exportType) {
        return switch (exportType) {
            case ASSET_LIST -> "资产台账";
            case INVENTORY_REPORT -> "盘点报告";
            case DEPRECIATION_LIST -> "折旧明细";
            case BALANCE_SHEET -> "资产负债表";
            case ASSET_BASE_LIST -> "资产底数清单";
            case PROBLEM_ASSET_LIST -> "问题资产及整治清单";
            case REVITALIZATION_LIST -> "盘活利用清单";
            case LAND_ASSET_DETAIL -> "土地类资产清查明细表";
            case BUILDING_ASSET_DETAIL -> "房屋建筑类清查明细表";
            case EQUITY_INVESTMENT_DETAIL -> "股权类（对外投资）清查明细表";
            case CREDITOR_RIGHTS_DETAIL -> "债权类清查明细表";
            case PE_FUND_INVESTMENT_DETAIL -> "企业私募基金投资情况调查表";
            case INVENTORY_DETAIL -> "存货清查明细表";
            case INTANGIBLE_ASSET_DETAIL -> "无形资产清查明细表";
            case FRANCHISE_RIGHT_DETAIL -> "特许经营权清查明细表";
            case DATA_ASSET_DETAIL -> "数据资源/资产清查明细表";
            case NATURAL_RESOURCE_DETAIL -> "自然资源资产清查明细表";
            case MACHINERY_EQUIP_DETAIL -> "大型机器设备清查明细表";
            case INFRASTRUCTURE_DETAIL -> "基础设施资产清查明细表";
            case MONETARY_FUND_DETAIL -> "货币资金清查明细表";
            case OTHER_FIXED_ASSET_DETAIL -> "其他固定资产清查明细表";
            case RECONCILIATION_TABLE -> "汇总表与明细表的勾稽关系表";
            default -> exportType;
        };
    }
}
