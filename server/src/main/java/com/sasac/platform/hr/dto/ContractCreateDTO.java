package com.sasac.platform.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating a new employment contract.
 */
@Data
public class ContractCreateDTO {

    @NotNull(message = "人员ID不能为空")
    private Long employeeId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    private String contractType = "LABOR";

    private LocalDate signDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isUnlimited = false;

    private String termsSummary;
}
