package com.sasac.platform.asset.maintenance.service;

import com.sasac.platform.asset.inspection.entity.InspectionAnomaly;
import com.sasac.platform.asset.inspection.mapper.InspectionAnomalyMapper;
import com.sasac.platform.asset.maintenance.entity.MaintenanceRecord;
import com.sasac.platform.asset.maintenance.entity.MaintenanceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MaintenanceServiceTest {

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private InspectionAnomalyMapper inspectionAnomalyMapper;

    @Test
    void shouldCreateRequest() {
        MaintenanceRequest req = createSampleRequest();

        MaintenanceRequest created = maintenanceService.createRequest(req);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getAssetId()).isEqualTo(1001L);
        assertThat(created.getFaultDescription()).isEqualTo("设备运行异常");
        assertThat(created.getPriority()).isEqualTo("MEDIUM");
        assertThat(created.getSourceType()).isEqualTo("MANUAL");
        assertThat(created.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void shouldCreateRequestFromAnomaly() {
        // Create a sample anomaly first
        InspectionAnomaly anomaly = new InspectionAnomaly();
        anomaly.setTaskId(1L);
        anomaly.setRecordId(1L);
        anomaly.setAssetId(2001L);
        anomaly.setAnomalyType("DAMAGED");
        anomaly.setDescription("设备外壳破损");
        anomaly.setStatus("OPEN");
        anomaly.setTenantId(0L);
        inspectionAnomalyMapper.insert(anomaly);
        assertThat(anomaly.getId()).isNotNull();

        // Transfer anomaly to maintenance
        MaintenanceRequest created = maintenanceService.createFromAnomaly(anomaly.getId());
        assertThat(created.getId()).isNotNull();
        assertThat(created.getSourceType()).isEqualTo("FROM_INSPECTION");
        assertThat(created.getSourceAnomalyId()).isEqualTo(anomaly.getId());
        assertThat(created.getAssetId()).isEqualTo(2001L);
        assertThat(created.getFaultDescription()).isEqualTo("设备外壳破损");
        assertThat(created.getStatus()).isEqualTo("PENDING");

        // Verify anomaly is updated
        InspectionAnomaly updatedAnomaly = inspectionAnomalyMapper.selectById(anomaly.getId());
        assertThat(updatedAnomaly.getResolution()).isEqualTo("TRANSFER_TO_MAINTENANCE");
        assertThat(updatedAnomaly.getStatus()).isEqualTo("RESOLVED");
        assertThat(updatedAnomaly.getMaintenanceRequestId()).isNotNull();
    }

    @Test
    void shouldCompleteMaintenance() {
        // Create a request first
        MaintenanceRequest req = createSampleRequest();
        req.setPriority("HIGH");
        req.setExpectedDate(LocalDate.of(2024, 7, 15));
        MaintenanceRequest created = maintenanceService.createRequest(req);

        // Complete maintenance with record
        MaintenanceRecord record = new MaintenanceRecord();
        record.setRequestId(created.getId());
        record.setProcessDescription("更换了损坏部件");
        record.setResult("FIXED");
        record.setCost(new BigDecimal("1500.00"));
        record.setServiceProvider("第三方维修公司");
        record.setTenantId(0L);

        maintenanceService.completeMaintenance(created.getId(), record);

        // Verify record is persisted
        assertThat(record.getId()).isNotNull();
        assertThat(record.getCompletionTime()).isNotNull();

        // Verify request status is updated
        List<MaintenanceRequest> requests = maintenanceService.getRequests(0L);
        assertThat(requests).isNotEmpty();
        MaintenanceRequest updated = requests.stream()
                .filter(r -> r.getId().equals(created.getId()))
                .findFirst().orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldGetRequestsByTenant() {
        MaintenanceRequest req1 = createSampleRequest();
        req1.setFaultDescription("故障A");
        maintenanceService.createRequest(req1);

        MaintenanceRequest req2 = createSampleRequest();
        req2.setFaultDescription("故障B");
        maintenanceService.createRequest(req2);

        List<MaintenanceRequest> requests = maintenanceService.getRequests(0L);
        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(MaintenanceRequest::getFaultDescription)
                .containsExactlyInAnyOrder("故障A", "故障B");
    }

    // ---- helpers ----

    private MaintenanceRequest createSampleRequest() {
        MaintenanceRequest req = new MaintenanceRequest();
        req.setAssetId(1001L);
        req.setProviderId(5001L);
        req.setFaultDescription("设备运行异常");
        req.setPriority("MEDIUM");
        req.setSourceType("MANUAL");
        req.setStatus("PENDING");
        req.setTenantId(0L);
        return req;
    }
}
