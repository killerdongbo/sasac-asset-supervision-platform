package com.sasac.platform.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for financial indicator calculation request.
 */
@Data
public class IndicatorDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotNull(message = "年份不能为空")
    private Integer periodYear;

    @NotNull(message = "月份不能为空")
    private Integer periodMonth;
}
