package com.sasac.platform.report.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.report.entity.Report;
import com.sasac.platform.report.service.ReportService;
import com.sasac.platform.report.service.ReportSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for report generation and retrieval.
 * <p>
 * Provides endpoints to generate asset snapshot reports, retrieve
 * parsed report data, access raw report entities, and list reports
 * by organization.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportSubmissionService submissionService;

    /**
     * Generates a new asset snapshot report.
     *
     * @param reportType the type of report
     * @param orgId      the organization ID
     * @param period     the reporting period
     * @param tenantId   the tenant ID
     * @return the created report
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Report>> generate(
            @RequestParam String reportType,
            @RequestParam Long orgId,
            @RequestParam String period,
            @RequestParam Long tenantId) {
        Report report = reportService.generate(reportType, orgId, period, tenantId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Retrieves a report with its parsed snapshot data.
     *
     * @param id the report ID
     * @return the parsed report data
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReportData(@PathVariable Long id) {
        Map<String, Object> data = reportService.getReportData(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Retrieves the raw report entity.
     *
     * @param id the report ID
     * @return the raw report entity
     */
    @GetMapping("/{id}/raw")
    public ResponseEntity<ApiResponse<Report>> getRawReport(@PathVariable Long id) {
        Report report = reportService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Lists all reports for a given organization.
     *
     * @param orgId the organization ID
     * @return list of reports ordered by creation time descending
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Report>>> listByOrg(@RequestParam Long orgId) {
        List<Report> reports = reportService.listByOrg(orgId);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    /**
     * Submits a report for review.
     *
     * @param id         the report ID
     * @param operatorId the ID of the submitting operator
     * @return the updated report with SUBMITTED status
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<Report>> submit(@PathVariable Long id, @RequestParam Long operatorId) {
        Report report = submissionService.submit(id, operatorId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Reviews a submitted report.
     *
     * @param id         the report ID
     * @param approved   whether the report is approved
     * @param remark     optional review remarks
     * @param reviewerId the ID of the reviewer
     * @return the updated report with REVIEWED or REJECTED status
     */
    @PostMapping("/{id}/review")
    public ResponseEntity<ApiResponse<Report>> review(@PathVariable Long id,
                                                      @RequestParam boolean approved,
                                                      @RequestParam(required = false) String remark,
                                                      @RequestParam Long reviewerId) {
        Report report = submissionService.review(id, approved, remark, reviewerId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    /**
     * Accepts a reviewed report.
     *
     * @param id the report ID
     * @return the updated report with ACCEPTED status
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<Report>> accept(@PathVariable Long id) {
        Report report = submissionService.accept(id);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
