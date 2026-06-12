package com.sasac.platform.supervision.approval.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for approving or rejecting an approval instance.
 */
@Getter
@Setter
public class ApprovalActionRequest {

    @NotNull(message = "审批人ID不能为空")
    private Long approverId;

    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    private String remark;
}
