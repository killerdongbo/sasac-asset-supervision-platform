package com.sasac.platform.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a property assessment filing.
 */
@Data
public class AssessmentDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private Long registrationId;

    @NotBlank(message = "评估编号不能为空")
    private String assessNo;

    private String assessPurpose;

    private String assessAgency;

    private String assessMethod;

    private BigDecimal bookValue;

    private BigDecimal assessedValue;

    private String assessReportIds;

    private LocalDate assessDate;
}
