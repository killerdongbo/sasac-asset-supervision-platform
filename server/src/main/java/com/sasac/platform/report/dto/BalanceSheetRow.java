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
public class BalanceSheetRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("项目")
    private String item;
    @ExcelProperty("行次")
    private Integer lineNo;
    @ExcelProperty("期末余额(元)")
    private BigDecimal endBalance;
    @ExcelProperty("年初余额(元)")
    private BigDecimal startBalance;
    @ExcelProperty("备注")
    private String remark;
}
