package com.sasac.platform.tenant.interceptor;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantContextFilter implements Filter {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    public static Long getCurrentTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String tenantHeader = httpRequest.getHeader("X-Tenant-Id");
            if (tenantHeader != null && !tenantHeader.isBlank()) {
                CURRENT_TENANT.set(Long.parseLong(tenantHeader));
            } else {
                CURRENT_TENANT.set(0L);
            }
            chain.doFilter(request, response);
        } finally {
            CURRENT_TENANT.remove();
        }
    }
}
