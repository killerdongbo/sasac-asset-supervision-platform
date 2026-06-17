package com.sasac.platform.hr.dto;

import lombok.Data;

/**
 * DTO for querying employees with filters and pagination.
 */
@Data
public class EmployeeQueryDTO {

    private Long orgId;

    private Long deptId;

    private String keyword;

    private String status;

    private String employmentType;

    private int page = 1;

    private int limit = 20;
}
