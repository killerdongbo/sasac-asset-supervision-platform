package com.sasac.platform.supervision.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a single approval node within an approval definition.
 * <p>
 * Each node defines one step in the approval workflow, including who must
 * approve and under what conditions.
 */
@Getter
@Setter
@TableName("approval_node")
public class ApprovalNode extends BaseEntity {

    /**
     * The ID of the approval definition this node belongs to.
     */
    private Long defId;

    /**
     * The order of this node in the approval sequence (1-based).
     */
    private Integer nodeOrder;

    /**
     * The role code of the approver (e.g. DEPARTMENT_HEAD, FINANCE_MANAGER, CEO).
     */
    private String approverRole;

    /**
     * Optional SpEL-like condition expression for conditional routing
     * (e.g. "asset.originalValue > 100000").
     */
    private String conditionExpr;

    /**
     * Whether this node allows rejection. Defaults to true.
     */
    private Boolean canReject;

    /**
     * Timeout in hours for this approval node. 0 means no timeout.
     */
    private Integer timeoutHours;
}
