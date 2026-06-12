package com.sasac.platform.supervision.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing an approval definition (template).
 * <p>
 * Defines the approval workflow template for a specific business type,
 * including the ordered list of approval nodes that must be followed.
 */
@Getter
@Setter
@TableName("approval_def")
public class ApprovalDef extends BaseEntity {

    /**
     * Name of the approval definition (e.g. "资产处置审批流程").
     */
    private String defName;

    /**
     * Business type: DISPOSAL, TRANSFER, PURCHASE, INVENTORY_DIFF, MAINTENANCE, REPORT.
     */
    private String bizType;

    /**
     * Status: ACTIVE or INACTIVE.
     */
    private String status;

    /**
     * Optional description of this approval definition.
     */
    private String description;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
