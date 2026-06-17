package com.sasac.platform.hr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.hr.dto.EmployeeChangeDTO;
import com.sasac.platform.hr.entity.HrEmployeeChange;
import com.sasac.platform.hr.service.HrEmployeeChangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for HR employee change record operations.
 */
@RestController
@RequestMapping("/api/hr/changes")
@RequiredArgsConstructor
public class HrEmployeeChangeController {

    private final HrEmployeeChangeService hrEmployeeChangeService;

    /**
     * Creates a new employee change record.
     *
     * @param dto the change creation data
     * @return API response with the created change record
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HrEmployeeChange>> create(@Valid @RequestBody EmployeeChangeDTO dto) {
        HrEmployeeChange created = hrEmployeeChangeService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries employee change records with filters and pagination.
     *
     * @param employeeId optional employee ID filter
     * @param tenantId   tenant ID filter
     * @param page       page number (1-based, default 1)
     * @param limit      page size (default 20)
     * @return API response with the list of change records and pagination meta
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrEmployeeChange>>> query(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        Page<HrEmployeeChange> result = hrEmployeeChangeService.query(employeeId, tenantId, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }
}
