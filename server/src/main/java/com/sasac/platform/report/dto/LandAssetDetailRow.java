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
public class LandAssetDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("土地名称")
    private String landName;
    @ExcelProperty("坐落位置")
    private String location;
    @ExcelProperty("宗地面积(㎡)")
    private BigDecimal area;
    @ExcelProperty("权属性质")
    private String ownershipType;
    @ExcelProperty("权属证号")
    private String certificateNo;
    @ExcelProperty("用途分类")
    private String usageCategory;
    @ExcelProperty("取得方式")
    private String acquisitionMethod;
    @ExcelProperty("取得日期")
    private LocalDate acquisitionDate;
    @ExcelProperty("使用年限")
    private Integer usefulLifeYears;
    @ExcelProperty("账面原值(元)")
    private BigDecimal originalValue;
    @ExcelProperty("账面净值(元)")
    private BigDecimal currentValue;
    @ExcelProperty("是否闲置")
    private String isIdle;
    @ExcelProperty("备注")
    private String remark;
}
