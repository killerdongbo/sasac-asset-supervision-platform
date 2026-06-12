package com.sasac.platform.common.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that extracts tenant ID from the "X-Tenant-Id" header
 * and sets it in the TenantContext for the duration of the request.
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String X_TENANT_ID_HEADER = "X-Tenant-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantIdStr = request.getHeader(X_TENANT_ID_HEADER);
        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            try {
                Long tenantId = Long.parseLong(tenantIdStr);
                TenantContext.setCurrentTenant(tenantId);
            } catch (NumberFormatException e) {
                // ignore invalid tenant ID header, continue without tenant context
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
