package com.sasac.platform.asset.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Inventory task entity.
 * <p>
 * Maps to the {@code inventory_task} table and represents a task
 * to physically verify a set of assets against their book records.
 */
@Getter
@Setter
@TableName("inventory_task")
public class InventoryTask extends BaseEntity {

    /**
     * Name of the inventory task.
     */
    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    /**
     * User ID of the person assigned to perform the inventory.
     */
    @NotNull(message = "负责人不能为空")
    private Long assigneeId;

    /**
     * Scope type: ORG, LOCATION, CATEGORY.
     */
    @NotBlank(message = "盘点范围类型不能为空")
    private String scopeType = "ORG";

    /**
     * Scope value (org ID, location, or category code).
     */
    private String scopeValue;

    /**
     * Start date of the inventory period.
     */
    private LocalDate startDate;

    /**
     * End date of the inventory period.
     */
    private LocalDate endDate;

    /**
     * Task status: PENDING, IN_PROGRESS, COMPLETED.
     */
    private String status = "PENDING";

    /**
     * Total number of assets to be inventoried.
     */
    private Integer totalCount = 0;

    /**
     * Number of assets that have been inventoried so far.
     */
    private Integer completedCount = 0;

    /**
     * Number of discrepancies (diffs) found so far.
     */
    private Integer diffCount = 0;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
