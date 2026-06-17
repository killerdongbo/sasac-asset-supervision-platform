package com.sasac.platform.investment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Due diligence record for an investment project.
 * <p>
 * Maps to the {@code invest_dd} table.
 */
@Getter
@Setter
@TableName("invest_dd")
public class InvestDD extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private String ddType;

    private String lawFirm;

    private String accountingFirm;

    private String reportSummary;

    private String riskFindings;

    private String attachmentIds;

    private String status;
}
