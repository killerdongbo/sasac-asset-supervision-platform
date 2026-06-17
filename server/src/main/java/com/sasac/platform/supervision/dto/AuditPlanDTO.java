package com.sasac.platform.supervision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating or updating an audit plan.
 */
@Getter
@Setter
public class AuditPlanDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "所属组织不能为空")
    private Long orgId;

    @NotNull(message = "计划年份不能为空")
    private Integer planYear;

    @NotBlank(message = "计划名称不能为空")
    private String planName;

    private String auditScope;

    private String auditTeam;

    private String status;
}
