package com.sasac.platform.property.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资产评估 - 资产评估备案信息
 */
@Getter
@Setter
@TableName("pr_assessment")
public class PrAssessment extends BaseEntity {

    private Long tenantId;
    private Long registrationId;
    private String assessNo;
    private String assessPurpose;
    private String assessAgency;
    private String assessMethod;
    private BigDecimal bookValue;
    private BigDecimal assessedValue;
    private BigDecimal deviationPct;
    private String assessReportIds;
    private String approvalStatus;
    private LocalDate assessDate;
}
