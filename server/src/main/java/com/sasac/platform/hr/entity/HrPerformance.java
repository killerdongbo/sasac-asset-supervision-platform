package com.sasac.platform.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Performance entity for employee performance review records.
 * <p>
 * Maps to the {@code hr_performance} table. Supports self-scoring,
 * manager scoring, and automated grade calculation.
 */
@Getter
@Setter
@TableName("hr_performance")
public class HrPerformance extends BaseEntity {

    private Long tenantId;

    private Long employeeId;

    private String cycleType;

    private Integer cycleYear;

    private String kpiItems;

    private BigDecimal selfScore;

    private BigDecimal managerScore;

    private BigDecimal finalScore;

    private String grade;

    private String reviewComment;

    private String status;
}
