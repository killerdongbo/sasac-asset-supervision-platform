package com.sasac.platform.performance.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for adjusting a salary budget.
 */
@Getter
@Setter
public class AdjustBudgetDTO {

    private BigDecimal adjustment;

    private String reason;
}
