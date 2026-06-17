package com.sasac.platform.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Training entity for employee training course records.
 * <p>
 * Maps to the {@code hr_training} table. Tracks course information,
 * trainer, attendees, and evaluation results.
 */
@Getter
@Setter
@TableName("hr_training")
public class HrTraining extends BaseEntity {

    private Long tenantId;

    private String courseName;

    private String trainer;

    private LocalDate trainingDate;

    private BigDecimal durationHours;

    private String attendees;

    private String evaluationSummary;

    private String attachmentIds;
}
