package com.sasac.platform.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Acceptance record entity for project completion review and sign-off.
 */
@Getter
@Setter
@TableName("pm_acceptance")
public class PmAcceptance extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private String acceptanceNo;

    private String acceptanceType;

    private String result;

    private BigDecimal score;

    private String reviewOpinion;

    private Long reviewerId;

    private String reviewerName;

    private LocalDate acceptanceDate;
}
