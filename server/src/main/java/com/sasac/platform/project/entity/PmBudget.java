package com.sasac.platform.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Budget entity tracking planned vs actual amounts per year for a project.
 */
@Getter
@Setter
@TableName("pm_budget")
public class PmBudget extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private Integer budgetYear;

    private String category;

    private BigDecimal plannedAmount;

    private BigDecimal actualAmount;
}
