package com.sasac.platform.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * System user entity.
 * <p>
 * Maps to the {@code sys_user} table and represents a platform user
 * with role-based access control (RBAC) support.
 */
@Getter
@Setter
@TableName("sys_user")
public class User extends BaseEntity {

    private String username;

    private String password;

    private String realName;

    private String phone;

    private Long tenantId;

    private Long orgId;

    /**
     * Role code for RBAC. Defaults to {@code ENTERPRISE_OPERATOR}.
     */
    private String roleCode = "ENTERPRISE_OPERATOR";

    /**
     * Account status: 1 = active, 0 = disabled.
     */
    private Integer status = 1;
}
