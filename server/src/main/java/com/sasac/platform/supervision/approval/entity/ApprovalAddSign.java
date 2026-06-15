package com.sasac.platform.supervision.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("approval_add_sign")
public class ApprovalAddSign extends BaseEntity {

    private Long instanceId;
    private Integer nodeOrder;
    private Long addSignUserId;
    private String addSignUserName;
    private String reason;
    private Boolean approved;
    private String remark;
}
