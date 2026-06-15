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
public class MachineryEquipDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("设备名称")
    private String equipmentName;
    @ExcelProperty("规格型号")
    private String specification;
    @ExcelProperty("制造厂商")
    private String manufacturer;
    @ExcelProperty("出厂编号")
    private String serialNo;
    @ExcelProperty("生产日期")
    private LocalDate productionDate;
    @ExcelProperty("投产日期")
    private LocalDate operationDate;
    @ExcelProperty("设计寿命(年)")
    private Integer designLifeYears;
    @ExcelProperty("账面原值(元)")
    private BigDecimal originalValue;
    @ExcelProperty("账面净值(元)")
    private BigDecimal currentValue;
    @ExcelProperty("成新率(%)")
    private BigDecimal newnessRate;
    @ExcelProperty("使用状态")
    private String useStatus;
    @ExcelProperty("维保频次")
    private String maintenanceFrequency;
    @ExcelProperty("存放地点")
    private String location;
    @ExcelProperty("备注")
    private String remark;
}
