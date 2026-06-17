package com.sasac.platform.hr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.hr.dto.SalaryCalculateDTO;
import com.sasac.platform.hr.dto.SalaryQueryDTO;
import com.sasac.platform.hr.entity.HrSalary;
import com.sasac.platform.hr.service.HrSalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for salary calculation and management.
 */
@RestController
@RequestMapping("/api/hr/salaries")
@RequiredArgsConstructor
public class HrSalaryController {

    private final HrSalaryService hrSalaryService;

    /**
     * Calculates monthly salary for an employee.
     *
     * @param dto the calculation parameters
     * @return API response with the calculated salary
     */
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<HrSalary>> calculate(@Valid @RequestBody SalaryCalculateDTO dto) {
        HrSalary salary = hrSalaryService.calculateMonthlySalary(dto);
        return ResponseEntity.ok(ApiResponse.success(salary));
    }

    /**
     * Queries salary records with filters and pagination.
     *
     * @param query the query parameters
     * @return API response with the list of salary records
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrSalary>>> query(@ModelAttribute SalaryQueryDTO query) {
        Page<HrSalary> page = hrSalaryService.queryByEmployee(query);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(page.getTotal())
                .page((int) page.getCurrent())
                .limit((int) page.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(page.getRecords(), meta));
    }

    /**
     * Confirms a salary record.
     *
     * @param id the salary record ID
     * @return API response with the confirmed salary
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<HrSalary>> confirm(@PathVariable Long id) {
        HrSalary salary = hrSalaryService.confirm(id);
        return ResponseEntity.ok(ApiResponse.success(salary));
    }
}
