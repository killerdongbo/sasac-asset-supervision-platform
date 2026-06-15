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
public class PeFundInvestmentRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("基金名称")
    private String fundName;
    @ExcelProperty("基金管理人")
    private String fundManager;
    @ExcelProperty("基金类型")
    private String fundType;
    @ExcelProperty("认缴金额(元)")
    private BigDecimal subscribedAmount;
    @ExcelProperty("实缴金额(元)")
    private BigDecimal paidAmount;
    @ExcelProperty("投资日期")
    private LocalDate investDate;
    @ExcelProperty("基金存续期")
    private String fundDuration;
    @ExcelProperty("当前净值(元)")
    private BigDecimal currentNav;
    @ExcelProperty("累计收益(元)")
    private BigDecimal cumulativeReturn;
    @ExcelProperty("是否违规")
    private String isViolation;
    @ExcelProperty("备案编号")
    private String recordNo;
    @ExcelProperty("备注")
    private String remark;
}
