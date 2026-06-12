package com.sasac.platform.report.service;

import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.service.AssetService;
import com.sasac.platform.report.entity.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ReportService.
 * <p>
 * Tests report generation, data retrieval, and listing functionality
 * against a real database with test data.
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AssetService assetService;

    @Test
    void shouldGenerateAndRetrieveReport() {
        // ---- given ----
        // Create test assets for org 1
        AssetCreateDTO equipment1 = new AssetCreateDTO();
        equipment1.setName("设备A");
        equipment1.setAssetCode("EQP-001");
        equipment1.setCategory("EQUIPMENT");
        equipment1.setOrgId(1L);
        equipment1.setTenantId(0L);
        equipment1.setSpecification("标准规格");
        equipment1.setUnit("台");
        equipment1.setQuantity(1);
        equipment1.setOriginalValue(new BigDecimal("100000.00"));
        equipment1.setUseStatus("IN_USE");
        equipment1.setCustodian("张三");
        equipment1.setPurchaseDate(LocalDate.of(2024, 1, 15));
        assetService.create(equipment1);

        AssetCreateDTO equipment2 = new AssetCreateDTO();
        equipment2.setName("设备B");
        equipment2.setAssetCode("EQP-002");
        equipment2.setCategory("EQUIPMENT");
        equipment2.setOrgId(1L);
        equipment2.setTenantId(0L);
        equipment2.setSpecification("标准规格");
        equipment2.setUnit("台");
        equipment2.setQuantity(1);
        equipment2.setOriginalValue(new BigDecimal("50000.00"));
        equipment2.setUseStatus("IDLE");
        equipment2.setCustodian("李四");
        equipment2.setPurchaseDate(LocalDate.of(2024, 3, 20));
        assetService.create(equipment2);

        AssetCreateDTO itEquipment = new AssetCreateDTO();
        itEquipment.setName("服务器C");
        itEquipment.setAssetCode("SRV-001");
        itEquipment.setCategory("IT_EQUIPMENT");
        itEquipment.setOrgId(1L);
        itEquipment.setTenantId(0L);
        itEquipment.setSpecification("Dell R740");
        itEquipment.setUnit("台");
        itEquipment.setQuantity(1);
        itEquipment.setOriginalValue(new BigDecimal("200000.00"));
        itEquipment.setUseStatus("IN_USE");
        itEquipment.setCustodian("王五");
        itEquipment.setPurchaseDate(LocalDate.of(2024, 6, 1));
        assetService.create(itEquipment);

        // ---- when ----
        Report report = reportService.generate("ASSET_SUMMARY", 1L, "2025-Q1", 0L);

        // ---- then ----
        // Assert report entity properties
        assertThat(report).isNotNull();
        assertThat(report.getId()).isNotNull();
        assertThat(report.getReportType()).isEqualTo("ASSET_SUMMARY");
        assertThat(report.getPeriod()).isEqualTo("2025-Q1");
        assertThat(report.getOrgId()).isEqualTo(1L);
        assertThat(report.getTenantId()).isEqualTo(0L);
        assertThat(report.getSubmitStatus()).isEqualTo("DRAFT");
        assertThat(report.getVersion()).isEqualTo(1);

        // Assert report data contains aggregated fields
        Map<String, Object> data = reportService.getReportData(report.getId());
        assertThat(data).isNotNull();
        assertThat(data).containsKey("totalAssets");
        assertThat(data).containsKey("totalOriginalValue");
        assertThat(data).containsKey("totalCurrentValue");
        assertThat(data).containsKey("totalAccumulatedDepreciation");
        assertThat(data).containsKey("categorySummary");
        assertThat(data).containsKey("statusSummary");

        // Assert total asset count
        Object totalAssets = data.get("totalAssets");
        int totalVal = totalAssets instanceof Number ? ((Number) totalAssets).intValue() : Integer.parseInt(totalAssets.toString());
        assertThat(totalVal).isEqualTo(3);

        // Assert category summary - EQUIPMENT should have count 2
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> categorySummary = (List<Map<String, Object>>) data.get("categorySummary");
        Map<String, Object> equipmentSummary = categorySummary.stream()
                .filter(item -> "EQUIPMENT".equals(item.get("category")))
                .findFirst()
                .orElse(null);
        assertThat(equipmentSummary).isNotNull();
        Object eqCount = equipmentSummary.get("count");
        int eqCountVal = eqCount instanceof Number ? ((Number) eqCount).intValue() : Integer.parseInt(eqCount.toString());
        assertThat(eqCountVal).isEqualTo(2);

        // Assert status summary - IN_USE should have count 2
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> statusSummary = (List<Map<String, Object>>) data.get("statusSummary");
        Map<String, Object> inUseSummary = statusSummary.stream()
                .filter(item -> "IN_USE".equals(item.get("status")))
                .findFirst()
                .orElse(null);
        assertThat(inUseSummary).isNotNull();
        Object inUseCount = inUseSummary.get("count");
        int inUseVal = inUseCount instanceof Number ? ((Number) inUseCount).intValue() : Integer.parseInt(inUseCount.toString());
        assertThat(inUseVal).isEqualTo(2);
    }

    @Test
    void shouldListReportsByOrg() {
        // ---- given ----
        // Create test assets for org 1
        AssetCreateDTO dto = new AssetCreateDTO();
        dto.setName("测试设备");
        dto.setAssetCode("TST-001");
        dto.setCategory("EQUIPMENT");
        dto.setOrgId(1L);
        dto.setTenantId(0L);
        dto.setOriginalValue(new BigDecimal("10000.00"));
        dto.setUseStatus("IN_USE");
        dto.setCustodian("测试人");
        dto.setPurchaseDate(LocalDate.of(2024, 1, 1));
        assetService.create(dto);

        // Generate two reports
        reportService.generate("ASSET_SUMMARY", 1L, "2025-Q1", 0L);
        reportService.generate("ASSET_SUMMARY", 1L, "2025-Q2", 0L);

        // ---- when ----
        List<Report> reports = reportService.listByOrg(1L);

        // ---- then ----
        assertThat(reports).hasSize(2);
        assertThat(reports.get(0).getPeriod()).isEqualTo("2025-Q2");
        assertThat(reports.get(1).getPeriod()).isEqualTo("2025-Q1");
    }
}
