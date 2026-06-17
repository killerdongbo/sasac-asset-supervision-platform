package com.sasac.platform.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.finance.dto.BudgetDTO;
import com.sasac.platform.finance.dto.FundMonitorDTO;
import com.sasac.platform.finance.dto.IndicatorDTO;
import com.sasac.platform.finance.dto.ReportDTO;
import com.sasac.platform.finance.entity.FinBudget;
import com.sasac.platform.finance.entity.FinFundMonitor;
import com.sasac.platform.finance.entity.FinIndicator;
import com.sasac.platform.finance.entity.FinReport;
import com.sasac.platform.finance.service.FinFinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for financial supervision.
 */
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinFinanceController {

    private final FinFinanceService financeService;

    /**
     * Submits a financial report.
     */
    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<FinReport>> submitReport(@Valid @RequestBody ReportDTO dto) {
        FinReport created = financeService.submitReport(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries financial reports with filters and pagination.
     */
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<FinReport>>> queryReports(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) Integer reportYear,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<FinReport> result = financeService.queryReports(tenantId, reportType, reportYear, status, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Gets a single financial report by ID.
     */
    @GetMapping("/reports/{id}")
    public ResponseEntity<ApiResponse<FinReport>> getReport(@PathVariable Long id) {
        FinReport report = financeService.getReport(id);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Calculates financial indicators for a given period.
     */
    @PostMapping("/indicators/calculate")
    public ResponseEntity<ApiResponse<List<FinIndicator>>> calculateIndicators(@Valid @RequestBody IndicatorDTO dto) {
        List<FinIndicator> indicators = financeService.calculateIndicators(dto);
        return ResponseEntity.ok(ApiResponse.success(indicators));
    }

    /**
     * Queries financial indicators with filters and pagination.
     */
    @GetMapping("/indicators")
    public ResponseEntity<ApiResponse<List<FinIndicator>>> queryIndicators(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String indicatorCode,
            @RequestParam(required = false) Integer periodYear,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<FinIndicator> result = financeService.queryIndicators(tenantId, indicatorCode, periodYear, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Records a fund transaction with auto-flagging.
     */
    @PostMapping("/funds")
    public ResponseEntity<ApiResponse<FinFundMonitor>> monitorFund(@Valid @RequestBody FundMonitorDTO dto) {
        FinFundMonitor created = financeService.monitorFund(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries fund transactions with optional flagged filter.
     */
    @GetMapping("/funds")
    public ResponseEntity<ApiResponse<List<FinFundMonitor>>> queryFunds(
            @RequestParam Long tenantId,
            @RequestParam(required = false) Boolean isFlagged,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<FinFundMonitor> result = financeService.queryFunds(tenantId, isFlagged, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Queries budgets with optional year filter.
     */
    @GetMapping("/budgets")
    public ResponseEntity<ApiResponse<List<FinBudget>>> queryBudgets(
            @RequestParam Long tenantId,
            @RequestParam(required = false) Integer budgetYear,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<FinBudget> result = financeService.queryBudgets(tenantId, budgetYear, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Creates a budget entry.
     */
    @PostMapping("/budgets")
    public ResponseEntity<ApiResponse<FinBudget>> createBudget(@Valid @RequestBody BudgetDTO dto) {
        FinBudget created = financeService.createBudget(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Checks budget execution for a given year.
     */
    @PostMapping("/budgets/check")
    public ResponseEntity<ApiResponse<List<FinBudget>>> checkBudgetExecution(
            @RequestParam Long tenantId,
            @RequestParam int budgetYear) {
        List<FinBudget> budgets = financeService.checkBudgetExecution(tenantId, budgetYear);
        return ResponseEntity.ok(ApiResponse.success(budgets));
    }
}
