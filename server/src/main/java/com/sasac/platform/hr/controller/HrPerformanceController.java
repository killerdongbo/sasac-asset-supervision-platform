package com.sasac.platform.hr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.hr.dto.PerformanceScoreDTO;
import com.sasac.platform.hr.entity.HrPerformance;
import com.sasac.platform.hr.service.HrPerformanceService;
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
 * REST controller for performance review management.
 */
@RestController
@RequestMapping("/api/hr/performances")
@RequiredArgsConstructor
public class HrPerformanceController {

    private final HrPerformanceService hrPerformanceService;

    /**
     * Creates a new performance review record.
     *
     * @param perf the performance review data
     * @return API response with the created record
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HrPerformance>> create(@Valid @RequestBody HrPerformance perf) {
        HrPerformance created = hrPerformanceService.create(perf);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Scores a performance review and calculates grade.
     *
     * @param id  the performance record ID
     * @param dto the score data
     * @return API response with the updated record
     */
    @PostMapping("/{id}/score")
    public ResponseEntity<ApiResponse<HrPerformance>> score(@PathVariable Long id,
                                                             @Valid @RequestBody PerformanceScoreDTO dto) {
        HrPerformance updated = hrPerformanceService.score(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Confirms a performance record.
     *
     * @param id the performance record ID
     * @return API response with the confirmed record
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<HrPerformance>> confirm(@PathVariable Long id) {
        HrPerformance updated = hrPerformanceService.confirm(id);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Queries performance records with pagination.
     *
     * @param page       page number
     * @param limit      page size
     * @param tenantId   tenant ID (optional)
     * @param employeeId employee ID (optional)
     * @return API response with performance records
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrPerformance>>> query(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long employeeId) {
        Page<HrPerformance> result = hrPerformanceService.query(page, limit, tenantId, employeeId);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }
}
