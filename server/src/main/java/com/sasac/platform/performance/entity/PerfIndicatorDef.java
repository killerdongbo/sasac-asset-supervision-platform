package com.sasac.platform.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Performance indicator definition entity.
 */
@Getter
@Setter
@TableName("perf_indicator_def")
public class PerfIndicatorDef extends BaseEntity {

    private Long tenantId;
    private Long orgId;
    private String indicatorCode;
    private String indicatorName;
    private String category;
    private BigDecimal weight;
    private BigDecimal targetValue;
    private BigDecimal actualValue;
    private BigDecimal score;
    private Integer cycleYear;
}
