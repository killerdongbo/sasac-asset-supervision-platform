package com.sasac.platform.hr.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for scoring a performance review.
 */
@Data
public class PerformanceScoreDTO {

    @NotNull(message = "自评分不能为空")
    @DecimalMin(value = "0.0", message = "自评分不能小于0")
    @DecimalMax(value = "100.0", message = "自评分不能超过100")
    private BigDecimal selfScore;

    @NotNull(message = "经理评分不能为空")
    @DecimalMin(value = "0.0", message = "经理评分不能小于0")
    @DecimalMax(value = "100.0", message = "经理评分不能超过100")
    private BigDecimal managerScore;
}
