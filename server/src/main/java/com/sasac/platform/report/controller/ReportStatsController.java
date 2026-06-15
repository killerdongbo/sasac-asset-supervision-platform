package com.sasac.platform.report.controller;

import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.report.dto.OrgAssetSummary;
import com.sasac.platform.report.dto.ReportQueryParam;
import com.sasac.platform.report.service.ReportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/report-stats")
@RequiredArgsConstructor
public class ReportStatsController {

    private final ReportQueryService reportQueryService;

    @GetMapping("/asset-summary")
    public ApiResponse<List<OrgAssetSummary>> assetSummary(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        ReportQueryParam param = buildParam(tenantId, orgId, category, null, startDate, endDate);
        return ApiResponse.success(reportQueryService.summarizeByOrg(param));
    }

    @GetMapping("/category-stats")
    public ApiResponse<List<Map<String, Object>>> categoryStats(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        ReportQueryParam param = buildParam(tenantId, orgId, null, null, startDate, endDate);
        var grouped = reportQueryService.groupByCategory(param);

        List<Map<String, Object>> result = grouped.entrySet().stream().map(e -> {
            var assets = e.getValue();
            return Map.<String, Object>of(
                    "category", e.getKey(),
                    "count", assets.size(),
                    "originalValue", sumOriginal(assets),
                    "currentValue", sumCurrent(assets),
                    "depreciation", sumDepreciation(assets)
            );
        }).toList();
        return ApiResponse.success(result);
    }

    @GetMapping("/disposal-stats")
    public ApiResponse<List<Map<String, Object>>> disposalStats(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        ReportQueryParam param = buildParam(tenantId, orgId, null, "DISPOSED", startDate, endDate);
        var assets = reportQueryService.queryAssets(param);

        var bySource = assets.stream()
                .collect(Collectors.groupingBy(a -> a.getSourceType() != null ? a.getSourceType() : "OTHER"));

        List<Map<String, Object>> result = bySource.entrySet().stream().map(e -> Map.<String, Object>of(
                "disposalType", e.getKey(),
                "count", e.getValue().size(),
                "originalValue", sumOriginal(e.getValue())
        )).toList();
        return ApiResponse.success(result);
    }

    @GetMapping("/depreciation-analysis")
    public ApiResponse<List<OrgAssetSummary>> depreciationAnalysis(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String category) {
        ReportQueryParam param = buildParam(tenantId, orgId, category, null, null, null);
        return ApiResponse.success(reportQueryService.summarizeByOrg(param));
    }

    @GetMapping("/idle-stats")
    public ApiResponse<List<OrgAssetSummary>> idleStats(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam(required = false) Long orgId) {
        ReportQueryParam param = buildParam(tenantId, orgId, null, "IDLE", null, null);
        return ApiResponse.success(reportQueryService.summarizeByOrg(param));
    }

    @GetMapping("/rental-stats")
    public ApiResponse<List<OrgAssetSummary>> rentalStats(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam(required = false) Long orgId) {
        ReportQueryParam param = buildParam(tenantId, orgId, null, "RENTED", null, null);
        return ApiResponse.success(reportQueryService.summarizeByOrg(param));
    }

    private ReportQueryParam buildParam(Long tenantId, Long orgId, String category,
                                        String useStatus, String startDate, String endDate) {
        ReportQueryParam param = new ReportQueryParam();
        param.setTenantId(tenantId);
        param.setOrgId(orgId);
        param.setCategory(category);
        param.setUseStatus(useStatus);
        param.setStartDate(startDate);
        param.setEndDate(endDate);
        return param;
    }

    private BigDecimal sumOriginal(List<Asset> assets) {
        return assets.stream()
                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumCurrent(List<Asset> assets) {
        return assets.stream()
                .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumDepreciation(List<Asset> assets) {
        return assets.stream()
                .map(a -> a.getAccumulatedDepreciation() != null ? a.getAccumulatedDepreciation() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
