package com.sasac.platform.asset.maintenance.controller;

import com.sasac.platform.asset.maintenance.entity.MaintenanceRecord;
import com.sasac.platform.asset.maintenance.entity.MaintenanceRequest;
import com.sasac.platform.asset.maintenance.service.MaintenanceService;
import com.sasac.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for maintenance management.
 * <p>
 * Endpoints for maintenance request creation (manual and from inspection),
 * completion recording, and querying.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    // ---- Maintenance Request ----

    /**
     * Creates a new maintenance request manually.
     */
    @PostMapping("/maintenance-requests")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> createRequest(
            @Valid @RequestBody MaintenanceRequest req) {
        MaintenanceRequest created = maintenanceService.createRequest(req);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Creates a maintenance request from an inspection anomaly.
     *
     * @param anomalyId the ID of the inspection anomaly
     */
    @PostMapping("/maintenance-requests/from-anomaly/{anomalyId}")
    public ResponseEntity<ApiResponse<MaintenanceRequest>> createFromAnomaly(
            @PathVariable Long anomalyId) {
        MaintenanceRequest created = maintenanceService.createFromAnomaly(anomalyId);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Records the completion of a maintenance request.
     *
     * @param id     the maintenance request ID
     * @param record the maintenance record with execution details
     */
    @PostMapping("/maintenance-requests/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRecord record) {
        maintenanceService.completeMaintenance(id, record);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Retrieves all maintenance requests for a tenant.
     *
     * @param tenantId the tenant ID
     */
    @GetMapping("/maintenance-requests")
    public ResponseEntity<ApiResponse<List<MaintenanceRequest>>> getRequests(
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        List<MaintenanceRequest> requests = maintenanceService.getRequests(tenantId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
}
