package com.sasac.platform.supervision.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Audit plan entity representing a yearly audit plan.
 * <p>
 * Maps to the {@code sup_audit_plan} table. Each plan defines the scope,
 * team, and schedule for audit activities within a given year.
 */
@Getter
@Setter
@TableName("sup_audit_plan")
public class SupAuditPlan extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Organization ID responsible for this plan.
     */
    private Long orgId;

    /**
     * The year this audit plan covers.
     */
    private Integer planYear;

    /**
     * Human-readable plan name.
     */
    private String planName;

    /**
     * Textual description of the audit scope.
     */
    private String auditScope;

    /**
     * Team members involved in the audit.
     */
    private String auditTeam;

    /**
     * Status: DRAFT, PUBLISHED, EXECUTING, COMPLETED.
     */
    private String status = "DRAFT";
}
