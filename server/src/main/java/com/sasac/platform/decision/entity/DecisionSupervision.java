package com.sasac.platform.decision.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 决策督办 - 记录决议事项的督办任务及进展情况。
 * <p>
 * 映射到 {@code decision_supervision} 表，跟踪决议执行
 * 的负责人、截止日期及完成状态。
 */
@Getter
@Setter
@TableName("decision_supervision")
public class DecisionSupervision extends BaseEntity {

    private Long tenantId;

    private Long resolutionId;

    private String taskTitle;

    private String description;

    private Long assigneeId;

    private String assigneeName;

    private LocalDate deadline;

    private LocalDateTime completedAt;

    private String progressNote;

    private String status;
}
