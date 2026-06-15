package com.sasac.platform.notification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("sys_notification")
public class Notification extends BaseEntity {
    private Long tenantId;
    private Long userId;
    private String title;
    private String content;
    private String type = "SYSTEM";
    private String level = "INFO";
    private String bizType;
    private Long bizId;
    private Integer isRead = 0;
    private LocalDateTime readAt;
}
