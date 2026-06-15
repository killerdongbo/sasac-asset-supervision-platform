package com.sasac.platform.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.organization.entity.Organization;
import com.sasac.platform.organization.mapper.OrganizationMapper;
import com.sasac.platform.report.entity.Report;
import com.sasac.platform.report.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for report generation and retrieval.
 * <p>
 * Aggregates asset data into JSON snapshot reports, supporting
 * the reporting lifecycle from DRAFT through REVIEWED/ACCEPTED.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;
    private final AssetMapper assetMapper;
    private final OrganizationMapper organizationMapper;
    private final ObjectMapper objectMapper;

    /**
     * Generates a new report by aggregating asset data into a JSON snapshot.
     *
     * @param reportType the type of report (e.g., ASSET_SUMMARY)
     * @param orgId      the organization ID to report on
     * @param period     the reporting period (e.g., 2025-Q1)
     * @param tenantId   the tenant ID
     * @return the created Report entity with DRAFT status
     */
    @Transactional
    public Report generate(String reportType, Long orgId, String period, Long tenantId) {
        Map<String, Object> data = buildReportData(reportType, orgId);
        try {
            String snapshotJson = objectMapper.writeValueAsString(data);

            Report report = new Report();
            report.setReportType(reportType);
            report.setPeriod(period);
            report.setOrgId(orgId);
            report.setTenantId(tenantId);
            report.setSubmitStatus("DRAFT");
            report.setVersion(1);
            report.setSnapshotData(snapshotJson);

            reportMapper.insert(report);
            return report;
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize report data", e);
        }
    }

    /**
     * Retrieves and parses the report data from its JSON snapshot.
     *
     * @param reportId the report ID
     * @return the parsed report data as a Map
     */
    public Map<String, Object> getReportData(Long reportId) {
        Report report = reportMapper.selectById(reportId);
        if (report == null || report.getSnapshotData() == null) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(report.getSnapshotData(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse report data", e);
        }
    }

    /**
     * Retrieves a report entity by ID.
     *
     * @param id the report ID
     * @return the Report entity
     * @throws BusinessException if the report is not found
     */
    public Report getById(Long id) {
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        return report;
    }

    /**
     * Lists reports for a given organization, ordered by creation time descending.
     *
     * @param orgId the organization ID
     * @return list of reports
     */
    public List<Report> listByOrg(Long orgId) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getOrgId, orgId);
        wrapper.orderByDesc(Report::getCreatedAt)
                .orderByDesc(Report::getId);
        return reportMapper.selectList(wrapper);
    }

    /**
     * Collects all descendant organization IDs recursively.
     */
    private List<Long> collectSubOrgIds(Long parentId) {
        List<Organization> children = organizationMapper.selectList(
                new LambdaQueryWrapper<Organization>()
                        .eq(Organization::getParentId, parentId)
                        .eq(Organization::getStatus, 1)
        );
        List<Long> ids = new ArrayList<>();
        for (Organization child : children) {
            ids.add(child.getId());
            ids.addAll(collectSubOrgIds(child.getId()));
        }
        return ids;
    }

    /**
     * Builds the aggregated report data from asset records,
     * including assets from sub-organizations recursively.
     */
    private Map<String, Object> buildReportData(String reportType, Long orgId) {
        // Collect all org IDs including sub-organizations
        List<Long> allOrgIds = new ArrayList<>(collectSubOrgIds(orgId));
        allOrgIds.add(orgId);

        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Asset::getOrgId, allOrgIds);
        List<Asset> assets = assetMapper.selectList(wrapper);

        // Total counts
        int totalAssets = assets.size();
        BigDecimal totalOriginalValue = assets.stream()
                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCurrentValue = assets.stream()
                .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDepreciation = assets.stream()
                .map(a -> a.getAccumulatedDepreciation() != null ? a.getAccumulatedDepreciation() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Average depreciation rate
        BigDecimal avgDepreciationRate = totalOriginalValue.compareTo(BigDecimal.ZERO) > 0
                ? totalDepreciation.divide(totalOriginalValue, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Distinct org count
        long orgCount = assets.stream().map(Asset::getOrgId).distinct().count();

        // Category distribution with financials
        Map<String, List<Asset>> byCategory = assets.stream()
                .collect(Collectors.groupingBy(a -> a.getCategory() != null ? a.getCategory() : "未知"));
        List<Map<String, Object>> categoryDistribution = byCategory.entrySet().stream()
                .map(entry -> {
                    BigDecimal catOrig = entry.getValue().stream()
                            .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal catCurr = entry.getValue().stream()
                            .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", entry.getKey());
                    item.put("value", entry.getValue().size());
                    item.put("totalOriginalValue", catOrig);
                    item.put("totalCurrentValue", catCurr);
                    return item;
                })
                .collect(Collectors.toList());

        // Build the summaryData wrapper that the frontend expects
        Map<String, Object> summaryData = new LinkedHashMap<>();
        summaryData.put("totalAssets", totalAssets);
        summaryData.put("totalOriginalValue", totalOriginalValue);
        summaryData.put("totalCurrentValue", totalCurrentValue);
        summaryData.put("totalDepreciation", totalDepreciation);
        summaryData.put("avgDepreciationRate", avgDepreciationRate);
        summaryData.put("orgCount", orgCount);
        summaryData.put("categoryDistribution", categoryDistribution);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("reportType", reportType);
        data.put("orgId", orgId.toString());
        data.put("generatedAt", LocalDateTime.now().toString());
        data.put("summaryData", summaryData);

        return data;
    }
}
