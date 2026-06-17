package com.sasac.platform.supervision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for recording an audit finding.
 */
@Getter
@Setter
public class FindingDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private Long planId;

    @NotBlank(message = "问题标题不能为空")
    private String title;

    @NotBlank(message = "严重程度不能为空")
    private String severity;

    private String description;

    private String evidenceIds;
}
