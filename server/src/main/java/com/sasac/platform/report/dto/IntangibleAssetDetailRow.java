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
public class IntangibleAssetDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("无形资产名称")
    private String assetName;
    @ExcelProperty("分类")
    private String category;
    @ExcelProperty("专利号/著作权号/商标号")
    private String registrationNo;
    @ExcelProperty("取得方式")
    private String acquisitionMethod;
    @ExcelProperty("取得日期")
    private LocalDate acquisitionDate;
    @ExcelProperty("摊销年限")
    private Integer amortizationYears;
    @ExcelProperty("账面原值(元)")
    private BigDecimal originalValue;
    @ExcelProperty("累计摊销(元)")
    private BigDecimal accumulatedAmortization;
    @ExcelProperty("账面净值(元)")
    private BigDecimal currentValue;
    @ExcelProperty("是否质押")
    private String isPledged;
    @ExcelProperty("备注")
    private String remark;
}
