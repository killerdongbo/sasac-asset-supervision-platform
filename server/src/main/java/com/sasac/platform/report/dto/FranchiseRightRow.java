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
public class FranchiseRightRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("经营权名称")
    private String rightName;
    @ExcelProperty("授权方")
    private String authorizer;
    @ExcelProperty("授权期限起")
    private LocalDate startDate;
    @ExcelProperty("授权期限止")
    private LocalDate endDate;
    @ExcelProperty("授权区域")
    private String authorizedArea;
    @ExcelProperty("经营内容")
    private String businessScope;
    @ExcelProperty("授权费(元)")
    private BigDecimal authorizationFee;
    @ExcelProperty("年费(元)")
    private BigDecimal annualFee;
    @ExcelProperty("是否到期")
    private String isExpired;
    @ExcelProperty("备注")
    private String remark;
}
