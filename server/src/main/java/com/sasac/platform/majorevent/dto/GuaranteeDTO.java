package com.sasac.platform.majorevent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating or updating a guarantee record.
 */
@Getter
@Setter
public class GuaranteeDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private Long eventId;

    @NotBlank(message = "担保类型不能为空")
    private String guaranteeType;

    @NotBlank(message = "受益人不能为空")
    private String beneficiary;

    @NotNull(message = "担保金额不能为空")
    private BigDecimal guaranteeAmount;

    private LocalDate guaranteePeriodStart;

    private LocalDate guaranteePeriodEnd;

    private String counterGuarantee;

    @NotBlank(message = "风险等级不能为空")
    private String riskLevel;

    private String status;
}
