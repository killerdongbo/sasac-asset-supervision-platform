package com.sasac.platform.permission.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /** 权限编码，如 asset:delete, user:manage */
    String value() default "";

    /** 角色编码，如 SYSTEM_ADMIN, TENANT_ADMIN */
    String[] roles() default {};
}
