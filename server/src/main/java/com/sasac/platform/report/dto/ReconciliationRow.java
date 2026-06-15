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
public class ReconciliationRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产类别")
    private String category;
    @ExcelProperty("明细表名称")
    private String detailTableName;
    @ExcelProperty("明细表数量(项)")
    private Integer detailCount;
    @ExcelProperty("明细表原值合计(元)")
    private BigDecimal detailOriginalValue;
    @ExcelProperty("明细表净值合计(元)")
    private BigDecimal detailCurrentValue;
    @ExcelProperty("底数清单原值(元)")
    private BigDecimal baseListOriginalValue;
    @ExcelProperty("底数清单净值(元)")
    private BigDecimal baseListCurrentValue;
    @ExcelProperty("原值差异(元)")
    private BigDecimal originalValueDiff;
    @ExcelProperty("净值差异(元)")
    private BigDecimal currentValueDiff;
    @ExcelProperty("差异原因说明")
    private String diffReason;
    @ExcelProperty("是否勾稽一致")
    private String isConsistent;
}
