package com.sasac.platform.hr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.hr.entity.HrRecruitment;
import com.sasac.platform.hr.service.HrRecruitmentService;
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
 * REST controller for recruitment management (CRUD).
 */
@RestController
@RequestMapping("/api/hr/recruitments")
@RequiredArgsConstructor
public class HrRecruitmentController {

    private final HrRecruitmentService hrRecruitmentService;

    /**
     * Creates a new recruitment record.
     *
     * @param recruitment the recruitment data
     * @return API response with the created record
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HrRecruitment>> create(@Valid @RequestBody HrRecruitment recruitment) {
        HrRecruitment created = hrRecruitmentService.create(recruitment);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries recruitment records with pagination.
     *
     * @param page     page number
     * @param limit    page size
     * @param tenantId tenant ID (optional)
     * @return API response with recruitment records
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrRecruitment>>> query(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Long tenantId) {
        Page<HrRecruitment> result = hrRecruitmentService.query(page, limit, tenantId);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Retrieves a recruitment record by ID.
     *
     * @param id the record ID
     * @return API response with the recruitment record
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HrRecruitment>> getById(@PathVariable Long id) {
        HrRecruitment recruitment = hrRecruitmentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(recruitment));
    }

    /**
     * Updates a recruitment record.
     *
     * @param id          the record ID to update
     * @param recruitment the updated fields
     * @return API response with the updated record
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HrRecruitment>> update(@PathVariable Long id,
                                                              @Valid @RequestBody HrRecruitment recruitment) {
        HrRecruitment updated = hrRecruitmentService.update(id, recruitment);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Deletes a recruitment record.
     *
     * @param id the record ID to delete
     * @return API response indicating success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hrRecruitmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
