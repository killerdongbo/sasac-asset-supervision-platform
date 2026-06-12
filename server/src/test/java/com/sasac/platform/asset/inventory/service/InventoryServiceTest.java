package com.sasac.platform.asset.inventory.service;

import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.inventory.entity.InventoryDiff;
import com.sasac.platform.asset.inventory.entity.InventoryRecord;
import com.sasac.platform.asset.inventory.entity.InventoryTask;
import com.sasac.platform.asset.mapper.AssetMapper;
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
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AssetMapper assetMapper;

    @Test
    void shouldCreateTask() {
        InventoryTask task = createSampleTask();
        task.setTaskName("2024年度资产盘点");

        InventoryTask created = inventoryService.createTask(task);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTaskName()).isEqualTo("2024年度资产盘点");
        assertThat(created.getStatus()).isEqualTo("PENDING");
        assertThat(created.getTotalCount()).isEqualTo(100);
        assertThat(created.getCompletedCount()).isZero();
        assertThat(created.getDiffCount()).isZero();
    }

    @Test
    void shouldGetMyTasks() {
        InventoryTask task1 = createSampleTask();
        task1.setTaskName("盘点任务A");
        inventoryService.createTask(task1);

        InventoryTask task2 = createSampleTask();
        task2.setTaskName("盘点任务B");
        inventoryService.createTask(task2);

        List<InventoryTask> myTasks = inventoryService.getMyTasks(1001L);
        assertThat(myTasks).hasSize(2);
        assertThat(myTasks).extracting(InventoryTask::getTaskName)
                .containsExactlyInAnyOrder("盘点任务A", "盘点任务B");
    }

    @Test
    void shouldGetRecordsByTask() {
        // Create a task and a sample asset
        InventoryTask task = createSampleTask();
        InventoryTask createdTask = inventoryService.createTask(task);

        Asset asset = createSampleAsset();
        asset.setName("测试资产");
        asset.setAssetCode("ZC-001");
        asset.setLocation("A栋3楼");
        asset.setUseStatus("IN_USE");
        assetMapper.insert(asset);

        // Record inventory for the asset
        InventoryRecord record = new InventoryRecord();
        record.setTaskId(createdTask.getId());
        record.setAssetId(asset.getId());
        record.setExists(true);
        record.setActualLocation("A栋3楼");
        record.setActualStatus("IN_USE");
        record.setOperatorId(1001L);
        record.setTenantId(0L);

        inventoryService.recordInventory(record);

        // Verify records
        List<InventoryRecord> records = inventoryService.getRecords(createdTask.getId());
        assertThat(records).hasSize(1);

        InventoryRecord saved = records.get(0);
        assertThat(saved.getAssetId()).isEqualTo(asset.getId());
        assertThat(saved.getBookName()).isEqualTo("测试资产");
        assertThat(saved.getBookLocation()).isEqualTo("A栋3楼");
        assertThat(saved.getBookStatus()).isEqualTo("IN_USE");
    }

    @Test
    void shouldDetectShortageWhenAssetNotExists() {
        InventoryTask task = createSampleTask();
        InventoryTask createdTask = inventoryService.createTask(task);

        Asset asset = createSampleAsset();
        asset.setName("缺失资产");
        asset.setAssetCode("ZC-002");
        asset.setLocation("B栋仓库");
        asset.setUseStatus("IN_USE");
        assetMapper.insert(asset);

        InventoryRecord record = new InventoryRecord();
        record.setTaskId(createdTask.getId());
        record.setAssetId(asset.getId());
        record.setExists(false);
        record.setOperatorId(1001L);
        record.setTenantId(0L);

        inventoryService.recordInventory(record);

        // Verify diff was auto-created
        List<InventoryDiff> diffs = inventoryService.getDiffs(createdTask.getId());
        assertThat(diffs).hasSize(1);
        InventoryDiff diff = diffs.get(0);
        assertThat(diff.getDiffType()).isEqualTo("SHORTAGE");
        assertThat(diff.getStatus()).isEqualTo("OPEN");
        assertThat(diff.getAssetId()).isEqualTo(asset.getId());

        // Verify task counts updated
        InventoryTask updatedTask = inventoryService.getMyTasks(1001L).stream()
                .filter(t -> t.getId().equals(createdTask.getId()))
                .findFirst().orElse(null);
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getCompletedCount()).isEqualTo(1);
        assertThat(updatedTask.getDiffCount()).isEqualTo(1);
    }

    @Test
    void shouldDetectWrongLocation() {
        InventoryTask task = createSampleTask();
        InventoryTask createdTask = inventoryService.createTask(task);

        Asset asset = createSampleAsset();
        asset.setName("位置错误资产");
        asset.setAssetCode("ZC-003");
        asset.setLocation("A栋3楼");
        asset.setUseStatus("IN_USE");
        assetMapper.insert(asset);

        InventoryRecord record = new InventoryRecord();
        record.setTaskId(createdTask.getId());
        record.setAssetId(asset.getId());
        record.setExists(true);
        record.setActualLocation("C栋5楼");
        record.setActualStatus("IN_USE");
        record.setOperatorId(1001L);
        record.setTenantId(0L);

        inventoryService.recordInventory(record);

        List<InventoryDiff> diffs = inventoryService.getDiffs(createdTask.getId());
        assertThat(diffs).hasSize(1);
        InventoryDiff diff = diffs.get(0);
        assertThat(diff.getDiffType()).isEqualTo("WRONG_LOCATION");
        assertThat(diff.getStatus()).isEqualTo("OPEN");
        assertThat(diff.getBookValue()).isEqualTo("A栋3楼");
        assertThat(diff.getActualValue()).isEqualTo("C栋5楼");
    }

    @Test
    void shouldDetectStatusMismatch() {
        InventoryTask task = createSampleTask();
        InventoryTask createdTask = inventoryService.createTask(task);

        Asset asset = createSampleAsset();
        asset.setName("状态异常资产");
        asset.setAssetCode("ZC-004");
        asset.setLocation("A栋3楼");
        asset.setUseStatus("IN_USE");
        assetMapper.insert(asset);

        InventoryRecord record = new InventoryRecord();
        record.setTaskId(createdTask.getId());
        record.setAssetId(asset.getId());
        record.setExists(true);
        record.setActualLocation("A栋3楼");
        record.setActualStatus("IDLE");
        record.setOperatorId(1001L);
        record.setTenantId(0L);

        inventoryService.recordInventory(record);

        List<InventoryDiff> diffs = inventoryService.getDiffs(createdTask.getId());
        assertThat(diffs).hasSize(1);
        InventoryDiff diff = diffs.get(0);
        assertThat(diff.getDiffType()).isEqualTo("STATUS_MISMATCH");
        assertThat(diff.getBookValue()).isEqualTo("IN_USE");
        assertThat(diff.getActualValue()).isEqualTo("IDLE");
    }

    // ---- helpers ----

    private InventoryTask createSampleTask() {
        InventoryTask task = new InventoryTask();
        task.setTaskName("默认盘点任务");
        task.setAssigneeId(1001L);
        task.setScopeType("ORG");
        task.setScopeValue("1001");
        task.setStartDate(LocalDate.of(2024, 6, 1));
        task.setEndDate(LocalDate.of(2024, 6, 30));
        task.setTotalCount(100);
        task.setTenantId(0L);
        return task;
    }

    private Asset createSampleAsset() {
        Asset asset = new Asset();
        asset.setName("样本资产");
        asset.setAssetCode("ZC-SAMPLE");
        asset.setCategory("IT设备");
        asset.setOrgId(1L);
        asset.setTenantId(0L);
        return asset;
    }
}
