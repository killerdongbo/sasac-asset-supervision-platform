package com.sasac.platform.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Budget execution entity.
 */
@Getter
@Setter
@TableName("fin_budget")
public class FinBudget extends BaseEntity {

    private Long tenantId;
    private Long orgId;
    private Integer budgetYear;
    private String budgetType;
    private BigDecimal plannedAmount;
    private BigDecimal actualAmount;
    private BigDecimal executedPct;
}
