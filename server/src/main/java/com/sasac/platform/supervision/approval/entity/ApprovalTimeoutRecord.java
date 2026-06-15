package com.sasac.platform.supervision.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("approval_timeout_record")
public class ApprovalTimeoutRecord extends BaseEntity {

    private Long instanceId;
    private Integer nodeOrder;
    private String actionTaken;
    private String escalateToRole;
    private Long escalateToUserId;
}
