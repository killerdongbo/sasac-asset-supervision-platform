package com.sasac.platform.performance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for defining a performance indicator.
 */
@Data
public class IndicatorDefDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotBlank(message = "指标编码不能为空")
    private String indicatorCode;

    @NotBlank(message = "指标名称不能为空")
    private String indicatorName;

    private String category;

    @NotNull(message = "权重不能为空")
    private BigDecimal weight;

    @NotNull(message = "目标值不能为空")
    private BigDecimal targetValue;

    private BigDecimal actualValue;

    private Integer cycleYear;
}
