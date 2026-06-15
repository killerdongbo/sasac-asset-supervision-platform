package com.sasac.platform.system.export;

import com.alibaba.excel.EasyExcel;
import com.sasac.platform.report.dto.BalanceSheetRow;
import com.sasac.platform.report.dto.BuildingAssetDetailRow;
import com.sasac.platform.report.dto.CreditorRightsRow;
import com.sasac.platform.report.dto.DataAssetDetailRow;
import com.sasac.platform.report.dto.EquityInvestmentRow;
import com.sasac.platform.report.dto.FranchiseRightRow;
import com.sasac.platform.report.dto.InfrastructureDetailRow;
import com.sasac.platform.report.dto.IntangibleAssetDetailRow;
import com.sasac.platform.report.dto.InventoryDetailRow;
import com.sasac.platform.report.dto.LandAssetDetailRow;
import com.sasac.platform.report.dto.MachineryEquipDetailRow;
import com.sasac.platform.report.dto.MonetaryFundDetailRow;
import com.sasac.platform.report.dto.NaturalResourceDetailRow;
import com.sasac.platform.report.dto.OtherFixedAssetDetailRow;
import com.sasac.platform.report.dto.PeFundInvestmentRow;
import com.sasac.platform.report.dto.ProblemAssetRow;
import com.sasac.platform.report.dto.ReconciliationRow;
import com.sasac.platform.report.dto.RevitalizationRow;
import com.sasac.platform.system.export.dto.AssetExportRow;
import com.sasac.platform.system.export.dto.DepreciationRow;
import com.sasac.platform.system.export.dto.InventoryReportRow;

import java.io.File;
import java.util.Collections;

/**
 * One-shot utility to generate header-only .xlsx template files for all 22 export types.
 * <p>
 * Run this main method, then copy the output files to src/main/resources/report-templates/.
 */
public class TemplateGenerator {

    private static final String OUTPUT_DIR = "server/src/main/resources/report-templates/";

    public static void main(String[] args) {
        new File(OUTPUT_DIR).mkdirs();

        // Existing 3
        EasyExcel.write(OUTPUT_DIR + "ASSET_LIST.xlsx", AssetExportRow.class).sheet("资产台账").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "INVENTORY_REPORT.xlsx", InventoryReportRow.class).sheet("盘点报告").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "DEPRECIATION_LIST.xlsx", DepreciationRow.class).sheet("折旧明细").doWrite(Collections.emptyList());

        // 综合报表 (1-4)
        EasyExcel.write(OUTPUT_DIR + "BALANCE_SHEET.xlsx", BalanceSheetRow.class).sheet("资产负债表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "ASSET_BASE_LIST.xlsx", AssetExportRow.class).sheet("资产底数清单").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "PROBLEM_ASSET_LIST.xlsx", ProblemAssetRow.class).sheet("问题资产及整治清单").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "REVITALIZATION_LIST.xlsx", RevitalizationRow.class).sheet("盘活利用清单").doWrite(Collections.emptyList());

        // 资产清查明细 (5-13)
        EasyExcel.write(OUTPUT_DIR + "LAND_ASSET_DETAIL.xlsx", LandAssetDetailRow.class).sheet("土地类资产清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "BUILDING_ASSET_DETAIL.xlsx", BuildingAssetDetailRow.class).sheet("房屋建筑类清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "EQUITY_INVESTMENT_DETAIL.xlsx", EquityInvestmentRow.class).sheet("股权类清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "CREDITOR_RIGHTS_DETAIL.xlsx", CreditorRightsRow.class).sheet("债权类清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "PE_FUND_INVESTMENT_DETAIL.xlsx", PeFundInvestmentRow.class).sheet("私募基金投资调查表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "INVENTORY_DETAIL.xlsx", InventoryDetailRow.class).sheet("存货清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "INTANGIBLE_ASSET_DETAIL.xlsx", IntangibleAssetDetailRow.class).sheet("无形资产清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "FRANCHISE_RIGHT_DETAIL.xlsx", FranchiseRightRow.class).sheet("特许经营权清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "DATA_ASSET_DETAIL.xlsx", DataAssetDetailRow.class).sheet("数据资源资产清查明细表").doWrite(Collections.emptyList());

        // 实物与资金 (14-18)
        EasyExcel.write(OUTPUT_DIR + "NATURAL_RESOURCE_DETAIL.xlsx", NaturalResourceDetailRow.class).sheet("自然资源资产清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "MACHINERY_EQUIP_DETAIL.xlsx", MachineryEquipDetailRow.class).sheet("大型机器设备清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "INFRASTRUCTURE_DETAIL.xlsx", InfrastructureDetailRow.class).sheet("基础设施资产清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "MONETARY_FUND_DETAIL.xlsx", MonetaryFundDetailRow.class).sheet("货币资金清查明细表").doWrite(Collections.emptyList());
        EasyExcel.write(OUTPUT_DIR + "OTHER_FIXED_ASSET_DETAIL.xlsx", OtherFixedAssetDetailRow.class).sheet("其他固定资产清查明细表").doWrite(Collections.emptyList());

        // 稽核 (19)
        EasyExcel.write(OUTPUT_DIR + "RECONCILIATION_TABLE.xlsx", ReconciliationRow.class).sheet("勾稽关系表").doWrite(Collections.emptyList());

        System.out.println("Generated " + new File(OUTPUT_DIR).listFiles(f -> f.getName().endsWith(".xlsx")).length + " template files in " + OUTPUT_DIR);
    }
}
