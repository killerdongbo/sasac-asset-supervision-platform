package com.sasac.platform.hr.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for triggering salary calculation.
 */
@Data
public class SalaryCalculateDTO {

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "年份不能为空")
    private Integer salaryYear;

    @NotNull(message = "月份不能为空")
    private Integer salaryMonth;
}
