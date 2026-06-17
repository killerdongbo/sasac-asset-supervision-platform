package com.sasac.platform.investment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for recording a post-investment monitoring snapshot.
 */
@Data
public class InvestPostDTO {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private LocalDate reportDate;

    private BigDecimal revenue;

    private BigDecimal netProfit;

    private BigDecimal netAssets;

    private BigDecimal debtRatio;

    private String majorEvents;

    private String riskLevel;
}
