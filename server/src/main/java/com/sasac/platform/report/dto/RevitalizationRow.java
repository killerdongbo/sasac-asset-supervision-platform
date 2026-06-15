package com.sasac.platform.report.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevitalizationRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("资产名称")
    private String assetName;
    @ExcelProperty("盘活方式")
    private String revitalizationMethod;
    @ExcelProperty("原使用状态")
    private String originalStatus;
    @ExcelProperty("原值(元)")
    private BigDecimal originalValue;
    @ExcelProperty("盘活收益(元)")
    private BigDecimal revitalizationRevenue;
    @ExcelProperty("盘活日期")
    private LocalDate revitalizationDate;
    @ExcelProperty("当前状态")
    private String currentStatus;
    @ExcelProperty("利用方")
    private String beneficiary;
    @ExcelProperty("审批文号")
    private String approvalNo;
    @ExcelProperty("备注")
    private String remark;
}
