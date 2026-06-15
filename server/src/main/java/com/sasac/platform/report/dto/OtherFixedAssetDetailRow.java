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
public class OtherFixedAssetDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("资产名称")
    private String assetName;
    @ExcelProperty("资产分类")
    private String category;
    @ExcelProperty("规格型号")
    private String specification;
    @ExcelProperty("计量单位")
    private String unit;
    @ExcelProperty("数量")
    private Integer quantity;
    @ExcelProperty("取得日期")
    private LocalDate acquisitionDate;
    @ExcelProperty("账面原值(元)")
    private BigDecimal originalValue;
    @ExcelProperty("账面净值(元)")
    private BigDecimal currentValue;
    @ExcelProperty("累计折旧(元)")
    private BigDecimal accumulatedDepreciation;
    @ExcelProperty("使用状态")
    private String useStatus;
    @ExcelProperty("存放地点")
    private String location;
    @ExcelProperty("使用部门")
    private String useDepartment;
    @ExcelProperty("保管人")
    private String custodian;
    @ExcelProperty("备注")
    private String remark;
}
