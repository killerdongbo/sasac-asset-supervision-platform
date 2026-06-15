package com.sasac.platform.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity mapping the natural_resource table.
 * <p>
 * Stores natural resource asset details for the
 * state-owned assets supervision report.
 */
@Getter
@Setter
@TableName("natural_resource")
public class NaturalResource extends BaseEntity {

    /** Name of the natural resource. */
    private String resourceName;

    /** Type of resource. */
    private String resourceType;

    /** Location. */
    private String location;

    /** Area or reserve amount. */
    private String areaOrReserve;

    /** Measurement unit. */
    private String unit;

    /** Certificate/title number. */
    private String certificateNo;

    /** Acquisition method. */
    private String acquisitionMethod;

    /** Useful life in years. */
    private Integer usefulLifeYears;

    /** Book value (yuan). */
    private BigDecimal bookValue;

    /** Appraised value (yuan). */
    private BigDecimal appraisedValue;

    /** Whether developed/utilized. */
    private Boolean isDeveloped;

    /** Organization ID this record belongs to. */
    private Long orgId;

    /** Tenant ID for multi-tenancy. */
    private Long tenantId;

    /** Additional remarks. */
    private String remark;
}
