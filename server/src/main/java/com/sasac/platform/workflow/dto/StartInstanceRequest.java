package com.sasac.platform.workflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for starting a new workflow instance.
 */
@Getter
@Setter
public class StartInstanceRequest {

    /**
     * ID of the business record the workflow instance is associated with.
     */
    @NotNull
    private Long bizId;

    /**
     * Business type matching the workflow definition's bizType.
     */
    @NotNull
    private String bizType;

    /**
     * Optional JSON context data for the workflow execution.
     */
    private String contextJson;
}
