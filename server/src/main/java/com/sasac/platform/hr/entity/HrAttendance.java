package com.sasac.platform.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Attendance entity recording daily check-in/check-out for employees.
 * <p>
 * Maps to the {@code hr_attendance} table. Each row represents one day
 * of attendance for a single employee.
 */
@Getter
@Setter
@TableName("hr_attendance")
public class HrAttendance extends BaseEntity {

    private Long tenantId;

    private Long employeeId;

    private LocalDate attDate;

    private LocalTime checkIn;

    private LocalTime checkOut;

    private String status;
}
