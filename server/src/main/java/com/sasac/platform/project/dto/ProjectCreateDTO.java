package com.sasac.platform.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new project.
 */
@Data
public class ProjectCreateDTO {

    @NotBlank(message = "项目名称不能为空")
    private String name;

    private String projectType;

    private String category;

    private BigDecimal budgetTotal;

    private LocalDate startDate;

    private LocalDate plannedEndDate;

    private String managerName;

    private String department;

    private String description;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;
}
