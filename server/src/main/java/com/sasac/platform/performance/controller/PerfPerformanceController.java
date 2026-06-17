package com.sasac.platform.performance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.performance.dto.IncentiveDTO;
import com.sasac.platform.performance.dto.IndicatorDefDTO;
import com.sasac.platform.performance.dto.SalaryBudgetDTO;
import com.sasac.platform.performance.entity.PerfIncentive;
import com.sasac.platform.performance.entity.PerfIndicatorDef;
import com.sasac.platform.performance.entity.PerfSalaryBudget;
import com.sasac.platform.performance.service.PerfPerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for performance and compensation management.
 */
@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerfPerformanceController {

    private final PerfPerformanceService performanceService;

    // ==================== Indicators ====================

    /**
     * Batch defines performance indicators.
     */
    @PostMapping("/indicators/batch")
    public ResponseEntity<ApiResponse<List<PerfIndicatorDef>>> defineIndicators(
            @Valid @RequestBody List<IndicatorDefDTO> dtos) {
        List<PerfIndicatorDef> created = performanceService.defineIndicators(dtos);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Records actual value for a single indicator.
     */
    @PutMapping("/indicators/{id}/actual")
    public ResponseEntity<ApiResponse<PerfIndicatorDef>> recordActual(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {
        PerfIndicatorDef updated = performanceService.recordActual(id, body.get("actualValue"));
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Calculates scores for all indicators of an organization in a given year.
     */
    @PostMapping("/indicators/calculate")
    public ResponseEntity<ApiResponse<List<PerfIndicatorDef>>> calculateScores(
            @RequestParam Long tenantId,
            @RequestParam Long orgId,
            @RequestParam int year) {
        List<PerfIndicatorDef> indicators = performanceService.calculateScores(tenantId, orgId, year);
        return ResponseEntity.ok(ApiResponse.success(indicators));
    }

    /**
     * Gets indicator summary for an organization and year.
     */
    @GetMapping("/indicators")
    public ResponseEntity<ApiResponse<List<PerfIndicatorDef>>> getIndicatorSummary(
            @RequestParam Long tenantId,
            @RequestParam Long orgId,
            @RequestParam int year) {
        List<PerfIndicatorDef> indicators = performanceService.getIndicatorSummary(tenantId, orgId, year);
        return ResponseEntity.ok(ApiResponse.success(indicators));
    }

    // ==================== Salary Budgets ====================

    /**
     * Creates or updates a salary budget.
     */
    @PostMapping("/salary-budgets")
    public ResponseEntity<ApiResponse<PerfSalaryBudget>> manageSalaryBudget(
            @Valid @RequestBody SalaryBudgetDTO dto) {
        PerfSalaryBudget budget = performanceService.manageSalaryBudget(dto);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }

    /**
     * Adjusts the approved budget amount.
     */
    @PostMapping("/salary-budgets/{id}/adjust")
    public ResponseEntity<ApiResponse<PerfSalaryBudget>> adjustBudget(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        BigDecimal adjustment = new BigDecimal(body.get("adjustment").toString());
        String reason = (String) body.get("reason");
        PerfSalaryBudget budget = performanceService.adjustBudget(id, adjustment, reason);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }

    /**
     * Queries salary budgets with pagination.
     */
    @GetMapping("/salary-budgets")
    public ResponseEntity<ApiResponse<List<PerfSalaryBudget>>> querySalaryBudgets(
            @RequestParam Long tenantId,
            @RequestParam(required = false) Integer budgetYear,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<PerfSalaryBudget> result = performanceService.querySalaryBudgets(tenantId, budgetYear, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Gets a single salary budget by ID.
     */
    @GetMapping("/salary-budgets/{id}")
    public ResponseEntity<ApiResponse<PerfSalaryBudget>> getSalaryBudget(@PathVariable Long id) {
        PerfSalaryBudget budget = performanceService.getSalaryBudget(id);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }

    /**
     * Deletes a salary budget by ID.
     */
    @DeleteMapping("/salary-budgets/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSalaryBudget(@PathVariable Long id) {
        performanceService.deleteSalaryBudget(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ==================== Incentives ====================

    /**
     * Creates a new incentive record.
     */
    @PostMapping("/incentives")
    public ResponseEntity<ApiResponse<PerfIncentive>> createIncentive(
            @Valid @RequestBody IncentiveDTO dto) {
        PerfIncentive incentive = performanceService.createIncentive(dto);
        return ResponseEntity.ok(ApiResponse.success(incentive));
    }

    /**
     * Queries incentives with pagination.
     */
    @GetMapping("/incentives")
    public ResponseEntity<ApiResponse<List<PerfIncentive>>> queryIncentives(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String incentiveType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<PerfIncentive> result = performanceService.queryIncentives(tenantId, incentiveType, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Gets a single incentive by ID.
     */
    @GetMapping("/incentives/{id}")
    public ResponseEntity<ApiResponse<PerfIncentive>> getIncentive(@PathVariable Long id) {
        PerfIncentive incentive = performanceService.getIncentive(id);
        return ResponseEntity.ok(ApiResponse.success(incentive));
    }

    /**
     * Deletes an incentive by ID.
     */
    @DeleteMapping("/incentives/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIncentive(@PathVariable Long id) {
        performanceService.deleteIncentive(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
