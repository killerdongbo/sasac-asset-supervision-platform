package com.sasac.platform.supervision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO for assigning a rectification task.
 */
@Getter
@Setter
public class RectificationDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "任务标题不能为空")
    private String taskTitle;

    private Long assigneeId;

    private String assigneeName;

    private LocalDate deadline;

    private String rectificationMeasure;
}
