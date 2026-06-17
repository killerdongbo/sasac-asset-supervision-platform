package com.sasac.platform.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for recording a project progress entry.
 */
@Data
public class ProgressRecordDTO {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "汇报日期不能为空")
    private LocalDate reportDate;

    private BigDecimal progressPct;

    private String completedWork;

    private String pendingWork;

    private String issues;

    private String nextPlan;
}
