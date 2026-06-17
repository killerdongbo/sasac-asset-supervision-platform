package com.sasac.platform.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating an employee change record.
 */
@Data
public class EmployeeChangeDTO {

    @NotNull(message = "人员ID不能为空")
    private Long employeeId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "变更类型不能为空")
    private String changeType;

    private String beforeValue;

    private String afterValue;

    @NotNull(message = "生效日期不能为空")
    private LocalDate effectiveDate;

    private String reason;
}
