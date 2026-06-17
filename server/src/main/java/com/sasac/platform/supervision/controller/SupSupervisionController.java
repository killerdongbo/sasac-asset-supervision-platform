package com.sasac.platform.supervision.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.supervision.dto.AuditPlanDTO;
import com.sasac.platform.supervision.dto.CaseDTO;
import com.sasac.platform.supervision.dto.FindingDTO;
import com.sasac.platform.supervision.dto.RectificationDTO;
import com.sasac.platform.supervision.entity.SupAuditFinding;
import com.sasac.platform.supervision.entity.SupAuditPlan;
import com.sasac.platform.supervision.entity.SupRectification;
import com.sasac.platform.supervision.entity.SupViolationCase;
import com.sasac.platform.supervision.service.SupSupervisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for supervision and accountability operations.
 */
@RestController
@RequiredArgsConstructor
public class SupSupervisionController {

    private final SupSupervisionService supSupervisionService;

    // ===== Audit Plans =====

    @GetMapping("/api/supervision/audit-plans")
    public ApiResponse<List<SupAuditPlan>> listAuditPlans(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Integer planYear) {
        return ApiResponse.success(supSupervisionService.listAuditPlans(tenantId, planYear));
    }

    @GetMapping("/api/supervision/audit-plans/{id}")
    public ApiResponse<SupAuditPlan> getAuditPlan(@PathVariable Long id) {
        return ApiResponse.success(supSupervisionService.getAuditPlan(id));
    }

    @PostMapping("/api/supervision/audit-plans")
    public ApiResponse<SupAuditPlan> createAuditPlan(@Valid @RequestBody AuditPlanDTO dto) {
        return ApiResponse.success(supSupervisionService.createAuditPlan(dto));
    }

    @PutMapping("/api/supervision/audit-plans/{id}")
    public ApiResponse<SupAuditPlan> updateAuditPlan(@PathVariable Long id, @Valid @RequestBody AuditPlanDTO dto) {
        return ApiResponse.success(supSupervisionService.updateAuditPlan(id, dto));
    }

    @DeleteMapping("/api/supervision/audit-plans/{id}")
    public ApiResponse<Void> deleteAuditPlan(@PathVariable Long id) {
        supSupervisionService.deleteAuditPlan(id);
        return ApiResponse.success(null);
    }

    // ===== Audit Findings =====

    @GetMapping("/api/supervision/findings")
    public ApiResponse<List<SupAuditFinding>> listFindings(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) String severity) {
        return ApiResponse.success(supSupervisionService.listFindings(tenantId, planId, severity));
    }

    @PostMapping("/api/supervision/findings")
    public ApiResponse<SupAuditFinding> recordFinding(@Valid @RequestBody FindingDTO dto) {
        return ApiResponse.success(supSupervisionService.recordFinding(dto));
    }

    // ===== Rectifications =====

    @GetMapping("/api/supervision/rectifications")
    public ApiResponse<List<SupRectification>> listRectifications(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long findingId,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(supSupervisionService.listRectifications(tenantId, findingId, status));
    }

    @PostMapping("/api/supervision/rectifications")
    public ApiResponse<SupRectification> assignRectification(
            @RequestParam Long findingId,
            @Valid @RequestBody RectificationDTO dto) {
        return ApiResponse.success(supSupervisionService.assignRectification(findingId, dto));
    }

    @PutMapping("/api/supervision/rectifications/{id}/verify")
    public ApiResponse<SupRectification> verifyRectification(
            @PathVariable Long id,
            @RequestParam String result) {
        return ApiResponse.success(supSupervisionService.verifyRectification(id, result));
    }

    @GetMapping("/api/supervision/rectifications/overdue")
    public ApiResponse<List<SupRectification>> checkOverdueRectifications(
            @RequestParam Long tenantId) {
        return ApiResponse.success(supSupervisionService.checkOverdueRectifications(tenantId));
    }

    // ===== Violation Cases =====

    @GetMapping("/api/supervision/cases")
    public ApiResponse<List<SupViolationCase>> listCases(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String violationType) {
        return ApiResponse.success(supSupervisionService.listCases(tenantId, status, violationType));
    }

    @GetMapping("/api/supervision/cases/{id}")
    public ApiResponse<SupViolationCase> getCase(@PathVariable Long id) {
        return ApiResponse.success(supSupervisionService.getCase(id));
    }

    @PostMapping("/api/supervision/cases")
    public ApiResponse<SupViolationCase> openCase(@Valid @RequestBody CaseDTO dto) {
        return ApiResponse.success(supSupervisionService.openCase(dto));
    }

    @PutMapping("/api/supervision/cases/{id}/investigate")
    public ApiResponse<SupViolationCase> investigate(
            @PathVariable Long id,
            @RequestParam String result,
            @RequestParam(required = false) BigDecimal assetLoss) {
        return ApiResponse.success(supSupervisionService.investigate(id, result, assetLoss));
    }

    @PutMapping("/api/supervision/cases/{id}/decide")
    public ApiResponse<SupViolationCase> decidePunishment(
            @PathVariable Long id,
            @RequestParam String decision) {
        return ApiResponse.success(supSupervisionService.decidePunishment(id, decision));
    }
}
