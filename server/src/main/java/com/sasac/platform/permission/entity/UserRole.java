package com.sasac.platform.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_user_role")
public class UserRole extends BaseEntity {

    private Long userId;
    private Long roleId;
}
