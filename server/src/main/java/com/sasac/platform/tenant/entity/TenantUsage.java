package com.sasac.platform.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("sys_tenant_usage")
public class TenantUsage extends BaseEntity {

    private Long tenantId;
    private Integer userCount = 0;
    private Integer assetCount = 0;
    private Long storageUsedMb = 0L;
    private LocalDateTime lastLoginTime;
}
