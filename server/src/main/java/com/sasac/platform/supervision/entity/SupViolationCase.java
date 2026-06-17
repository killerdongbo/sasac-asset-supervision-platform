package com.sasac.platform.supervision.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Violation case entity for tracking business violations.
 * <p>
 * Maps to the {@code sup_violation_case} table. Each case documents
 * an investigation into suspected violations, including asset loss
 * assessment and final punishment decisions.
 */
@Getter
@Setter
@TableName("sup_violation_case")
public class SupViolationCase extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Auto-generated case number (e.g. WJ-20240617120000).
     */
    private String caseNo;

    /**
     * Case title describing the violation.
     */
    private String caseTitle;

    /**
     * Violation type: FRAUD, EMBEZZLEMENT, NEGLIGENCE, POLICY_VIOLATION, OTHER.
     */
    private String violationType;

    /**
     * User ID of the suspected violator.
     */
    private Long suspectId;

    /**
     * Display name of the suspect.
     */
    private String suspectName;

    /**
     * Monetary value of assets lost due to the violation.
     */
    private BigDecimal assetLoss;

    /**
     * Textual report of investigation findings.
     */
    private String investigationResult;

    /**
     * Description of the punishment decision.
     */
    private String punishmentDecision;

    /**
     * Status: INVESTIGATING, RESOLVED, DISMISSED.
     */
    private String status = "INVESTIGATING";
}
