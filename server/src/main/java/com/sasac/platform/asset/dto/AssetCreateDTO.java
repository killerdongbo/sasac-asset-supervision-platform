package com.sasac.platform.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new asset.
 */
@Data
public class AssetCreateDTO {

    @NotBlank(message = "资产名称不能为空")
    private String name;

    @NotBlank(message = "资产编码不能为空")
    private String assetCode;

    @NotBlank(message = "资产分类不能为空")
    private String category;

    @NotNull(message = "所属组织不能为空")
    private Long orgId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private String specification;

    private String unit;

    private Integer quantity = 1;

    private BigDecimal originalValue;

    private String depreciationMethod;

    private Integer usefulLifeMonths;

    private BigDecimal residualRate;

    private String useStatus = "IN_USE";

    private String useDepartment;

    private String custodian;

    private String location;

    private String address;

    private LocalDate purchaseDate;

    private LocalDate registrationDate;

    private String sourceType;

    private String certificateNo;

    private String remark;
}
