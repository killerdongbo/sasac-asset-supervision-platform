package com.sasac.platform.workflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for approving or rejecting a workflow task.
 */
@Getter
@Setter
public class ApproveRequest {

    /**
     * User ID of the approver.
     */
    @NotNull
    private Long approverId;

    /**
     * Approval decision: true for approved, false for rejected.
     */
    @NotNull
    private Boolean approved;

    /**
     * Optional remark or comment from the approver.
     */
    private String remark;
}
