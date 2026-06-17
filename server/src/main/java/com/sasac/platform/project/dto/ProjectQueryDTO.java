package com.sasac.platform.project.dto;

import lombok.Data;

/**
 * DTO for querying projects with filters and pagination.
 */
@Data
public class ProjectQueryDTO {

    private String keyword;

    private String status;

    private String projectType;

    private int page = 1;

    private int limit = 20;
}
