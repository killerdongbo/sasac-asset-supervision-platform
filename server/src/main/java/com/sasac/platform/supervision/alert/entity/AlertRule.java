package com.sasac.platform.supervision.alert.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Alert rule entity defining conditions that trigger alerts.
 * <p>
 * Maps to the {@code alert_rule} table. Each rule specifies an alert type,
 * a human-readable name, a JSON configuration (e.g. {"daysBefore":7}),
 * and whether the rule is currently enabled.
 */
@Getter
@Setter
@TableName("alert_rule")
public class AlertRule extends BaseEntity {

    /**
     * Alert type: MAINTENANCE_EXPIRY, INSPECTION_OVERDUE, BORROW_OVERDUE, IDLE_ASSET.
     */
    private String alertType;

    /**
     * Human-readable rule name.
     */
    private String ruleName;

    /**
     * JSON configuration for the rule (e.g. {"daysBefore": 7}).
     */
    private String ruleConfig;

    /**
     * Whether this rule is currently active.
     */
    private Boolean enabled = true;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
