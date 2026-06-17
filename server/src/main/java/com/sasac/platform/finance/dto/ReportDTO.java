package com.sasac.platform.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for submitting a financial report.
 */
@Data
public class ReportDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotBlank(message = "报表类型不能为空")
    private String reportType;

    @NotNull(message = "年份不能为空")
    private Integer reportYear;

    @NotNull(message = "期间不能为空")
    private Integer reportPeriod;

    @NotBlank(message = "报表数据不能为空")
    private String reportData;
}
