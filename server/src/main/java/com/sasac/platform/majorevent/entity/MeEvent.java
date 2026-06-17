package com.sasac.platform.majorevent.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Major event entity representing a significant business incident.
 * <p>
 * Maps to the {@code me_event} table. Each event documents the type,
 * impact, handling plan, and approval status of a major incident
 * that requires management attention.
 */
@Getter
@Setter
@TableName("me_event")
public class MeEvent extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Organization ID responsible for this event.
     */
    private Long orgId;

    /**
     * Auto-generated event number (e.g. ZDSX-20240617120000-1234).
     */
    private String eventNo;

    /**
     * Event type: ACCIDENT, LAWSUIT, GUARANTEE_RISK, ENVIRONMENTAL, PRODUCTION_SAFETY, OTHER.
     */
    private String eventType;

    /**
     * Human-readable event title.
     */
    private String title;

    /**
     * Detailed description of the event.
     */
    private String description;

    /**
     * Assessment of potential impact on the organization.
     */
    private String impactAssessment;

    /**
     * Plan for handling or mitigating the event.
     */
    private String handlingPlan;

    /**
     * Reference to the associated approval workflow instance.
     */
    private Long approvalInstanceId;

    /**
     * Status: REPORTED, UNDER_REVIEW, APPROVED, RESOLVED, CLOSED.
     */
    private String status = "REPORTED";

    /**
     * Timestamp when the event was first reported.
     */
    private LocalDateTime reportedAt;

    /**
     * Timestamp when the event was resolved.
     */
    private LocalDateTime resolvedAt;
}
