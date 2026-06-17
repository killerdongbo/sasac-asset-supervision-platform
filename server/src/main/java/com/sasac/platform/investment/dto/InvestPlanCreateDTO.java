package com.sasac.platform.investment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating an annual investment plan.
 */
@Data
public class InvestPlanCreateDTO {

    @NotNull(message = "计划年份不能为空")
    private Integer planYear;

    private String planName;

    private BigDecimal totalBudget;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;
}
