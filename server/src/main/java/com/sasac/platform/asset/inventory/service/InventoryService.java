package com.sasac.platform.asset.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.inventory.entity.InventoryDiff;
import com.sasac.platform.asset.inventory.entity.InventoryRecord;
import com.sasac.platform.asset.inventory.entity.InventoryTask;
import com.sasac.platform.asset.inventory.mapper.InventoryDiffMapper;
import com.sasac.platform.asset.inventory.mapper.InventoryRecordMapper;
import com.sasac.platform.asset.inventory.mapper.InventoryTaskMapper;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service for inventory task management, physical inventory recording,
 * and automatic discrepancy detection.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryTaskMapper inventoryTaskMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final InventoryDiffMapper inventoryDiffMapper;
    private final AssetMapper assetMapper;

    // ---- Inventory Task ----

    /**
     * Creates a new inventory task with PENDING status.
     *
     * @param task the task to create
     * @return the created task with generated ID
     */
    @Transactional
    public InventoryTask createTask(InventoryTask task) {
        task.setStatus("PENDING");
        task.setCompletedCount(0);
        task.setDiffCount(0);
        inventoryTaskMapper.insert(task);
        return task;
    }

    /**
     * Retrieves tasks assigned to a specific user.
     *
     * @param assigneeId the user ID of the assignee
     * @return list of tasks assigned to the user
     */
    public List<InventoryTask> getMyTasks(Long assigneeId) {
        LambdaQueryWrapper<InventoryTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryTask::getAssigneeId, assigneeId);
        wrapper.orderByDesc(InventoryTask::getId);
        return inventoryTaskMapper.selectList(wrapper);
    }

    /**
     * Retrieves all inventory records for a given task.
     *
     * @param taskId the task ID
     * @return list of inventory records
     */
    public List<InventoryRecord> getRecords(Long taskId) {
        LambdaQueryWrapper<InventoryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryRecord::getTaskId, taskId);
        wrapper.orderByDesc(InventoryRecord::getId);
        return inventoryRecordMapper.selectList(wrapper);
    }

    /**
     * Retrieves all inventory diffs for a given task.
     *
     * @param taskId the task ID
     * @return list of inventory diffs
     */
    public List<InventoryDiff> getDiffs(Long taskId) {
        LambdaQueryWrapper<InventoryDiff> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryDiff::getTaskId, taskId);
        wrapper.orderByDesc(InventoryDiff::getId);
        return inventoryDiffMapper.selectList(wrapper);
    }

    // ---- Inventory Recording ----

    /**
     * Records a physical inventory result for a single asset.
     * <p>
     * Automatically loads the asset's book values, persists the record,
     * detects discrepancies (SHORTAGE, WRONG_LOCATION, STATUS_MISMATCH),
     * and updates the task's completion and diff counts.
     *
     * @param record the inventory record to persist
     * @throws BusinessException if the parent task or asset is not found
     */
    @Transactional
    public void recordInventory(InventoryRecord record) {
        InventoryTask task = inventoryTaskMapper.selectById(record.getTaskId());
        if (task == null) {
            throw new BusinessException("盘点任务不存在");
        }

        Asset asset = assetMapper.selectById(record.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // Fill book values from asset
        record.setBookName(asset.getName());
        record.setBookLocation(asset.getLocation());
        record.setBookStatus(asset.getUseStatus());
        inventoryRecordMapper.insert(record);

        // Auto-detect discrepancies
        boolean hasDiff = false;

        if (Boolean.FALSE.equals(record.getExists())) {
            // Asset not found physically -> SHORTAGE
            InventoryDiff diff = new InventoryDiff();
            diff.setTaskId(record.getTaskId());
            diff.setRecordId(record.getId());
            diff.setAssetId(record.getAssetId());
            diff.setDiffType("SHORTAGE");
            diff.setBookValue(asset.getLocation());
            diff.setActualValue("(缺失)");
            diff.setDescription("资产在盘点中未找到，状态为缺失");
            diff.setStatus("OPEN");
            diff.setTenantId(record.getTenantId() != null
                    ? record.getTenantId()
                    : task.getTenantId());
            inventoryDiffMapper.insert(diff);
            hasDiff = true;

        } else {
            // Check location mismatch
            if (!Objects.equals(record.getActualLocation(), asset.getLocation())) {
                InventoryDiff diff = new InventoryDiff();
                diff.setTaskId(record.getTaskId());
                diff.setRecordId(record.getId());
                diff.setAssetId(record.getAssetId());
                diff.setDiffType("WRONG_LOCATION");
                diff.setBookValue(asset.getLocation());
                diff.setActualValue(record.getActualLocation());
                diff.setDescription("账面位置: " + asset.getLocation()
                        + ", 实际位置: " + record.getActualLocation());
                diff.setStatus("OPEN");
                diff.setTenantId(record.getTenantId() != null
                        ? record.getTenantId()
                        : task.getTenantId());
                inventoryDiffMapper.insert(diff);
                hasDiff = true;
            }

            // Check status mismatch
            if (!Objects.equals(record.getActualStatus(), asset.getUseStatus())) {
                InventoryDiff diff = new InventoryDiff();
                diff.setTaskId(record.getTaskId());
                diff.setRecordId(record.getId());
                diff.setAssetId(record.getAssetId());
                diff.setDiffType("STATUS_MISMATCH");
                diff.setBookValue(asset.getUseStatus());
                diff.setActualValue(record.getActualStatus());
                diff.setDescription("账面状态: " + asset.getUseStatus()
                        + ", 实际状态: " + record.getActualStatus());
                diff.setStatus("OPEN");
                diff.setTenantId(record.getTenantId() != null
                        ? record.getTenantId()
                        : task.getTenantId());
                inventoryDiffMapper.insert(diff);
                hasDiff = true;
            }
        }

        // Update task counters
        task.setCompletedCount(task.getCompletedCount() + 1);
        if (hasDiff) {
            task.setDiffCount(task.getDiffCount() + 1);
        }
        inventoryTaskMapper.updateById(task);
    }
}
