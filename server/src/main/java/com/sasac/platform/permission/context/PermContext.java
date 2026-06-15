package com.sasac.platform.permission.context;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 当前用户权限上下文（ThreadLocal）.
 * 登录时由认证模块写入，请求结束后清除。
 */
public final class PermContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> ORG_ID = new ThreadLocal<>();
    private static final ThreadLocal<Set<String>> PERMISSIONS = ThreadLocal.withInitial(ConcurrentHashMap::newKeySet);
    private static final ThreadLocal<Set<String>> ROLES = ThreadLocal.withInitial(ConcurrentHashMap::newKeySet);

    private PermContext() {}

    public static void setUserId(Long uid) { USER_ID.set(uid); }
    public static Long getUserId() { return USER_ID.get(); }

    public static void setTenantId(Long tid) { TENANT_ID.set(tid); }
    public static Long getTenantId() { return TENANT_ID.get(); }

    public static void setOrgId(Long oid) { ORG_ID.set(oid); }
    public static Long getOrgId() { return ORG_ID.get(); }

    public static void setPermissions(Set<String> perms) {
        PERMISSIONS.get().clear();
        if (perms != null) PERMISSIONS.get().addAll(perms);
    }

    public static boolean hasPermission(String perm) {
        return PERMISSIONS.get().contains(perm);
    }

    public static boolean hasRole(String role) {
        return ROLES.get().contains(role);
    }

    public static void setRoles(Set<String> roleCodes) {
        ROLES.get().clear();
        if (roleCodes != null) ROLES.get().addAll(roleCodes);
    }

    public static void clear() {
        USER_ID.remove();
        TENANT_ID.remove();
        ORG_ID.remove();
        PERMISSIONS.remove();
        ROLES.remove();
    }
}
