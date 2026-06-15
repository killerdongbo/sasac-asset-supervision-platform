package com.sasac.platform.notification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_notification_template")
public class NotificationTemplate extends BaseEntity {
    private String templateCode;
    private String templateName;
    private String titleTpl;
    private String contentTpl;
    private String channels = "IN_APP";
    private Integer status = 1;
}
