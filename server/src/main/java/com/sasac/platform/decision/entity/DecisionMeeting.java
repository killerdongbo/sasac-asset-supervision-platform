package com.sasac.platform.decision.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 决策会议 - 记录三重一大决策会议的详细安排。
 * <p>
 * 映射到 {@code decision_meeting} 表，包含会议时间、
 * 地点、议程、参会人员及会议纪要。
 */
@Getter
@Setter
@TableName("decision_meeting")
public class DecisionMeeting extends BaseEntity {

    private Long tenantId;

    private Long orgId;

    private String meetingNo;

    private String meetingType;

    private String title;

    private String agenda;

    private LocalDateTime scheduledAt;

    private String location;

    private String host;

    private String attendees;

    private String minutes;

    private String status;
}
