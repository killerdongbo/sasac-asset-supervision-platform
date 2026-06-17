package com.sasac.platform.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Progress record entity for periodic project progress reporting.
 */
@Getter
@Setter
@TableName("pm_progress")
public class PmProgress extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private LocalDate reportDate;

    private Long reporterId;

    private String reporterName;

    private BigDecimal progressPct;

    private String completedWork;

    private String pendingWork;

    private String issues;

    private String nextPlan;
}
