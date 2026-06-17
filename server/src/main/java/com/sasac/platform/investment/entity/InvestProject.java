package com.sasac.platform.investment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Investment project entity tracking the full lifecycle of an investment.
 * <p>
 * Maps to the {@code invest_project} table.
 */
@Getter
@Setter
@TableName("invest_project")
public class InvestProject extends BaseEntity {

    private Long tenantId;

    private Long orgId;

    private Long planId;

    private String projectNo;

    private String projectName;

    private String investType;

    private String industry;

    private String region;

    private BigDecimal investAmount;

    private BigDecimal equityPct;

    private String targetCompany;

    private String targetDescription;

    private Long decisionItemId;

    private Long approvalInstanceId;

    private String phase;

    private BigDecimal expectedRoi;

    private BigDecimal actualRoi;

    private String status;
}
