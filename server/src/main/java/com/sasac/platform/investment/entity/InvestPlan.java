package com.sasac.platform.investment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Investment plan entity representing an annual investment plan.
 * <p>
 * Maps to the {@code invest_plan} table.
 */
@Getter
@Setter
@TableName("invest_plan")
public class InvestPlan extends BaseEntity {

    private Long tenantId;

    private Long orgId;

    private Integer planYear;

    private String planName;

    private BigDecimal totalBudget;

    private String approvedBy;

    private String status;
}
