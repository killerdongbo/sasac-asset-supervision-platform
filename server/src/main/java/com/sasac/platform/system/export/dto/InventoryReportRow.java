package com.sasac.platform.system.export.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportRow {
    @ExcelProperty("任务名称")
    private String taskName;
    @ExcelProperty("状态")
    private String status;
    @ExcelProperty("资产总数")
    private Integer totalCount;
    @ExcelProperty("已完成数")
    private Integer completedCount;
    @ExcelProperty("差异数")
    private Integer diffCount;
    @ExcelProperty("盘点记录数")
    private Integer recordCount;
}
