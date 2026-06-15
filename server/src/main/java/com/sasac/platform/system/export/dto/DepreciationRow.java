package com.sasac.platform.system.export.dto;

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
public class DepreciationRow {
    @ExcelProperty("资产名称")
    private String assetName;
    @ExcelProperty("资产编码")
    private String assetCode;
    @ExcelProperty("折旧期间")
    private String period;
    @ExcelProperty("折旧金额")
    private BigDecimal depreciationAmount;
    @ExcelProperty("折旧前价值")
    private BigDecimal beforeValue;
    @ExcelProperty("折旧后价值")
    private BigDecimal afterValue;
    @ExcelProperty("折旧日期")
    private LocalDate depreciationDate;
}
