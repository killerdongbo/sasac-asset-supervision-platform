package com.sasac.platform.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity mapping the cash_account table.
 * <p>
 * Stores monetary fund (cash account) details for the
 * state-owned assets supervision report.
 */
@Getter
@Setter
@TableName("cash_account")
public class CashAccount extends BaseEntity {

    /** Account name. */
    private String accountName;

    /** Bank name. */
    private String bankName;

    /** Bank account number. */
    private String accountNo;

    /** Currency type (default CNY). */
    private String currency;

    /** Book balance (yuan). */
    private BigDecimal bookBalance;

    /** Bank statement balance (yuan). */
    private BigDecimal statementBalance;

    /** Difference amount (yuan). */
    private BigDecimal diffAmount;

    /** Reason for the difference. */
    private String diffReason;

    /** Account type. */
    private String accountType;

    /** Whether the account is restricted. */
    private Boolean isRestricted;

    /** Restricted amount (yuan). */
    private BigDecimal restrictedAmount;

    /** Reconciliation date. */
    private LocalDate reconciliationDate;

    /** Organization ID this record belongs to. */
    private Long orgId;

    /** Tenant ID for multi-tenancy. */
    private Long tenantId;

    /** Additional remarks. */
    private String remark;
}
