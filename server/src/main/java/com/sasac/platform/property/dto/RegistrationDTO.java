package com.sasac.platform.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating or updating a property registration.
 */
@Data
public class RegistrationDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotBlank(message = "登记类型不能为空")
    private String regType;

    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    private String propertyType;

    private String propertyOwner;

    private BigDecimal equityPct;

    private BigDecimal registeredCapital;

    private BigDecimal paidCapital;

    private LocalDate registrationDate;

    private String certNo;

    private String certFileIds;
}
