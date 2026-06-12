package com.sasac.platform.common.tenant;

/**
 * ThreadLocal-based tenant context for SaaS multi-tenant data isolation.
 * Tenant ID is set per-request via interceptor and cleared after completion.
 */
public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenant(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    private TenantContext() {
        // prevent instantiation
    }
}
