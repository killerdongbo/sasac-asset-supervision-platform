package com.sasac.platform.investment.dto;

import lombok.Data;

/**
 * DTO for querying investment projects with filters and pagination.
 */
@Data
public class InvestProjectQueryDTO {

    private String keyword;

    private String investType;

    private String status;

    private String phase;

    private int page = 1;

    private int limit = 20;
}
