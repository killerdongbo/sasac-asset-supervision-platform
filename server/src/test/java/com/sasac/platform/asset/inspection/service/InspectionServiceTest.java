package com.sasac.platform.asset.inspection.service;

import com.sasac.platform.asset.inspection.entity.InspectionAnomaly;
import com.sasac.platform.asset.inspection.entity.InspectionRecord;
import com.sasac.platform.asset.inspection.entity.InspectionTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class InspectionServiceTest {

    @Autowired
    private InspectionService inspectionService;

    @Test
    void shouldCreateTask() {
        InspectionTask task = createSampleTask();
        task.setTaskName("2024年度资产盘点");

        InspectionTask created = inspectionService.createTask(task);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTaskName()).isEqualTo("2024年度资产盘点");
        assertThat(created.getStatus()).isEqualTo("PENDING");
        assertThat(created.getCompletedCount()).isZero();
    }

    @Test
    void shouldRecordNormalInspection() {
        InspectionTask task = createSampleTask();
        InspectionTask createdTask = inspectionService.createTask(task);

        InspectionRecord record = new InspectionRecord();
        record.setTaskId(createdTask.getId());
        record.setAssetId(1L);
        record.setIsNormal(true);
        record.setActualLocation("A栋3楼机房");
        record.setActualStatus("IN_USE");
        record.setInspectorId(1001L);
        record.setTenantId(0L);

        inspectionService.recordInspection(record);
        assertThat(record.getId()).isNotNull();

        List<InspectionRecord> records = inspectionService.getRecords(createdTask.getId());
        assertThat(records).hasSize(1);

        // No anomaly should be created for normal inspections
        List<InspectionAnomaly> anomalies = inspectionService.getAnomalies(createdTask.getId());
        assertThat(anomalies).isEmpty();
    }

    @Test
    void shouldAutoCreateAnomalyWhenNotNormal() {
        InspectionTask task = createSampleTask();
        InspectionTask createdTask = inspectionService.createTask(task);

        InspectionRecord record = new InspectionRecord();
        record.setTaskId(createdTask.getId());
        record.setAssetId(1L);
        record.setIsNormal(false);
        record.setAnomalyType("DAMAGED");
        record.setDescription("设备外壳有明显破损");
        record.setInspectorId(1001L);
        record.setTenantId(0L);

        inspectionService.recordInspection(record);
        assertThat(record.getId()).isNotNull();

        List<InspectionAnomaly> anomalies = inspectionService.getAnomalies(createdTask.getId());
        assertThat(anomalies).hasSize(1);
        InspectionAnomaly anomaly = anomalies.get(0);
        assertThat(anomaly.getAnomalyType()).isEqualTo("DAMAGED");
        assertThat(anomaly.getStatus()).isEqualTo("OPEN");
        assertThat(anomaly.getRecordId()).isEqualTo(record.getId());
    }

    @Test
    void shouldGetMyTasks() {
        InspectionTask task1 = createSampleTask();
        task1.setTaskName("盘点任务A");
        inspectionService.createTask(task1);

        InspectionTask task2 = createSampleTask();
        task2.setTaskName("盘点任务B");
        inspectionService.createTask(task2);

        List<InspectionTask> myTasks = inspectionService.getMyTasks(1001L);
        assertThat(myTasks).hasSize(2);
        assertThat(myTasks).extracting(InspectionTask::getTaskName)
                .containsExactlyInAnyOrder("盘点任务A", "盘点任务B");
    }

    @Test
    void shouldResolveAnomaly() {
        InspectionTask task = createSampleTask();
        InspectionTask createdTask = inspectionService.createTask(task);

        InspectionRecord record = new InspectionRecord();
        record.setTaskId(createdTask.getId());
        record.setAssetId(1L);
        record.setIsNormal(false);
        record.setAnomalyType("NOT_FOUND");
        record.setDescription("设备未找到");
        record.setInspectorId(1001L);
        record.setTenantId(0L);
        inspectionService.recordInspection(record);

        List<InspectionAnomaly> anomalies = inspectionService.getAnomalies(createdTask.getId());
        assertThat(anomalies).hasSize(1);
        InspectionAnomaly anomaly = anomalies.get(0);
        assertThat(anomaly.getStatus()).isEqualTo("OPEN");

        inspectionService.resolveAnomaly(anomaly.getId(), "RECTIFY");

        List<InspectionAnomaly> resolvedAnomalies = inspectionService.getAnomalies(createdTask.getId());
        assertThat(resolvedAnomalies).hasSize(1);
        InspectionAnomaly resolved = resolvedAnomalies.get(0);
        assertThat(resolved.getResolution()).isEqualTo("RECTIFY");
        assertThat(resolved.getStatus()).isEqualTo("RESOLVED");
    }

    // ---- helpers ----

    private InspectionTask createSampleTask() {
        InspectionTask task = new InspectionTask();
        task.setTaskName("默认盘点任务");
        task.setAssigneeId(1001L);
        task.setAssetScope("[1,2,3]");
        task.setStartDate(LocalDate.of(2024, 6, 1));
        task.setEndDate(LocalDate.of(2024, 6, 30));
        task.setTotalCount(3);
        task.setTenantId(0L);
        return task;
    }
}
