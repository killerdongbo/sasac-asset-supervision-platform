package com.sasac.platform.decision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for creating a new decision meeting.
 */
@Data
public class MeetingCreateDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotBlank(message = "会议标题不能为空")
    private String title;

    private String agenda;

    @NotNull(message = "会议时间不能为空")
    private LocalDateTime scheduledAt;

    private String location;

    private String host;

    private String attendees;
}
