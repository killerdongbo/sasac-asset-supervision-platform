package com.sasac.platform.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a workflow definition (graph-based process template).
 * <p>
 * Stores the visual graph JSON and metadata for a workflow process definition,
 * used by the visual workflow designer to render and execute business processes.
 */
@Getter
@Setter
@TableName("workflow_def")
public class WorkflowDef extends BaseEntity {

    /**
     * Name of the workflow definition.
     */
    private String name;

    /**
     * Business type associated with this workflow.
     */
    private String bizType;

    /**
     * JSON representation of the workflow graph (nodes and edges).
     */
    private String graphJson;

    /**
     * Status: ACTIVE or INACTIVE.
     */
    private String status;

    /**
     * Optional description of this workflow definition.
     */
    private String description;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
