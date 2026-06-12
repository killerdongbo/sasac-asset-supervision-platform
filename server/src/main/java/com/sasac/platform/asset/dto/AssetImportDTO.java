package com.sasac.platform.asset.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for importing assets from Excel files.
 * Fields are mapped to Excel columns via {@code @ExcelProperty}.
 */
@Data
public class AssetImportDTO {

    @ExcelProperty("资产名称")
    @NotBlank
    private String name;

    @ExcelProperty("资产编码")
    @NotBlank
    private String assetCode;

    @ExcelProperty("资产分类")
    @NotBlank
    private String category;

    @ExcelProperty("规格型号")
    private String specification;

    @ExcelProperty("计量单位")
    private String unit;

    @ExcelProperty("数量")
    private Integer quantity;

    @ExcelProperty("原值(元)")
    private BigDecimal originalValue;

    @ExcelProperty("折旧方法")
    private String depreciationMethod;

    @ExcelProperty("使用年限(月)")
    private Integer usefulLifeMonths;

    @ExcelProperty("使用状态")
    private String useStatus;

    @ExcelProperty("使用部门")
    private String useDepartment;

    @ExcelProperty("保管人")
    private String custodian;

    @ExcelProperty("存放地点")
    private String location;

    @ExcelProperty("购置日期")
    private String purchaseDate;

    @ExcelProperty("来源方式")
    private String sourceType;

    @ExcelProperty("权属证号")
    private String certificateNo;

    @ExcelProperty("备注")
    private String remark;
}
