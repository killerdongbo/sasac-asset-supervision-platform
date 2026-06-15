package com.sasac.platform.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity mapping the creditor_rights table.
 * <p>
 * Stores creditor rights (claims) information for the
 * state-owned assets supervision report.
 */
@Getter
@Setter
@TableName("creditor_rights")
public class CreditorRights extends BaseEntity {

    /** Creditor rights code. */
    private String creditorCode;

    /** Name of the debtor. */
    private String debtorName;

    /** Type of rights/claim. */
    private String rightsType;

    /** Amount of the claim (yuan). */
    private BigDecimal amount;

    /** Date the claim was formed. */
    private LocalDate formedDate;

    /** Aging of the claim. */
    private String aging;

    /** Bad debt provision already made (yuan). */
    private BigDecimal badDebtProvision;

    /** Estimated recoverable amount (yuan). */
    private BigDecimal estimatedRecoverable;

    /** Collection status. */
    private String collectionStatus;

    /** Contract/certificate number. */
    private String contractNo;

    /** Whether litigation is involved. */
    private Boolean isLitigation;

    /** Organization ID this record belongs to. */
    private Long orgId;

    /** Tenant ID for multi-tenancy. */
    private Long tenantId;

    /** Additional remarks. */
    private String remark;
}
