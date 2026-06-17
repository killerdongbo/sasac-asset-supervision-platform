package com.sasac.platform.majorevent.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Guarantee entity tracking guarantees and sureties provided by the organization.
 * <p>
 * Maps to the {@code me_guarantee} table. Each record documents a guarantee
 * agreement, including the beneficiary, amounts, validity period, and risk level.
 */
@Getter
@Setter
@TableName("me_guarantee")
public class MeGuarantee extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Reference to the associated major event.
     */
    private Long eventId;

    /**
     * Type of guarantee: LOAN_GUARANTEE, PERFORMANCE_BOND, BID_BOND, ADVANCE_PAYMENT, OTHER.
     */
    private String guaranteeType;

    /**
     * Name of the beneficiary receiving the guarantee.
     */
    private String beneficiary;

    /**
     * Amount covered by the guarantee.
     */
    private BigDecimal guaranteeAmount;

    /**
     * Start date of the guarantee validity period.
     */
    private LocalDate guaranteePeriodStart;

    /**
     * End date of the guarantee validity period.
     */
    private LocalDate guaranteePeriodEnd;

    /**
     * Description of counter-guarantee or collateral provided.
     */
    private String counterGuarantee;

    /**
     * Risk level: LOW, MEDIUM, HIGH, CRITICAL.
     */
    private String riskLevel;

    /**
     * Status: ACTIVE, EXPIRED, TERMINATED, CLAIMED.
     */
    private String status = "ACTIVE";
}
