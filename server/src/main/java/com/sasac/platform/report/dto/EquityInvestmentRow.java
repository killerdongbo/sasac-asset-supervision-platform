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
public class EquityInvestmentRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("被投资企业")
    private String enterpriseName;
    @ExcelProperty("统一社会信用代码")
    private String creditCode;
    @ExcelProperty("投资日期")
    private LocalDate investDate;
    @ExcelProperty("投资方式")
    private String investMethod;
    @ExcelProperty("持股比例(%)")
    private BigDecimal shareRatio;
    @ExcelProperty("投资金额(元)")
    private BigDecimal investAmount;
    @ExcelProperty("累计分红(元)")
    private BigDecimal cumulativeDividend;
    @ExcelProperty("账面价值(元)")
    private BigDecimal bookValue;
    @ExcelProperty("公允价值(元)")
    private BigDecimal fairValue;
    @ExcelProperty("是否减值")
    private String isImpaired;
    @ExcelProperty("减值金额(元)")
    private BigDecimal impairmentAmount;
    @ExcelProperty("被投资企业状态")
    private String enterpriseStatus;
    @ExcelProperty("备注")
    private String remark;
}
