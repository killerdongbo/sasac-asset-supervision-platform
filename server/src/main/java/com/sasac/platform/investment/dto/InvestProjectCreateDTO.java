package com.sasac.platform.investment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating an investment project.
 */
@Data
public class InvestProjectCreateDTO {

    private Long planId;

    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    private String investType;

    private String industry;

    private String region;

    private BigDecimal investAmount;

    private BigDecimal equityPct;

    private String targetCompany;

    private String targetDescription;

    private BigDecimal expectedRoi;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;
}
