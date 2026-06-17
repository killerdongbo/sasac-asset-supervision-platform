package com.sasac.platform.performance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a performance incentive.
 */
@Data
public class IncentiveDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "激励类型不能为空")
    private String incentiveType;

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @NotNull(message = "授予日期不能为空")
    private LocalDate grantDate;

    private Integer vestingPeriod;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private String status;
}
