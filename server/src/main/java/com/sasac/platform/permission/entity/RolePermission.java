package com.sasac.platform.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_role_permission")
public class RolePermission extends BaseEntity {

    private Long roleId;
    private Long permId;
}
