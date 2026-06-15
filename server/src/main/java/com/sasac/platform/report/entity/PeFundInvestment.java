package com.sasac.platform.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity mapping the pe_fund_investment table.
 * <p>
 * Stores private equity fund investment details for the
 * state-owned assets supervision report.
 */
@Getter
@Setter
@TableName("pe_fund_investment")
public class PeFundInvestment extends BaseEntity {

    /** Name of the fund. */
    private String fundName;

    /** Fund manager. */
    private String fundManager;

    /** Fund type (e.g., equity, venture capital). */
    private String fundType;

    /** Subscribed amount (yuan). */
    private BigDecimal subscribedAmount;

    /** Paid-in amount (yuan). */
    private BigDecimal paidAmount;

    /** Investment date. */
    private LocalDate investDate;

    /** Fund duration. */
    private String fundDuration;

    /** Current net asset value (yuan). */
    private BigDecimal currentNav;

    /** Cumulative return (yuan). */
    private BigDecimal cumulativeReturn;

    /** Whether there is a violation. */
    private Boolean isViolation;

    /** Filing/record number. */
    private String recordNo;

    /** Organization ID this record belongs to. */
    private Long orgId;

    /** Tenant ID for multi-tenancy. */
    private Long tenantId;

    /** Additional remarks. */
    private String remark;
}
