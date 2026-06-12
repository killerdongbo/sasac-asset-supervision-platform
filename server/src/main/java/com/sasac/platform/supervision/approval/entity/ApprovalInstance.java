package com.sasac.platform.supervision.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a running (or completed) approval instance.
 * <p>
 * Tracks the progress of a single approval process through its workflow nodes,
 * storing the results of each approval step as a JSON array in {@code nodeResults}.
 */
@Getter
@Setter
@TableName("approval_instance")
public class ApprovalInstance extends BaseEntity {

    /**
     * The ID of the approval definition this instance follows.
     */
    private Long defId;

    /**
     * The ID of the business record being approved.
     */
    private Long bizId;

    /**
     * The business type matching the approval definition.
     */
    private String bizType;

    /**
     * Current status: PENDING, APPROVED, REJECTED, or CANCELLED.
     */
    private String status;

    /**
     * The current node order (1-based) waiting for approval.
     */
    private Integer currentNode;

    /**
     * JSON array of node results stored as TEXT.
     * Each element contains approverId, approverRole, approved, remark, timestamp.
     */
    private String nodeResults;

    /**
     * The user ID of the submitter who initiated this approval.
     */
    private Long submitterId;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
