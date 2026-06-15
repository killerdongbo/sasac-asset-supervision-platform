package com.sasac.platform.system.export;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.system.export.dto.ExportRequestDTO;
import com.sasac.platform.system.export.entity.ExportTask;
import com.sasac.platform.system.export.mapper.ExportTaskMapper;
import com.sasac.platform.system.export.service.ExportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for ExportService.
 * <p>
 * Verifies that export tasks are created, executed asynchronously,
 * and produce completed exports with valid file metadata.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExportServiceIntegrationTest {

    @Autowired
    private ExportService exportService;

    @Autowired
    private ExportTaskMapper exportTaskMapper;

    @Autowired
    private AssetMapper assetMapper;

    private Long testAssetId;

    @BeforeEach
    void setUp() {
        // Create a test asset for export verification
        Asset asset = new Asset();
        asset.setName("测试设备");
        asset.setAssetCode("TEST-" + System.currentTimeMillis());
        asset.setCategory("机器设备类");
        asset.setOrgId(1L);
        asset.setTenantId(0L);
        asset.setOriginalValue(new BigDecimal("100000.00"));
        asset.setCurrentValue(new BigDecimal("80000.00"));
        asset.setAccumulatedDepreciation(new BigDecimal("20000.00"));
        asset.setUseStatus("IN_USE");
        asset.setQuantity(1);
        asset.setPurchaseDate(LocalDate.of(2024, 1, 1));
        assetMapper.insert(asset);
        testAssetId = asset.getId();

        // Clean up previous test export tasks
        exportTaskMapper.delete(new LambdaQueryWrapper<>());
    }

    @AfterEach
    void tearDown() {
        // Clean up the test asset created during setup
        if (testAssetId != null) {
            assetMapper.deleteById(testAssetId);
        }
    }

    @Test
    void shouldExportAssetBaseListSuccessfully() {
        ExportRequestDTO dto = new ExportRequestDTO();
        dto.setExportType(ExportType.ASSET_BASE_LIST);

        ExportTask task = exportService.createTask(0L, 0L, dto);
        assertThat(task.getId()).isNotNull();
        assertThat(task.getStatus()).isEqualTo("PENDING");

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ExportTask t = exportTaskMapper.selectById(task.getId());
            return "COMPLETED".equals(t.getStatus()) || "FAILED".equals(t.getStatus());
        });

        ExportTask result = exportTaskMapper.selectById(task.getId());
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        assertThat(result.getFileName()).isNotNull();
        assertThat(result.getFilePath()).isNotNull();
        assertThat(result.getTotalRows()).isGreaterThan(0);
    }

    @Test
    void shouldExportBalanceSheetSuccessfully() {
        ExportRequestDTO dto = new ExportRequestDTO();
        dto.setExportType(ExportType.BALANCE_SHEET);

        ExportTask task = exportService.createTask(0L, 0L, dto);

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ExportTask t = exportTaskMapper.selectById(task.getId());
            return "COMPLETED".equals(t.getStatus()) || "FAILED".equals(t.getStatus());
        });

        ExportTask result = exportTaskMapper.selectById(task.getId());
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldExportReconciliationTableSuccessfully() {
        ExportRequestDTO dto = new ExportRequestDTO();
        dto.setExportType(ExportType.RECONCILIATION_TABLE);

        ExportTask task = exportService.createTask(0L, 0L, dto);

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ExportTask t = exportTaskMapper.selectById(task.getId());
            return "COMPLETED".equals(t.getStatus()) || "FAILED".equals(t.getStatus());
        });

        ExportTask result = exportTaskMapper.selectById(task.getId());
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldExportMachineryEquipDetailSuccessfully() {
        ExportRequestDTO dto = new ExportRequestDTO();
        dto.setExportType(ExportType.MACHINERY_EQUIP_DETAIL);

        ExportTask task = exportService.createTask(0L, 0L, dto);

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ExportTask t = exportTaskMapper.selectById(task.getId());
            return "COMPLETED".equals(t.getStatus()) || "FAILED".equals(t.getStatus());
        });

        ExportTask result = exportTaskMapper.selectById(task.getId());
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldFailForUnknownExportType() {
        ExportRequestDTO dto = new ExportRequestDTO();
        dto.setExportType("UNKNOWN_TYPE");

        ExportTask task = exportService.createTask(0L, 0L, dto);

        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ExportTask t = exportTaskMapper.selectById(task.getId());
            return "COMPLETED".equals(t.getStatus()) || "FAILED".equals(t.getStatus());
        });

        ExportTask result = exportTaskMapper.selectById(task.getId());
        assertThat(result.getStatus()).isEqualTo("FAILED");
        assertThat(result.getErrorMessage()).isNotNull();
    }
}
