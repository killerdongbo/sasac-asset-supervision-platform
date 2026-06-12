package com.sasac.platform.supervision.audit.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.supervision.audit.entity.OperationLog;
import com.sasac.platform.supervision.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for audit trail queries.
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * Retrieves operation logs for a specific target.
     *
     * @param targetType the target entity type
     * @param targetId   the target entity ID
     * @return list of operation logs
     */
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<OperationLog>>> getLogs(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        List<OperationLog> logs = auditService.getLogs(targetType, targetId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * Retrieves the combined asset lifecycle timeline.
     *
     * @param assetId the asset ID
     * @return timeline entries merged from operation logs and field change logs
     */
    @GetMapping("/asset-lifecycle/{assetId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAssetLifecycle(
            @PathVariable Long assetId) {
        List<Map<String, Object>> timeline = auditService.getAssetLifecycle(assetId);
        return ResponseEntity.ok(ApiResponse.success(timeline));
    }
}
