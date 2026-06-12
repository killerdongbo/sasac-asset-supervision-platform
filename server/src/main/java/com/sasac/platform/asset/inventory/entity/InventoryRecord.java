package com.sasac.platform.asset.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Inventory record entity.
 * <p>
 * Maps to the {@code inventory_record} table and captures the
 * physical inspection result for a single asset during an inventory task.
 * Book values are populated automatically from the asset table.
 */
@Getter
@Setter
@TableName("inventory_record")
public class InventoryRecord extends BaseEntity {

    /**
     * ID of the parent inventory task.
     */
    @NotNull(message = "盘点任务ID不能为空")
    private Long taskId;

    /**
     * ID of the asset being inventoried.
     */
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    /**
     * Asset name from the book (filled automatically from asset table).
     */
    private String bookName;

    /**
     * Asset location from the book (filled automatically from asset table).
     */
    private String bookLocation;

    /**
     * Asset status from the book (filled automatically from asset table).
     */
    private String bookStatus;

    /**
     * Whether the asset physically exists.
     */
    @TableField("is_exists")
    private Boolean exists;

    /**
     * Actual location observed during inventory.
     */
    private String actualLocation;

    /**
     * Actual status observed during inventory.
     */
    private String actualStatus;

    /**
     * Additional remarks.
     */
    private String remark;

    /**
     * User ID of the person performing the inventory.
     */
    @NotNull(message = "盘点人不能为空")
    private Long operatorId;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
