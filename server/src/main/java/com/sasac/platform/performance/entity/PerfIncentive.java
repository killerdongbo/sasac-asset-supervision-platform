package com.sasac.platform.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Performance incentive entity.
 */
@Getter
@Setter
@TableName("perf_incentive")
public class PerfIncentive extends BaseEntity {

    private Long tenantId;
    private String incentiveType;
    private Long employeeId;
    private LocalDate grantDate;
    private Integer vestingPeriod;
    private BigDecimal amount;
    private String status;
}
