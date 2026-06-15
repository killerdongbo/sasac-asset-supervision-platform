package com.sasac.platform.system.export.dto;

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
public class AssetExportRow {
    @ExcelProperty("资产名称")
    private String name;
    @ExcelProperty("资产编码")
    private String assetCode;
    @ExcelProperty("资产分类")
    private String category;
    @ExcelProperty("规格型号")
    private String specification;
    @ExcelProperty("存放地点")
    private String location;
    @ExcelProperty("使用状态")
    private String useStatus;
    @ExcelProperty("原值")
    private BigDecimal originalValue;
    @ExcelProperty("净值")
    private BigDecimal currentValue;
}
