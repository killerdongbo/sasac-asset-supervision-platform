package com.sasac.platform.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing an individual approval task within a workflow instance.
 * <p>
 * Each task corresponds to a node in the workflow graph and tracks who approved,
 * rejected, or is pending to act on that node.
 */
@Getter
@Setter
@TableName("workflow_task")
public class WorkflowTask extends BaseEntity {

    /**
     * The ID of the workflow instance this task belongs to.
     */
    private Long instanceId;

    /**
     * The node ID in the workflow graph this task corresponds to.
     */
    private String nodeId;

    /**
     * The role of the approver (e.g. "FINANCE_MANAGER", "DEPARTMENT_HEAD").
     */
    private String approverRole;

    /**
     * The user ID of the approver who acted on this task.
     */
    private Long approverId;

    /**
     * Action taken: PENDING, APPROVED, REJECTED, RETURNED.
     */
    private String action;

    /**
     * Optional remark or comment from the approver.
     */
    private String remark;

    /**
     * Timestamp when this task was completed (approved/rejected).
     */
    private LocalDateTime completedAt;
}
