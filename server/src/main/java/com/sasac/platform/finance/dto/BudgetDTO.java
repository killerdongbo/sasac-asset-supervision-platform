package com.sasac.platform.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a budget entry.
 */
@Data
public class BudgetDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotNull(message = "预算年份不能为空")
    private Integer budgetYear;

    private String budgetType;

    @NotNull(message = "预算金额不能为空")
    private BigDecimal plannedAmount;
}
