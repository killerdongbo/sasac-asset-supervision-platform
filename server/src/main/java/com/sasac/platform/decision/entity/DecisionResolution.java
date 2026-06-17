package com.sasac.platform.decision.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 决策决议 - 记录会议形成的正式决议内容。
 * <p>
 * 映射到 {@code decision_resolution} 表，包含决议编号、
 * 内容、投票结果及状态。
 */
@Getter
@Setter
@TableName("decision_resolution")
public class DecisionResolution extends BaseEntity {

    private Long tenantId;

    private Long meetingId;

    private Long itemId;

    private String resolutionNo;

    private String title;

    private String content;

    private String voteResult;

    private String status;
}
