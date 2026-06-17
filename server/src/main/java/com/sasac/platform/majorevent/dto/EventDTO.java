package com.sasac.platform.majorevent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for creating or updating a major event.
 */
@Getter
@Setter
public class EventDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "所属组织不能为空")
    private Long orgId;

    @NotBlank(message = "事件类型不能为空")
    private String eventType;

    @NotBlank(message = "事件标题不能为空")
    private String title;

    private String description;

    private String impactAssessment;

    private String handlingPlan;

    private LocalDateTime reportedAt;
}
