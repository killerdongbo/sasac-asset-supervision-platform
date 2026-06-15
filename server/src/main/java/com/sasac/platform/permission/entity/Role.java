package com.sasac.platform.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_role")
public class Role extends BaseEntity {

    private Long tenantId;
    private String roleCode;
    private String roleName;
    private String roleType;
    private String dataScope;
    private String description;
    private Integer status = 1;
}
