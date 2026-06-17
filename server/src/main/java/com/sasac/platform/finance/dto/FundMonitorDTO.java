package com.sasac.platform.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for recording a fund transaction.
 */
@Data
public class FundMonitorDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    private String transactionNo;

    @NotNull(message = "交易日期不能为空")
    private LocalDateTime transactionDate;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private String payer;
    private String payee;
    private String purpose;
}
