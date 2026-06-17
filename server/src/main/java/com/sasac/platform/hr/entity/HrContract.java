package com.sasac.platform.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Contract entity representing an employment contract.
 * <p>
 * Maps to the {@code hr_contract} table and tracks the lifecycle of each
 * employment contract, including start/end dates and renewal status.
 */
@Getter
@Setter
@TableName("hr_contract")
public class HrContract extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Reference to the employee record.
     */
    private Long employeeId;

    /**
     * Unique contract number.
     */
    private String contractNo;

    /**
     * Contract type (FIXED_TERM, UNLIMITED, PROBATION, PROJECT_BASED, etc.).
     */
    private String contractType;

    /**
     * Date the contract was signed.
     */
    private LocalDate signDate;

    /**
     * Contract start date.
     */
    private LocalDate startDate;

    /**
     * Contract end date (null if unlimited).
     */
    private LocalDate endDate;

    /**
     * Whether this is an unlimited / permanent contract.
     */
    private Boolean isUnlimited;

    /**
     * Contract status (ACTIVE, EXPIRED, TERMINATED, RENEWED).
     */
    private String status;

    /**
     * Summary of key contract terms.
     */
    private String termsSummary;

    /**
     * Comma-separated attachment file IDs.
     */
    private String attachmentIds;
}
