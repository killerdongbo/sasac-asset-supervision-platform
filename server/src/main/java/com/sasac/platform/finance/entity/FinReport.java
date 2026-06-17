package com.sasac.platform.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Financial report entity.
 */
@Getter
@Setter
@TableName("fin_report")
public class FinReport extends BaseEntity {

    private Long tenantId;
    private Long orgId;
    private String reportType;
    private Integer reportYear;
    private Integer reportPeriod;
    private String reportData;
    private String status;
    private LocalDateTime submittedAt;
}
