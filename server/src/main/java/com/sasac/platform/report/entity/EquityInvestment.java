package com.sasac.platform.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity mapping the equity_investment table.
 * <p>
 * Stores equity investment (equity-based external investment) details
 * for the state-owned assets supervision report.
 */
@Getter
@Setter
@TableName("equity_investment")
public class EquityInvestment extends BaseEntity {

    /** Name of the invested enterprise. */
    private String enterpriseName;

    /** Unified social credit code of the invested enterprise. */
    private String creditCode;

    /** Date of investment. */
    private LocalDate investDate;

    /** Investment method (e.g., cash, asset contribution). */
    private String investMethod;

    /** Shareholding ratio (percentage). */
    private BigDecimal shareRatio;

    /** Investment amount (yuan). */
    private BigDecimal investAmount;

    /** Cumulative dividend received (yuan). */
    private BigDecimal cumulativeDividend;

    /** Book value (yuan). */
    private BigDecimal bookValue;

    /** Fair value (yuan). */
    private BigDecimal fairValue;

    /** Whether the investment is impaired. */
    private Boolean isImpaired;

    /** Impairment amount (yuan). */
    private BigDecimal impairmentAmount;

    /** Status of the invested enterprise. */
    private String enterpriseStatus;

    /** Organization ID this record belongs to. */
    private Long orgId;

    /** Tenant ID for multi-tenancy. */
    private Long tenantId;

    /** Additional remarks. */
    private String remark;
}
