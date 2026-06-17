package com.sasac.platform.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Financial indicator entity.
 */
@Getter
@Setter
@TableName("fin_indicator")
public class FinIndicator extends BaseEntity {

    private Long tenantId;
    private Long orgId;
    private String indicatorCode;
    private String indicatorName;
    private BigDecimal indicatorValue;
    private BigDecimal thresholdWarn;
    private BigDecimal thresholdAlarm;
    private Integer periodYear;
    private Integer periodMonth;
    private String status;
}
