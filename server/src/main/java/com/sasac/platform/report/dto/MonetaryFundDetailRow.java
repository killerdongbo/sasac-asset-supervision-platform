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
public class MonetaryFundDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("账户名称")
    private String accountName;
    @ExcelProperty("开户银行")
    private String bankName;
    @ExcelProperty("银行账号")
    private String accountNo;
    @ExcelProperty("币种")
    private String currency;
    @ExcelProperty("账面余额(元)")
    private BigDecimal bookBalance;
    @ExcelProperty("银行对账单余额(元)")
    private BigDecimal statementBalance;
    @ExcelProperty("差异金额(元)")
    private BigDecimal diffAmount;
    @ExcelProperty("差异说明")
    private String diffReason;
    @ExcelProperty("账户类型")
    private String accountType;
    @ExcelProperty("是否受限")
    private String isRestricted;
    @ExcelProperty("受限金额(元)")
    private BigDecimal restrictedAmount;
    @ExcelProperty("对账日期")
    private LocalDate reconciliationDate;
    @ExcelProperty("备注")
    private String remark;
}
