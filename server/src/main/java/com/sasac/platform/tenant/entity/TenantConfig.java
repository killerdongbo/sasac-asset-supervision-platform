package com.sasac.platform.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_tenant_config")
public class TenantConfig extends BaseEntity {

    private Long tenantId;
    private String configKey;
    private String configValue;
    private String description;
}
