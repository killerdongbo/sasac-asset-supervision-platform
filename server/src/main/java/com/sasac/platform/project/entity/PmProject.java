package com.sasac.platform.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Project entity representing a construction / IT / renovation project.
 * <p>
 * Maps to the {@code pm_project} table and tracks the full lifecycle
 * from DRAFT to COMPLETED.
 */
@Getter
@Setter
@TableName("pm_project")
public class PmProject extends BaseEntity {

    private Long tenantId;

    private Long orgId;

    private String projectNo;

    private String name;

    private String projectType;

    private String category;

    private BigDecimal budgetTotal;

    private BigDecimal budgetApproved;

    private LocalDate startDate;

    private LocalDate plannedEndDate;

    private LocalDate actualEndDate;

    private Long managerId;

    private String managerName;

    private String department;

    private Long decisionItemId;

    private String status;

    private String description;

    private String remark;
}
