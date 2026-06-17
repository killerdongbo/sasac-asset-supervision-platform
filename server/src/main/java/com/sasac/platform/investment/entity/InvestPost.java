package com.sasac.platform.investment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Post-investment monitoring record.
 * <p>
 * Maps to the {@code invest_post} table. Does not have an updated_at column
 * in the database — these are append-only monitoring snapshots.
 */
@Getter
@Setter
@TableName("invest_post")
public class InvestPost extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private LocalDate reportDate;

    private BigDecimal revenue;

    private BigDecimal netProfit;

    private BigDecimal netAssets;

    private BigDecimal debtRatio;

    private String majorEvents;

    private String riskLevel;
}
