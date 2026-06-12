package com.sasac.platform.supervision.alert.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.supervision.alert.entity.AlertRecord;
import com.sasac.platform.supervision.alert.entity.AlertRule;
import com.sasac.platform.supervision.alert.service.AlertService;
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
 * REST controller for alert center operations.
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    /**
     * Creates a new alert rule.
     */
    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<AlertRule>> createRule(@Valid @RequestBody AlertRule rule) {
        AlertRule created = alertService.createRule(rule);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Updates an existing alert rule.
     */
    @PutMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<AlertRule>> updateRule(@PathVariable Long id,
                                                             @Valid @RequestBody AlertRule rule) {
        AlertRule updated = alertService.updateRule(id, rule);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Lists alert rules for a tenant.
     */
    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<AlertRule>>> listRules(@RequestParam Long tenantId,
                                                                  @RequestParam(required = false) String alertType) {
        List<AlertRule> rules = alertService.listRules(tenantId, alertType);
        return ResponseEntity.ok(ApiResponse.success(rules));
    }

    /**
     * Lists alert records for a tenant.
     */
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<AlertRecord>>> listRecords(@RequestParam Long tenantId,
                                                                      @RequestParam(required = false) String alertType,
                                                                      @RequestParam(required = false) String status) {
        List<AlertRecord> records = alertService.listRecords(tenantId, alertType, status);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Acknowledges an alert record.
     */
    @PutMapping("/records/{id}/acknowledge")
    public ResponseEntity<ApiResponse<Void>> acknowledge(@PathVariable Long id,
                                                         @RequestParam Long handledBy) {
        alertService.acknowledge(id, handledBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Resolves an alert record.
     */
    @PutMapping("/records/{id}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolve(@PathVariable Long id,
                                                     @RequestParam Long handledBy) {
        alertService.resolve(id, handledBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
