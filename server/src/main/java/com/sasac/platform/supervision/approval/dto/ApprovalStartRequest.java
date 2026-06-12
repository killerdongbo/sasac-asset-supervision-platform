package com.sasac.platform.supervision.approval.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for starting a new approval instance.
 */
@Getter
@Setter
public class ApprovalStartRequest {

    @NotNull(message = "审批定义ID不能为空")
    private Long defId;

    @NotNull(message = "业务记录ID不能为空")
    private Long bizId;

    @NotNull(message = "业务类型不能为空")
    private String bizType;

    @NotNull(message = "提交人ID不能为空")
    private Long submitterId;
}
