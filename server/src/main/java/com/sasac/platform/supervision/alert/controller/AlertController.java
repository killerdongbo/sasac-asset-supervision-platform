package com.sasac.platform.supervision.alert.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.supervision.alert.entity.AlertRecord;
import com.sasac.platform.supervision.alert.entity.AlertRule;
import com.sasac.platform.supervision.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // ===== Alert Rules =====

    @GetMapping("/api/alert-rules")
    public ApiResponse<List<AlertRule>> listRules(@RequestParam(required = false) Long tenantId) {
        return ApiResponse.success(alertService.listRules(tenantId, null));
    }

    @PostMapping("/api/alert-rules")
    public ApiResponse<AlertRule> createRule(@RequestBody AlertRule rule) {
        return ApiResponse.success(alertService.createRule(rule));
    }

    // ===== Alert Records (名单) =====

    @GetMapping("/api/alerts")
    public ApiResponse<List<AlertRecord>> listRecords(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(alertService.listRecords(tenantId, alertType, status));
    }

    @PutMapping("/api/alerts/{id}/acknowledge")
    public ApiResponse<Void> acknowledge(@PathVariable Long id) {
        alertService.acknowledge(id, null);
        return ApiResponse.success(null);
    }

    @PutMapping("/api/alerts/{id}/resolve")
    public ApiResponse<Void> resolve(@PathVariable Long id) {
        alertService.resolve(id, null);
        return ApiResponse.success(null);
    }
}
