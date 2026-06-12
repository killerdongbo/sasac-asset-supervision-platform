package com.sasac.platform.asset.maintenance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Maintenance record entity.
 * <p>
 * Maps to the {@code maintenance_record} table and captures the execution
 * details of a maintenance request, including the result and cost.
 */
@Getter
@Setter
@TableName("maintenance_record")
public class MaintenanceRecord extends BaseEntity {

    /**
     * ID of the parent maintenance request.
     */
    @NotNull(message = "维修申请ID不能为空")
    private Long requestId;

    /**
     * Description of the maintenance process performed.
     */
    private String processDescription;

    /**
     * Result type: FIXED, CANNOT_FIX, NEED_REPLACE.
     */
    @NotBlank(message = "维修结果不能为空")
    private String result;

    /**
     * Total cost of the maintenance.
     */
    private BigDecimal cost;

    /**
     * Timestamp when the maintenance was completed.
     */
    private LocalDateTime completionTime;

    /**
     * Name of the service provider who performed the maintenance.
     */
    private String serviceProvider;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
