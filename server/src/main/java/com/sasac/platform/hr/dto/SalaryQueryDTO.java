package com.sasac.platform.hr.dto;

import lombok.Data;

/**
 * DTO for querying salary records with filters and pagination.
 */
@Data
public class SalaryQueryDTO {

    private Long employeeId;

    private Long tenantId;

    private Integer salaryYear;

    private Integer salaryMonth;

    private int page = 1;

    private int limit = 20;
}
