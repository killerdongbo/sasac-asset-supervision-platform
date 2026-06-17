package com.sasac.platform.investment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for recording an investment exit.
 */
@Data
public class InvestExitDTO {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private LocalDate exitDate;

    private BigDecimal exitAmount;

    private String exitMethod;
}
