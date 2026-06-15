package com.sasac.platform.report.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataAssetDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("数据资产名称")
    private String dataName;
    @ExcelProperty("数据类型")
    private String dataType;
    @ExcelProperty("数据量")
    private String dataVolume;
    @ExcelProperty("存储方式")
    private String storageMethod;
    @ExcelProperty("安全等级")
    private String securityLevel;
    @ExcelProperty("是否入表")
    private String isInBalanceSheet;
    @ExcelProperty("入表价值(元)")
    private BigDecimal balanceSheetValue;
    @ExcelProperty("使用场景")
    private String usageScenario;
    @ExcelProperty("备注")
    private String remark;
}
