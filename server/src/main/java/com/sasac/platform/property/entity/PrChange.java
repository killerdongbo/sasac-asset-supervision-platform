package com.sasac.platform.property.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 产权变动 - 记录产权信息变更前后的数据
 */
@Getter
@Setter
@TableName("pr_change")
public class PrChange extends BaseEntity {

    private Long tenantId;
    private Long registrationId;
    private String changeType;
    private String beforeData;
    private String afterData;
    private String changeReason;
    private String approvalFileIds;
    private LocalDate effectiveDate;
}
