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
public class NaturalResourceDetailRow {
    @ExcelProperty("序号")
    private Integer seq;
    @ExcelProperty("资产编号")
    private String assetCode;
    @ExcelProperty("资源名称")
    private String resourceName;
    @ExcelProperty("资源类型")
    private String resourceType;
    @ExcelProperty("坐落位置")
    private String location;
    @ExcelProperty("面积/储量")
    private String areaOrReserve;
    @ExcelProperty("计量单位")
    private String unit;
    @ExcelProperty("权属证号")
    private String certificateNo;
    @ExcelProperty("取得方式")
    private String acquisitionMethod;
    @ExcelProperty("使用年限")
    private Integer usefulLifeYears;
    @ExcelProperty("账面价值(元)")
    private BigDecimal bookValue;
    @ExcelProperty("评估价值(元)")
    private BigDecimal appraisedValue;
    @ExcelProperty("是否开发利用")
    private String isDeveloped;
    @ExcelProperty("备注")
    private String remark;
}
