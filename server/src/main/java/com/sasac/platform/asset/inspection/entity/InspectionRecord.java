package com.sasac.platform.asset.inspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Inspection record entity.
 * <p>
 * Maps to the {@code inspection_record} table and stores the result of
 * a single asset inspection within a task.
 */
@Getter
@Setter
@TableName("inspection_record")
public class InspectionRecord extends BaseEntity {

    /**
     * ID of the parent inspection task.
     */
    @NotNull(message = "所属任务不能为空")
    private Long taskId;

    /**
     * ID of the asset being inspected.
     */
    @NotNull(message = "资产不能为空")
    private Long assetId;

    /**
     * Whether the asset was found in normal condition.
     */
    @TableField("is_normal")
    private Boolean isNormal;

    /**
     * Physical location where the asset was found.
     */
    private String actualLocation;

    /**
     * Observed usage status at inspection time.
     */
    private String actualStatus;

    /**
     * Type of anomaly if the asset is not normal:
     * DAMAGED, NOT_FOUND, WRONG_LOCATION, STATUS_ABNORMAL.
     */
    private String anomalyType;

    /**
     * Free-text description of the inspection result.
     */
    private String description;

    /**
     * Comma-separated photo IDs (for file service integration).
     */
    private String photoIds;

    /**
     * User ID of the inspector.
     */
    @NotNull(message = "巡检人不能为空")
    private Long inspectorId;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
