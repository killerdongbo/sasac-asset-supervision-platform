package com.sasac.platform.asset.inspection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Inspection anomaly entity.
 * <p>
 * Maps to the {@code inspection_anomaly} table and captures issues
 * discovered during an inspection that require resolution.
 */
@Getter
@Setter
@TableName("inspection_anomaly")
public class InspectionAnomaly extends BaseEntity {

    /**
     * ID of the parent inspection task.
     */
    private Long taskId;

    /**
     * ID of the inspection record that triggered this anomaly.
     */
    private Long recordId;

    /**
     * ID of the asset that was found to be anomalous.
     */
    private Long assetId;

    /**
     * Type of anomaly: DAMAGED, NOT_FOUND, WRONG_LOCATION, STATUS_ABNORMAL.
     */
    private String anomalyType;

    /**
     * Free-text description of the anomaly.
     */
    private String description;

    /**
     * Resolution applied: RECTIFY, TRANSFER_TO_MAINTENANCE, VERIFY.
     */
    private String resolution;

    /**
     * ID of the maintenance request created for this anomaly (if any).
     */
    private String maintenanceRequestId;

    /**
     * Anomaly status: OPEN, RESOLVED.
     */
    private String status = "OPEN";

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
