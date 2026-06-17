package com.sasac.platform.decision.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 决策事项 - 记录三重一大决策的具体事项。
 * <p>
 * 映射到 {@code decision_item} 表，包含事项的基本信息、
 * 类型、紧急程度及审批状态。
 */
@Getter
@Setter
@TableName("decision_item")
public class DecisionItem extends BaseEntity {

    private Long tenantId;

    private Long orgId;

    private String itemNo;

    private String itemType;

    private String title;

    private String description;

    private BigDecimal amount;

    private Long proposerId;

    private String proposerName;

    private String department;

    private String urgency;

    private Long approvalInstanceId;

    private String status;
}
