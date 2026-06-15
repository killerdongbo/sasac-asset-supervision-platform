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
public class BuildingAssetDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("建筑物名称")
    private String buildingName;
    @ExcelProperty("坐落位置")
    private String location;
    @ExcelProperty("结构类型")
    private String structureType;
    @ExcelProperty("建筑面积(㎡)")
    private BigDecimal area;
    @ExcelProperty("权属证号")
    private String certificateNo;
    @ExcelProperty("竣工日期")
    private LocalDate completionDate;
    @ExcelProperty("使用年限")
    private Integer usefulLifeYears;
    @ExcelProperty("账面原值(元)")
    private BigDecimal originalValue;
    @ExcelProperty("账面净值(元)")
    private BigDecimal currentValue;
    @ExcelProperty("使用状态")
    private String useStatus;
    @ExcelProperty("是否出租")
    private String isRented;
    @ExcelProperty("年租金(元)")
    private BigDecimal annualRent;
    @ExcelProperty("备注")
    private String remark;
}
