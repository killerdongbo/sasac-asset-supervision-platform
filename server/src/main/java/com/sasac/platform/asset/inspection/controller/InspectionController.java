package com.sasac.platform.asset.inspection.controller;

import com.sasac.platform.asset.inspection.entity.InspectionAnomaly;
import com.sasac.platform.asset.inspection.entity.InspectionRecord;
import com.sasac.platform.asset.inspection.entity.InspectionTask;
import com.sasac.platform.asset.inspection.mapper.InspectionAnomalyMapper;
import com.sasac.platform.asset.inspection.service.InspectionService;
import com.sasac.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for inspection management.
 * <p>
 * Endpoints for inspection-task, inspection-record, and
 * inspection-anomaly operations.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;
    private final InspectionAnomalyMapper anomalyMapper;

    // ---- Inspection Task ----

    /**
     * Creates a new inspection task.
     */
    @PostMapping("/inspection-tasks")
    public ResponseEntity<ApiResponse<InspectionTask>> createTask(
            @Valid @RequestBody InspectionTask task) {
        InspectionTask created = inspectionService.createTask(task);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Retrieves tasks assigned to the current inspector.
     *
     * @param assigneeId the user ID of the assignee
     */
    @GetMapping("/inspection-tasks/my")
    public ResponseEntity<ApiResponse<List<InspectionTask>>> getMyTasks(
            @RequestParam(required = false) Long assigneeId) {
        List<InspectionTask> tasks = inspectionService.getMyTasks(assigneeId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /**
     * Completes an inspection task.
     */
    @PutMapping("/inspection-tasks/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeTask(@PathVariable Long id) {
        inspectionService.completeTask(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ---- Inspection Record ----

    /**
     * Records an inspection result for a single asset.
     */
    @PostMapping("/inspection-records")
    public ResponseEntity<ApiResponse<Void>> recordInspection(
            @Valid @RequestBody InspectionRecord record) {
        inspectionService.recordInspection(record);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Retrieves all inspection records for a task.
     */
    @GetMapping("/inspection-tasks/{taskId}/records")
    public ResponseEntity<ApiResponse<List<InspectionRecord>>> getRecords(
            @PathVariable Long taskId) {
        List<InspectionRecord> records = inspectionService.getRecords(taskId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    // ---- Inspection Anomaly ----

    /**
     * Lists all inspection anomalies optionally filtered by task.
     */
    @GetMapping("/inspection-anomalies")
    public ResponseEntity<ApiResponse<List<InspectionAnomaly>>> listAllAnomalies(
            @RequestParam(required = false) Long taskId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InspectionAnomaly> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (taskId != null) {
            wrapper.eq(InspectionAnomaly::getTaskId, taskId);
        }
        wrapper.orderByDesc(InspectionAnomaly::getId);
        return ResponseEntity.ok(ApiResponse.success(anomalyMapper.selectList(wrapper)));
    }

    /**
     * Retrieves all anomalies for a task.
     */
    @GetMapping("/inspection-tasks/{taskId}/anomalies")
    public ResponseEntity<ApiResponse<List<InspectionAnomaly>>> getAnomalies(
            @PathVariable Long taskId) {
        List<InspectionAnomaly> anomalies = inspectionService.getAnomalies(taskId);
        return ResponseEntity.ok(ApiResponse.success(anomalies));
    }

    /**
     * Resolves an anomaly with a given resolution type.
     *
     * @param id         the anomaly ID
     * @param resolution the resolution type (RECTIFY, TRANSFER_TO_MAINTENANCE, VERIFY)
     */
    @PutMapping("/inspection-anomalies/{id}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolveAnomaly(
            @PathVariable Long id,
            @RequestParam String resolution) {
        inspectionService.resolveAnomaly(id, resolution);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Transfers an anomaly to maintenance.
     */
    @PutMapping("/inspection-anomalies/{id}/transfer-to-maintenance")
    public ResponseEntity<ApiResponse<Void>> transferToMaintenance(@PathVariable Long id) {
        inspectionService.transferToMaintenance(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
