package com.sasac.platform.hr.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for recording employee attendance.
 */
@Data
public class AttendanceRecordDTO {

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "日期不能为空")
    private LocalDate attDate;

    private LocalTime checkIn;

    private LocalTime checkOut;
}
