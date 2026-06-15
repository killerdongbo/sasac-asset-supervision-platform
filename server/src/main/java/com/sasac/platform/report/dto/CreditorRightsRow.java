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
public class CreditorRightsRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("债权编号")
    private String creditorCode;
    @ExcelProperty("债务人名称")
    private String debtorName;
    @ExcelProperty("债权类型")
    private String rightsType;
    @ExcelProperty("债权金额(元)")
    private BigDecimal amount;
    @ExcelProperty("形成日期")
    private LocalDate formedDate;
    @ExcelProperty("账龄")
    private String aging;
    @ExcelProperty("已计提坏账(元)")
    private BigDecimal badDebtProvision;
    @ExcelProperty("预计可收回(元)")
    private BigDecimal estimatedRecoverable;
    @ExcelProperty("催收状态")
    private String collectionStatus;
    @ExcelProperty("合同/凭证号")
    private String contractNo;
    @ExcelProperty("是否涉诉")
    private String isLitigation;
    @ExcelProperty("备注")
    private String remark;
}
