package com.sasac.platform.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Performance salary budget entity.
 */
@Getter
@Setter
@TableName("perf_salary_budget")
public class PerfSalaryBudget extends BaseEntity {

    private Long tenantId;
    private Long orgId;
    private Integer budgetYear;
    private BigDecimal totalBudget;
    private BigDecimal approvedBudget;
    private BigDecimal actualPaid;
    private String adjustmentReason;
    private String status;
}
