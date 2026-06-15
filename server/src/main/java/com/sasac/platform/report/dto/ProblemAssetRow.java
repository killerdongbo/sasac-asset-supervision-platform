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
public class ProblemAssetRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("资产名称")
    private String assetName;
    @ExcelProperty("问题类型")
    private String problemType;
    @ExcelProperty("问题描述")
    private String description;
    @ExcelProperty("涉及金额(元)")
    private BigDecimal amount;
    @ExcelProperty("发现日期")
    private LocalDate foundDate;
    @ExcelProperty("整治措施")
    private String measure;
    @ExcelProperty("责任部门")
    private String responsibleDept;
    @ExcelProperty("整改状态")
    private String status;
    @ExcelProperty("完成日期")
    private LocalDate completedDate;
    @ExcelProperty("备注")
    private String remark;
}
