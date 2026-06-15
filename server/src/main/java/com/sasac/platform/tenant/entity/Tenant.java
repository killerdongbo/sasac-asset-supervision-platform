package com.sasac.platform.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("sys_tenant")
public class Tenant extends BaseEntity {

    private String tenantCode;
    private String tenantName;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private Integer status = 1;
    private LocalDateTime expireTime;
    private Integer maxUsers = 50;
    private Integer maxAssets = 10000;
    private String edition = "STANDARD";
    private String logoUrl;
    private String domain;
    private String remark;
}
