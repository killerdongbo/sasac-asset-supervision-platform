package com.sasac.platform.performance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for managing salary budget.
 */
@Data
public class SalaryBudgetDTO {

    private Long id;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotNull(message = "预算年份不能为空")
    private Integer budgetYear;

    @NotNull(message = "预算总额不能为空")
    private BigDecimal totalBudget;

    private BigDecimal approvedBudget;

    private BigDecimal actualPaid;

    private String adjustmentReason;

    private String status;
}
