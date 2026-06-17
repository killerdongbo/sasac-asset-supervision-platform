package com.sasac.platform.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Employee change record entity tracking modifications to employee data.
 * <p>
 * Maps to the {@code hr_employee_change} table and records each change event
 * (promotion, transfer, title change, etc.) for audit trail purposes.
 */
@Getter
@Setter
@TableName("hr_employee_change")
public class HrEmployeeChange extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Reference to the employee record.
     */
    private Long employeeId;

    /**
     * Type of change (PROMOTION, TRANSFER, TITLE_CHANGE, SALARY_ADJUSTMENT, etc.).
     */
    private String changeType;

    /**
     * Value before the change.
     */
    private String beforeValue;

    /**
     * Value after the change.
     */
    private String afterValue;

    /**
     * Date the change takes effect.
     */
    private LocalDate effectiveDate;

    /**
     * Reason or description for the change.
     */
    private String reason;

    /**
     * Associated approval workflow instance ID.
     */
    private Long approvalInstanceId;

    /**
     * Change status (PENDING, APPROVED, REJECTED).
     */
    private String status;
}
