package com.sasac.platform.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity mapping the franchise_right table.
 * <p>
 * Stores franchise rights information for the
 * state-owned assets supervision report.
 */
@Getter
@Setter
@TableName("franchise_right")
public class FranchiseRight extends BaseEntity {

    /** Name of the franchise right. */
    private String rightName;

    /** Authorizing entity. */
    private String authorizer;

    /** Start date of the franchise. */
    private LocalDate startDate;

    /** End date of the franchise. */
    private LocalDate endDate;

    /** Authorized area/region. */
    private String authorizedArea;

    /** Business scope. */
    private String businessScope;

    /** Authorization fee (yuan). */
    private BigDecimal authorizationFee;

    /** Annual fee (yuan). */
    private BigDecimal annualFee;

    /** Whether the franchise has expired. */
    private Boolean isExpired;

    /** Organization ID this record belongs to. */
    private Long orgId;

    /** Tenant ID for multi-tenancy. */
    private Long tenantId;

    /** Additional remarks. */
    private String remark;
}
