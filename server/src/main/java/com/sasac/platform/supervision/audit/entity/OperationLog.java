package com.sasac.platform.supervision.audit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a single audit log entry recording a user operation.
 * <p>
 * Maps to the {@code operation_log} table. Each entry captures who performed
 * what action on which target, along with the before/after state for
 * full traceability.
 */
@Getter
@Setter
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * User ID of the operator.
     */
    private Long operatorId;

    /**
     * Display name of the operator.
     */
    private String operatorName;

    /**
     * Action performed: CREATE, UPDATE, DELETE, APPROVE, SUBMIT.
     */
    private String action;

    /**
     * Type of the target entity (e.g. "Asset", "InspectionTask").
     */
    private String targetType;

    /**
     * ID of the target entity.
     */
    private Long targetId;

    /**
     * JSON snapshot of the state before the operation.
     */
    private String beforeData;

    /**
     * JSON snapshot of the state after the operation.
     */
    private String afterData;

    /**
     * Client IP address.
     */
    private String ip;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Timestamp of when the operation occurred.
     */
    private LocalDateTime createdAt;
}
