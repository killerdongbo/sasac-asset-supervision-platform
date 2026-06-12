package com.sasac.platform.asset.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Inventory discrepancy (diff) entity.
 * <p>
 * Maps to the {@code inventory_diff} table and captures differences
 * between the book records and the physical inventory results.
 * Diffs can be auto-detected or created manually.
 */
@Getter
@Setter
@TableName("inventory_diff")
public class InventoryDiff extends BaseEntity {

    /**
     * ID of the parent inventory task.
     */
    @NotNull(message = "盘点任务ID不能为空")
    private Long taskId;

    /**
     * ID of the inventory record that triggered this diff.
     */
    private Long recordId;

    /**
     * ID of the asset with the discrepancy.
     */
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    /**
     * Diff type: SURPLUS, SHORTAGE, WRONG_LOCATION, STATUS_MISMATCH.
     */
    @NotBlank(message = "差异类型不能为空")
    private String diffType;

    /**
     * The expected value from the book.
     */
    private String bookValue;

    /**
     * The actual value observed during inventory.
     */
    private String actualValue;

    /**
     * Description of the discrepancy.
     */
    private String description;

    /**
     * Diff status: OPEN, APPROVED, ADJUSTED.
     */
    private String status = "OPEN";

    /**
     * ID of the approval instance (if approval workflow is triggered).
     */
    private Long approvalInstanceId;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
