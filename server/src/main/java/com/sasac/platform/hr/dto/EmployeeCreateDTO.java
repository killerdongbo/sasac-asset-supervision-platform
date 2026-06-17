package com.sasac.platform.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating a new employee.
 */
@Data
public class EmployeeCreateDTO {

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "工号不能为空")
    private String employeeNo;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String gender;

    private String idCard;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private String education;

    private LocalDate entryDate;

    private Long deptId;

    private String position;

    private String title;

    private String employmentType;
}
