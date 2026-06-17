package com.sasac.platform.decision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for creating a new decision supervision task.
 */
@Data
public class SupervisionCreateDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "任务标题不能为空")
    private String taskTitle;

    private Long assigneeId;

    private String assigneeName;

    private String deadline;
}
