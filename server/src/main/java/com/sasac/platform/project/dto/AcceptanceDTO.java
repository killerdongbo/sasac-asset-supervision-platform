package com.sasac.platform.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for recording a project acceptance / completion review.
 */
@Data
public class AcceptanceDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private String acceptanceNo;

    private String acceptanceType;

    private String result;

    private BigDecimal score;

    private String reviewOpinion;

    private String reviewerName;

    private LocalDate acceptanceDate;
}
