package com.sasac.platform.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a running (or completed) workflow instance.
 * <p>
 * Tracks the execution state of a single workflow process,
 * including current node positions and business context data.
 */
@Getter
@Setter
@TableName("workflow_instance")
public class WorkflowInstance extends BaseEntity {

    /**
     * The ID of the workflow definition this instance follows.
     */
    private Long defId;

    /**
     * The ID of the business record associated with this instance.
     */
    private Long bizId;

    /**
     * The business type matching the workflow definition.
     */
    private String bizType;

    /**
     * Current status: PENDING, PROCESSING, COMPLETED, REJECTED, CANCELLED.
     */
    private String status;

    /**
     * Comma-separated IDs of the current active nodes.
     */
    private String currentNodeIds;

    /**
     * JSON context data for the workflow execution.
     */
    private String contextJson;

    /**
     * The user ID of the submitter who initiated this workflow.
     */
    private Long submitterId;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
