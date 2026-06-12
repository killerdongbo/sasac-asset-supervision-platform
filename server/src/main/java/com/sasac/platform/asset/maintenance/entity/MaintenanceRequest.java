package com.sasac.platform.asset.maintenance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Maintenance request entity.
 * <p>
 * Maps to the {@code maintenance_request} table and tracks requests for
 * asset maintenance, which can be created manually or automatically
 * transferred from an inspection anomaly.
 */
@Getter
@Setter
@TableName("maintenance_request")
public class MaintenanceRequest extends BaseEntity {

    /**
     * ID of the asset requiring maintenance.
     */
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    /**
     * ID of the preferred maintenance provider.
     */
    private Long providerId;

    /**
     * Description of the fault or issue.
     */
    private String faultDescription;

    /**
     * Priority level: LOW, MEDIUM, HIGH, URGENT.
     */
    @NotBlank(message = "优先级不能为空")
    private String priority = "MEDIUM";

    /**
     * Source type: MANUAL (user-created) or FROM_INSPECTION (auto-transferred).
     */
    @NotBlank(message = "来源类型不能为空")
    private String sourceType = "MANUAL";

    /**
     * ID of the inspection anomaly that triggered this request (if any).
     */
    private Long sourceAnomalyId;

    /**
     * Request status: PENDING, PROCESSING, COMPLETED.
     */
    private String status = "PENDING";

    /**
     * Expected completion date.
     */
    private LocalDate expectedDate;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
