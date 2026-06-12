package com.sasac.platform.asset.inspection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Inspection task entity.
 * <p>
 * Maps to the {@code inspection_task} table and represents a batch
 * inspection assignment for a specific user covering a set of assets.
 */
@Getter
@Setter
@TableName("inspection_task")
public class InspectionTask extends BaseEntity {

    /**
     * Human-readable name for this inspection task.
     */
    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    /**
     * User ID of the person assigned to perform the inspections.
     */
    @NotNull(message = "负责人不能为空")
    private Long assigneeId;

    /**
     * JSON array of asset IDs that this task covers.
     */
    @NotBlank(message = "巡检范围不能为空")
    private String assetScope;

    /**
     * Start date of the inspection period.
     */
    private LocalDate startDate;

    /**
     * End date of the inspection period.
     */
    private LocalDate endDate;

    /**
     * Task status: PENDING, IN_PROGRESS, COMPLETED.
     */
    private String status = "PENDING";

    /**
     * Total number of assets to be inspected.
     */
    private Integer totalCount = 0;

    /**
     * Number of assets that have been inspected so far.
     */
    private Integer completedCount = 0;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
