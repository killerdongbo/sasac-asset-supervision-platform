package com.sasac.platform.majorevent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating or updating a lawsuit record.
 */
@Getter
@Setter
public class LawsuitDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private Long eventId;

    private String caseNo;

    private String court;

    @NotBlank(message = "原告不能为空")
    private String plaintiff;

    @NotBlank(message = "被告不能为空")
    private String defendant;

    private BigDecimal claimAmount;

    private BigDecimal judgmentAmount;

    private String lawFirm;

    private String attorney;

    private String trialProgress;

    private LocalDate judgmentDate;

    private String status;
}
