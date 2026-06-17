package com.sasac.platform.hr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.hr.dto.ContractCreateDTO;
import com.sasac.platform.hr.entity.HrContract;
import com.sasac.platform.hr.service.HrContractService;
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

import java.util.List;

/**
 * REST controller for HR contract CRUD operations.
 */
@RestController
@RequestMapping("/api/hr/contracts")
@RequiredArgsConstructor
public class HrContractController {

    private final HrContractService hrContractService;

    /**
     * Creates a new contract.
     *
     * @param dto the contract creation data
     * @return API response with the created contract
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HrContract>> create(@Valid @RequestBody ContractCreateDTO dto) {
        HrContract created = hrContractService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Retrieves a contract by ID.
     *
     * @param id the contract ID
     * @return API response with the contract
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HrContract>> getById(@PathVariable Long id) {
        HrContract contract = hrContractService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(contract));
    }

    /**
     * Queries contracts with filters and pagination.
     *
     * @param employeeId optional employee ID filter
     * @param tenantId   tenant ID filter
     * @param page       page number (1-based, default 1)
     * @param limit      page size (default 20)
     * @return API response with the list of contracts and pagination meta
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrContract>>> query(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        Page<HrContract> result = hrContractService.query(employeeId, tenantId, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Retrieves contracts expiring within the specified number of days.
     *
     * @param tenantId tenant ID filter
     * @param days     number of days from now (default 30)
     * @return API response with the list of expiring contracts
     */
    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<HrContract>>> getExpiring(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "30") int days) {
        List<HrContract> contracts = hrContractService.findExpiring(tenantId, days);
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }

    /**
     * Updates an existing contract.
     *
     * @param id     the contract ID to update
     * @param update the updated contract fields
     * @return API response with the updated contract
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HrContract>> update(@PathVariable Long id,
                                                          @RequestBody HrContract update) {
        HrContract updated = hrContractService.update(id, update);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Deletes a contract by ID.
     *
     * @param id the contract ID to delete
     * @return API response indicating success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hrContractService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
