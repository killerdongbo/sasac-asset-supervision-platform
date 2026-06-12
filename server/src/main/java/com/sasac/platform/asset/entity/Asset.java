package com.sasac.platform.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Asset entity representing a fixed asset in the state-owned assets supervision platform.
 * <p>
 * Maps to the {@code asset} table and tracks the full lifecycle of each asset,
 * including acquisition, depreciation, usage status, and disposal.
 */
@Getter
@Setter
@TableName("asset")
public class Asset extends BaseEntity {

    /**
     * Asset name.
     */
    @NotBlank(message = "资产名称不能为空")
    private String name;

    /**
     * Unique asset code (business code).
     */
    @NotBlank(message = "资产编码不能为空")
    private String assetCode;

    /**
     * Category code referencing asset_category.code.
     */
    @NotBlank(message = "资产分类不能为空")
    private String category;

    /**
     * Organization ID this asset belongs to.
     */
    @NotNull(message = "所属组织不能为空")
    private Long orgId;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Asset specification / model.
     */
    private String specification;

    /**
     * Measurement unit (e.g., 台, 套, 辆).
     */
    private String unit;

    /**
     * Quantity of this asset. Defaults to 1.
     */
    private Integer quantity = 1;

    /**
     * Original purchase value.
     */
    private BigDecimal originalValue;

    /**
     * Current net value (after depreciation).
     */
    private BigDecimal currentValue;

    /**
     * Accumulated depreciation amount. Defaults to 0.
     */
    private BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

    /**
     * Depreciation method (e.g., STRAIGHT_LINE, DOUBLE_DECLINING).
     */
    private String depreciationMethod;

    /**
     * Useful life in months.
     */
    private Integer usefulLifeMonths;

    /**
     * Residual rate for depreciation calculation. Defaults to 0.05 (5%).
     */
    private BigDecimal residualRate = BigDecimal.valueOf(0.05);

    /**
     * Usage status: IN_USE, IDLE, DISPOSED, etc. Defaults to IN_USE.
     */
    private String useStatus = "IN_USE";

    /**
     * Department currently using the asset.
     */
    private String useDepartment;

    /**
     * Person responsible for the asset.
     */
    private String custodian;

    /**
     * Physical location of the asset.
     */
    private String location;

    /**
     * Detailed address.
     */
    private String address;

    /**
     * Date of purchase.
     */
    private LocalDate purchaseDate;

    /**
     * Date of registration in the system.
     */
    private LocalDate registrationDate;

    /**
     * Source type (e.g., PURCHASE, LEASE, TRANSFER).
     */
    private String sourceType;

    /**
     * Certificate / invoice number.
     */
    private String certificateNo;

    /**
     * Comma-separated photo IDs (for file service integration).
     */
    private String photoIds;

    /**
     * Additional remarks.
     */
    private String remark;
}
