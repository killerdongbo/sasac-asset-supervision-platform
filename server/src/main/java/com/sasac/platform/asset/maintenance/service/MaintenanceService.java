package com.sasac.platform.asset.maintenance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.inspection.entity.InspectionAnomaly;
import com.sasac.platform.asset.inspection.mapper.InspectionAnomalyMapper;
import com.sasac.platform.asset.maintenance.entity.MaintenanceRecord;
import com.sasac.platform.asset.maintenance.entity.MaintenanceRequest;
import com.sasac.platform.asset.maintenance.mapper.MaintenanceRecordMapper;
import com.sasac.platform.asset.maintenance.mapper.MaintenanceRequestMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for maintenance request management, including manual creation,
 * automatic transfer from inspection anomalies, and completion recording.
 */
@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRequestMapper maintenanceRequestMapper;
    private final MaintenanceRecordMapper maintenanceRecordMapper;
    private final InspectionAnomalyMapper inspectionAnomalyMapper;

    // ---- Maintenance Request ----

    /**
     * Creates a new maintenance request with PENDING status.
     *
     * @param req the request to create
     * @return the created request with generated ID
     */
    @Transactional
    public MaintenanceRequest createRequest(MaintenanceRequest req) {
        req.setStatus("PENDING");
        maintenanceRequestMapper.insert(req);
        return req;
    }

    /**
     * Creates a maintenance request from an inspection anomaly.
     * <p>
     * Reads the {@link InspectionAnomaly}, creates a request with
     * {@code sourceType=FROM_INSPECTION}, and updates the anomaly
     * to mark it as resolved and linked.
     *
     * @param anomalyId the ID of the inspection anomaly
     * @return the created maintenance request
     * @throws BusinessException if the anomaly is not found
     */
    @Transactional
    public MaintenanceRequest createFromAnomaly(Long anomalyId) {
        InspectionAnomaly anomaly = inspectionAnomalyMapper.selectById(anomalyId);
        if (anomaly == null) {
            throw new BusinessException("异常记录不存在");
        }

        MaintenanceRequest req = new MaintenanceRequest();
        req.setAssetId(anomaly.getAssetId());
        req.setFaultDescription(anomaly.getDescription());
        req.setPriority("MEDIUM");
        req.setSourceType("FROM_INSPECTION");
        req.setSourceAnomalyId(anomaly.getId());
        req.setStatus("PENDING");
        req.setTenantId(anomaly.getTenantId());
        maintenanceRequestMapper.insert(req);

        // Mark anomaly as transferred to maintenance
        anomaly.setResolution("TRANSFER_TO_MAINTENANCE");
        anomaly.setStatus("RESOLVED");
        anomaly.setMaintenanceRequestId(String.valueOf(req.getId()));
        inspectionAnomalyMapper.updateById(anomaly);

        return req;
    }

    /**
     * Completes a maintenance request by recording its execution details.
     * <p>
     * Inserts a {@link MaintenanceRecord} and updates the request status
     * to COMPLETED.
     *
     * @param requestId the request ID to complete
     * @param record    the maintenance record with execution details
     * @throws BusinessException if the request is not found or already completed
     */
    @Transactional
    public void completeMaintenance(Long requestId, MaintenanceRecord record) {
        MaintenanceRequest request = maintenanceRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("维修申请不存在");
        }
        if ("COMPLETED".equals(request.getStatus())) {
            throw new BusinessException("维修申请已完结");
        }

        record.setRequestId(requestId);
        record.setCompletionTime(LocalDateTime.now());
        maintenanceRecordMapper.insert(record);

        request.setStatus("COMPLETED");
        maintenanceRequestMapper.updateById(request);
    }

    /**
     * Retrieves all maintenance requests for a given tenant.
     *
     * @param tenantId the tenant ID
     * @return list of maintenance requests
     */
    public List<MaintenanceRequest> getRequests(Long tenantId) {
        LambdaQueryWrapper<MaintenanceRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaintenanceRequest::getTenantId, tenantId);
        wrapper.orderByDesc(MaintenanceRequest::getId);
        return maintenanceRequestMapper.selectList(wrapper);
    }
}
