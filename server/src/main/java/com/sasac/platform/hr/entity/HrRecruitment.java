package com.sasac.platform.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Recruitment entity tracking open positions and candidate pipelines.
 * <p>
 * Maps to the {@code hr_recruitment} table. Each record represents a
 * recruitment requirement for a specific position within an organization.
 */
@Getter
@Setter
@TableName("hr_recruitment")
public class HrRecruitment extends BaseEntity {

    private Long tenantId;

    private Long orgId;

    private String positionName;

    private Integer headcount;

    private String pipeline;

    private String candidates;

    private String status;
}
