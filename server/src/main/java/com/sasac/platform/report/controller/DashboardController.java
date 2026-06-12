package com.sasac.platform.report.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.report.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for the SASAC supervision dashboard.
 * <p>
 * Provides tenant-wide overview statistics and top-orgs-by-value
 * endpoints used by the frontend dashboard view.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Retrieves tenant-wide dashboard overview statistics.
     *
     * @param tenantId the tenant ID from the request header
     * @return ApiResponse containing totalAssets, financial aggregates,
     *         avgDepreciationRate, categoryDistribution, and statusDistribution
     */
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview(@RequestHeader("X-Tenant-Id") Long tenantId) {
        return ApiResponse.success(dashboardService.getOverview(tenantId));
    }

    /**
     * Retrieves the top N organizations by total asset value.
     *
     * @param tenantId the tenant ID from the request header
     * @param limit    the maximum number of organizations to return (default 10)
     * @return ApiResponse containing a list of orgId/totalValue entries
     */
    @GetMapping("/top-orgs")
    public ApiResponse<?> topOrgs(@RequestHeader("X-Tenant-Id") Long tenantId,
                                   @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(dashboardService.getTopOrgsByValue(tenantId, limit));
    }
}
