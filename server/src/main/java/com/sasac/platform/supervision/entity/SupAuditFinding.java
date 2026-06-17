package com.sasac.platform.supervision.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Audit finding entity representing issues discovered during an audit.
 * <p>
 * Maps to the {@code sup_audit_finding} table. Findings are linked to
 * an audit plan and can trigger rectification tasks.
 */
@Getter
@Setter
@TableName("sup_audit_finding")
public class SupAuditFinding extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Reference to the audit plan that identified this finding.
     */
    private Long planId;

    /**
     * Auto-generated finding number (e.g. FX-20240001).
     */
    private String findingNo;

    /**
     * Short finding title.
     */
    private String title;

    /**
     * Severity: CRITICAL, HIGH, MEDIUM, LOW.
     */
    private String severity;

    /**
     * Detailed description of the finding.
     */
    private String description;

    /**
     * Comma-separated IDs of attached evidence files.
     */
    private String evidenceIds;

    /**
     * Status: OPEN, RECTIFYING, VERIFIED, CLOSED.
     */
    private String status = "OPEN";
}
