package com.sasac.platform.asset.dto;

import lombok.Data;

/**
 * DTO for querying assets with filters and pagination.
 */
@Data
public class AssetQueryDTO {

    private Long orgId;

    private String category;

    private String keyword;

    private String useStatus;

    private int page = 1;

    private int limit = 20;
}
