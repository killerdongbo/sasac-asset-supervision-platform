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
public class InventoryDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("存货名称")
    private String inventoryName;
    @ExcelProperty("存货分类")
    private String category;
    @ExcelProperty("计量单位")
    private String unit;
    @ExcelProperty("数量")
    private BigDecimal quantity;
    @ExcelProperty("单价(元)")
    private BigDecimal unitPrice;
    @ExcelProperty("金额(元)")
    private BigDecimal amount;
    @ExcelProperty("存放地点")
    private String location;
    @ExcelProperty("库龄")
    private String storageAge;
    @ExcelProperty("是否呆滞")
    private String isStagnant;
    @ExcelProperty("跌价准备(元)")
    private BigDecimal impairmentProvision;
    @ExcelProperty("盘点日期")
    private LocalDate inventoryDate;
    @ExcelProperty("备注")
    private String remark;
}
