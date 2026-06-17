package com.sasac.platform.supervision.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Rectification task entity for correcting audit findings.
 * <p>
 * Maps to the {@code sup_rectification} table. Each rectification is
 * linked to an audit finding and tracks assignment, deadline, measures,
 * and verification results.
 */
@Getter
@Setter
@TableName("sup_rectification")
public class SupRectification extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Reference to the audit finding being rectified.
     */
    private Long findingId;

    /**
     * Task title describing the rectification work.
     */
    private String taskTitle;

    /**
     * User ID of the person assigned to this task.
     */
    private Long assigneeId;

    /**
     * Display name of the assignee.
     */
    private String assigneeName;

    /**
     * Deadline for completing the rectification.
     */
    private LocalDate deadline;

    /**
     * Timestamp when the rectification was completed.
     */
    private LocalDateTime completedAt;

    /**
     * Description of measures taken to rectify the finding.
     */
    private String rectificationMeasure;

    /**
     * Verification result from the reviewer.
     */
    private String resultVerification;

    /**
     * Status: PENDING, IN_PROGRESS, COMPLETED, ESCALATED.
     */
    private String status = "PENDING";
}
