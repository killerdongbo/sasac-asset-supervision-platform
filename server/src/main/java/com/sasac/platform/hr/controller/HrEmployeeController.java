package com.sasac.platform.hr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.hr.dto.EmployeeCreateDTO;
import com.sasac.platform.hr.dto.EmployeeQueryDTO;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.service.HrEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for HR employee CRUD operations.
 */
@RestController
@RequestMapping("/api/hr/employees")
@RequiredArgsConstructor
public class HrEmployeeController {

    private final HrEmployeeService hrEmployeeService;

    /**
     * Creates a new employee.
     *
     * @param dto the employee creation data
     * @return API response with the created employee
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HrEmployee>> create(@Valid @RequestBody EmployeeCreateDTO dto) {
        HrEmployee created = hrEmployeeService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Retrieves an employee by ID.
     *
     * @param id the employee ID
     * @return API response with the employee
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HrEmployee>> getById(@PathVariable Long id) {
        HrEmployee employee = hrEmployeeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(employee));
    }

    /**
     * Queries employees with filters and pagination.
     *
     * @param query the query parameters
     * @return API response with the list of employees
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrEmployee>>> query(@ModelAttribute EmployeeQueryDTO query) {
        Page<HrEmployee> page = hrEmployeeService.query(query);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(page.getTotal())
                .page((int) page.getCurrent())
                .limit((int) page.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(page.getRecords(), meta));
    }

    /**
     * Updates an existing employee.
     *
     * @param id     the employee ID to update
     * @param update the updated employee fields
     * @return API response with the updated employee
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HrEmployee>> update(@PathVariable Long id,
                                                          @RequestBody HrEmployee update) {
        HrEmployee updated = hrEmployeeService.update(id, update);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Deletes an employee by ID.
     *
     * @param id the employee ID to delete
     * @return API response indicating success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hrEmployeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
