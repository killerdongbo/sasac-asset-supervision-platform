package com.sasac.platform.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity mapping the data_asset table.
 * <p>
 * Stores data resource/asset information for the
 * state-owned assets supervision report.
 */
@Getter
@Setter
@TableName("data_asset")
public class DataAsset extends BaseEntity {

    /** Name of the data asset. */
    private String dataName;

    /** Type of data. */
    private String dataType;

    /** Volume of data. */
    private String dataVolume;

    /** Storage method. */
    private String storageMethod;

    /** Security level classification. */
    private String securityLevel;

    /** Whether included in the balance sheet. */
    private Boolean isInBalanceSheet;

    /** Value recorded in the balance sheet (yuan). */
    private BigDecimal balanceSheetValue;

    /** Usage scenario description. */
    private String usageScenario;

    /** Organization ID this record belongs to. */
    private Long orgId;

    /** Tenant ID for multi-tenancy. */
    private Long tenantId;

    /** Additional remarks. */
    private String remark;
}
