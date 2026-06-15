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
public class InfrastructureDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("设施名称")
    private String facilityName;
    @ExcelProperty("设施类型")
    private String facilityType;
    @ExcelProperty("坐落位置")
    private String location;
    @ExcelProperty("规模/长度")
    private String scale;
    @ExcelProperty("计量单位")
    private String unit;
    @ExcelProperty("竣工日期")
    private LocalDate completionDate;
    @ExcelProperty("设计使用年限")
    private Integer designLifeYears;
    @ExcelProperty("账面原值(元)")
    private BigDecimal originalValue;
    @ExcelProperty("账面净值(元)")
    private BigDecimal currentValue;
    @ExcelProperty("使用状态")
    private String useStatus;
    @ExcelProperty("管理部门")
    private String managementDept;
    @ExcelProperty("是否收费运营")
    private String isTollOperation;
    @ExcelProperty("年运营收入(元)")
    private BigDecimal annualRevenue;
    @ExcelProperty("备注")
    private String remark;
}
