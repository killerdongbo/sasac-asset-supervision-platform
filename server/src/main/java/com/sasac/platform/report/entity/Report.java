package com.sasac.platform.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Report entity representing a generated asset snapshot report.
 * <p>
 * Stores aggregated asset data as a JSON snapshot in {@code snapshotData},
 * supporting the reporting workflow with status tracking (DRAFT, SUBMITTED,
 * REVIEWED, ACCEPTED, REJECTED) and versioning.
 */
@Getter
@Setter
@TableName("report")
public class Report extends BaseEntity {

    /**
     * Type of the report (e.g., ASSET_SUMMARY, DEPRECIATION).
     */
    private String reportType;

    /**
     * Reporting period (e.g., 2025-Q1, 2025-01).
     */
    private String period;

    /**
     * Organization this report belongs to.
     */
    private Long orgId;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Submit status: DRAFT, SUBMITTED, REVIEWED, ACCEPTED, REJECTED.
     */
    private String submitStatus = "DRAFT";

    /**
     * Report version number, incremented on re-generation.
     */
    private Integer version = 1;

    /**
     * JSON snapshot of the aggregated report data.
     */
    private String snapshotData;

    /**
     * ID of the reviewer who reviewed this report.
     */
    private String reviewerId;

    /**
     * Review remarks or comments.
     */
    private String reviewRemark;
}
