package com.sasac.platform.asset.inspection.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.inspection.entity.InspectionAnomaly;
import com.sasac.platform.asset.inspection.entity.InspectionRecord;
import com.sasac.platform.asset.inspection.entity.InspectionTask;
import com.sasac.platform.asset.inspection.mapper.InspectionAnomalyMapper;
import com.sasac.platform.asset.inspection.mapper.InspectionRecordMapper;
import com.sasac.platform.asset.inspection.mapper.InspectionTaskMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for inspection task management, inspection recording,
 * anomaly detection, and resolution.
 */
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionTaskMapper inspectionTaskMapper;
    private final InspectionRecordMapper inspectionRecordMapper;
    private final InspectionAnomalyMapper inspectionAnomalyMapper;

    // ---- Inspection Task ----

    /**
     * Creates a new inspection task with PENDING status.
     *
     * @param task the task to create
     * @return the created task with generated ID
     */
    @Transactional
    public InspectionTask createTask(InspectionTask task) {
        task.setStatus("PENDING");
        task.setCompletedCount(0);
        inspectionTaskMapper.insert(task);
        return task;
    }

    /**
     * Retrieves tasks assigned to a specific user.
     *
     * @param assigneeId the user ID of the assignee
     * @return list of tasks assigned to the user
     */
    public List<InspectionTask> getMyTasks(Long assigneeId) {
        LambdaQueryWrapper<InspectionTask> wrapper = new LambdaQueryWrapper<>();
        if (assigneeId != null) {
            wrapper.eq(InspectionTask::getAssigneeId, assigneeId);
        }
        wrapper.orderByDesc(InspectionTask::getId);
        return inspectionTaskMapper.selectList(wrapper);
    }

    /**
     * Marks a task as COMPLETED.
     *
     * @param taskId the task ID to complete
     * @throws BusinessException if the task is not found or not all assets have been inspected
     */
    @Transactional
    public void completeTask(Long taskId) {
        InspectionTask task = inspectionTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("巡检任务不存在");
        }
        if (task.getCompletedCount() < task.getTotalCount()) {
            throw new BusinessException("尚有未巡检资产，无法完成");
        }
        task.setStatus("COMPLETED");
        inspectionTaskMapper.updateById(task);
    }

    // ---- Inspection Record ----

    /**
     * Records an inspection result for a single asset.
     * <p>
     * If the asset is not in normal condition ({@code isNormal == false}),
     * an {@link InspectionAnomaly} is automatically created. The parent
     * task's {@code completedCount} is incremented.
     *
     * @param record the inspection record to persist
     * @throws BusinessException if the parent task is not found or all assets already inspected
     */
    @Transactional
    public void recordInspection(InspectionRecord record) {
        InspectionTask task = inspectionTaskMapper.selectById(record.getTaskId());
        if (task == null) {
            throw new BusinessException("巡检任务不存在");
        }
        if (task.getCompletedCount() >= task.getTotalCount()) {
            throw new BusinessException("该任务所有资产已巡检完毕");
        }

        inspectionRecordMapper.insert(record);

        // Auto-create anomaly for non-normal inspections
        if (Boolean.FALSE.equals(record.getIsNormal())) {
            InspectionAnomaly anomaly = new InspectionAnomaly();
            anomaly.setTaskId(record.getTaskId());
            anomaly.setRecordId(record.getId());
            anomaly.setAssetId(record.getAssetId());
            anomaly.setAnomalyType(record.getAnomalyType());
            anomaly.setDescription(record.getDescription());
            anomaly.setStatus("OPEN");
            anomaly.setTenantId(record.getTenantId() != null
                    ? record.getTenantId()
                    : task.getTenantId());
            inspectionAnomalyMapper.insert(anomaly);
        }

        // Increment completed count
        task.setCompletedCount(task.getCompletedCount() + 1);
        inspectionTaskMapper.updateById(task);
    }

    /**
     * Retrieves all inspection records for a given task.
     *
     * @param taskId the task ID
     * @return list of inspection records
     */
    public List<InspectionRecord> getRecords(Long taskId) {
        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionRecord::getTaskId, taskId);
        wrapper.orderByDesc(InspectionRecord::getId);
        return inspectionRecordMapper.selectList(wrapper);
    }

    // ---- Inspection Anomaly ----

    /**
     * Retrieves all anomalies for a given task.
     *
     * @param taskId the task ID
     * @return list of anomalies
     */
    public List<InspectionAnomaly> getAnomalies(Long taskId) {
        LambdaQueryWrapper<InspectionAnomaly> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionAnomaly::getTaskId, taskId);
        wrapper.orderByDesc(InspectionAnomaly::getId);
        return inspectionAnomalyMapper.selectList(wrapper);
    }

    /**
     * Resolves an anomaly by setting its resolution and marking it as RESOLVED.
     *
     * @param anomalyId  the anomaly ID
     * @param resolution the resolution type (RECTIFY, TRANSFER_TO_MAINTENANCE, VERIFY)
     * @throws BusinessException if the anomaly is not found
     */
    @Transactional
    public void resolveAnomaly(Long anomalyId, String resolution) {
        InspectionAnomaly anomaly = inspectionAnomalyMapper.selectById(anomalyId);
        if (anomaly == null) {
            throw new BusinessException("异常记录不存在");
        }
        anomaly.setResolution(resolution);
        anomaly.setStatus("RESOLVED");
        inspectionAnomalyMapper.updateById(anomaly);
    }

    /**
     * Marks an anomaly as transferred to maintenance.
     *
     * @param anomalyId the anomaly ID
     * @throws BusinessException if the anomaly is not found
     */
    @Transactional
    public void transferToMaintenance(Long anomalyId) {
        InspectionAnomaly anomaly = inspectionAnomalyMapper.selectById(anomalyId);
        if (anomaly == null) {
            throw new BusinessException("异常记录不存在");
        }
        anomaly.setResolution("TRANSFER_TO_MAINTENANCE");
        anomaly.setStatus("RESOLVED");
        inspectionAnomalyMapper.updateById(anomaly);
    }
}
