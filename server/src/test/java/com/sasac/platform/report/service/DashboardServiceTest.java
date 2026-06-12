package com.sasac.platform.report.service;

import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for DashboardService.
 * <p>
 * Tests overview aggregation and top-orgs-by-value queries
 * against a real database with test data.
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AssetMapper assetMapper;

    @Test
    void shouldReturnOverviewWithAggregatedData() {
        // ---- given ----
        // Create 3 test assets with different categories and orgIds
        Asset land = new Asset();
        land.setName("土地资产");
        land.setAssetCode("LAND-001");
        land.setCategory("LAND");
        land.setOrgId(100L);
        land.setTenantId(1L);
        land.setOriginalValue(new BigDecimal("1000000.00"));
        land.setCurrentValue(new BigDecimal("900000.00"));
        land.setAccumulatedDepreciation(new BigDecimal("100000.00"));
        land.setUseStatus("IN_USE");
        land.setCustodian("张三");
        assetMapper.insert(land);

        Asset equipment = new Asset();
        equipment.setName("设备资产");
        equipment.setAssetCode("EQP-001");
        equipment.setCategory("EQUIPMENT");
        equipment.setOrgId(100L);
        equipment.setTenantId(1L);
        equipment.setOriginalValue(new BigDecimal("500000.00"));
        equipment.setCurrentValue(new BigDecimal("350000.00"));
        equipment.setAccumulatedDepreciation(new BigDecimal("150000.00"));
        equipment.setUseStatus("IN_USE");
        equipment.setCustodian("李四");
        assetMapper.insert(equipment);

        Asset vehicle = new Asset();
        vehicle.setName("车辆资产");
        vehicle.setAssetCode("VEH-001");
        vehicle.setCategory("VEHICLE");
        vehicle.setOrgId(200L);
        vehicle.setTenantId(1L);
        vehicle.setOriginalValue(new BigDecimal("300000.00"));
        vehicle.setCurrentValue(new BigDecimal("200000.00"));
        vehicle.setAccumulatedDepreciation(new BigDecimal("100000.00"));
        vehicle.setUseStatus("IDLE");
        vehicle.setCustodian("王五");
        assetMapper.insert(vehicle);

        // ---- when ----
        Map<String, Object> overview = dashboardService.getOverview(1L);

        // ---- then ----
        assertThat(overview.get("totalAssets")).isEqualTo(3);

        // Financial aggregates
        BigDecimal expectedTotalOriginalValue = new BigDecimal("1800000.00");
        assertThat(((BigDecimal) overview.get("totalOriginalValue")))
                .isEqualByComparingTo(expectedTotalOriginalValue);

        BigDecimal expectedTotalCurrentValue = new BigDecimal("1450000.00");
        assertThat(((BigDecimal) overview.get("totalCurrentValue")))
                .isEqualByComparingTo(expectedTotalCurrentValue);

        BigDecimal expectedTotalDepreciation = new BigDecimal("350000.00");
        assertThat(((BigDecimal) overview.get("totalAccumulatedDepreciation")))
                .isEqualByComparingTo(expectedTotalDepreciation);

        // avgDepreciationRate = (350000 / 1800000) * 100 = 19.44 (rounded)
        BigDecimal expectedRate = BigDecimal.valueOf(350000)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(1800000), 2, RoundingMode.HALF_UP);
        assertThat(((BigDecimal) overview.get("avgDepreciationRate")))
                .isEqualByComparingTo(expectedRate);

        // Category distribution: grouped by category, sum originalValue
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> categoryDistribution = (Map<String, BigDecimal>) overview.get("categoryDistribution");
        assertThat(categoryDistribution).hasSize(3);
        assertThat(categoryDistribution).containsKeys("LAND", "EQUIPMENT", "VEHICLE");
        assertThat(categoryDistribution.get("LAND")).isEqualByComparingTo(new BigDecimal("1000000.00"));
        assertThat(categoryDistribution.get("EQUIPMENT")).isEqualByComparingTo(new BigDecimal("500000.00"));
        assertThat(categoryDistribution.get("VEHICLE")).isEqualByComparingTo(new BigDecimal("300000.00"));

        // Status distribution: grouped by useStatus, count
        @SuppressWarnings("unchecked")
        Map<String, Long> statusDistribution = (Map<String, Long>) overview.get("statusDistribution");
        assertThat(statusDistribution.get("IN_USE")).isEqualTo(2L);
        assertThat(statusDistribution.get("IDLE")).isEqualTo(1L);
    }

    @Test
    void shouldReturnTopOrgsByValueInDescendingOrder() {
        // ---- given ----
        // Create assets across orgs 100 and 200
        Asset land = new Asset();
        land.setName("土地资产");
        land.setAssetCode("LAND-002");
        land.setCategory("LAND");
        land.setOrgId(100L);
        land.setTenantId(1L);
        land.setOriginalValue(new BigDecimal("1000000.00"));
        land.setUseStatus("IN_USE");
        land.setCustodian("张三");
        assetMapper.insert(land);

        Asset equipment = new Asset();
        equipment.setName("设备资产");
        equipment.setAssetCode("EQP-002");
        equipment.setCategory("EQUIPMENT");
        equipment.setOrgId(100L);
        equipment.setTenantId(1L);
        equipment.setOriginalValue(new BigDecimal("500000.00"));
        equipment.setUseStatus("IN_USE");
        equipment.setCustodian("李四");
        assetMapper.insert(equipment);

        Asset vehicle = new Asset();
        vehicle.setName("车辆资产");
        vehicle.setAssetCode("VEH-002");
        vehicle.setCategory("VEHICLE");
        vehicle.setOrgId(200L);
        vehicle.setTenantId(1L);
        vehicle.setOriginalValue(new BigDecimal("300000.00"));
        vehicle.setUseStatus("IDLE");
        vehicle.setCustodian("王五");
        assetMapper.insert(vehicle);

        // ---- when ----
        List<Map<String, Object>> topOrgs = dashboardService.getTopOrgsByValue(1L, 5);

        // ---- then ----
        // org 100 total = 1,500,000; org 200 total = 300,000
        assertThat(topOrgs).hasSize(2);

        // First entry should be org 100 (higher total value)
        assertThat(topOrgs.get(0).get("orgId")).isEqualTo(100L);
        assertThat(((Number) topOrgs.get(0).get("totalValue")).doubleValue())
                .isGreaterThanOrEqualTo(((Number) topOrgs.get(1).get("totalValue")).doubleValue());
    }

    @Test
    void shouldHandleEmptyTenant() {
        // ---- given ----
        // Tenant 999L has no assets

        // ---- when ----
        Map<String, Object> overview = dashboardService.getOverview(999L);

        // ---- then ----
        // Should return zero totals without crashing
        assertThat(overview.get("totalAssets")).isEqualTo(0);
        assertThat(((BigDecimal) overview.get("totalOriginalValue")))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(((BigDecimal) overview.get("totalCurrentValue")))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(((BigDecimal) overview.get("totalAccumulatedDepreciation")))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(((BigDecimal) overview.get("avgDepreciationRate")))
                .isEqualByComparingTo(BigDecimal.ZERO);

        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> categoryDistribution = (Map<String, BigDecimal>) overview.get("categoryDistribution");
        assertThat(categoryDistribution).isEmpty();

        @SuppressWarnings("unchecked")
        Map<String, Long> statusDistribution = (Map<String, Long>) overview.get("statusDistribution");
        assertThat(statusDistribution).isEmpty();
    }
}
