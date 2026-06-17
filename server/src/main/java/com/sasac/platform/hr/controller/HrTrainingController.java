package com.sasac.platform.hr.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.hr.entity.HrTraining;
import com.sasac.platform.hr.service.HrTrainingService;
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
 * REST controller for training management (CRUD).
 */
@RestController
@RequestMapping("/api/hr/trainings")
@RequiredArgsConstructor
public class HrTrainingController {

    private final HrTrainingService hrTrainingService;

    /**
     * Creates a new training record.
     *
     * @param training the training data
     * @return API response with the created record
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HrTraining>> create(@Valid @RequestBody HrTraining training) {
        HrTraining created = hrTrainingService.create(training);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries training records with pagination.
     *
     * @param page     page number
     * @param limit    page size
     * @param tenantId tenant ID (optional)
     * @return API response with training records
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrTraining>>> query(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Long tenantId) {
        Page<HrTraining> result = hrTrainingService.query(page, limit, tenantId);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Retrieves a training record by ID.
     *
     * @param id the record ID
     * @return API response with the training record
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HrTraining>> getById(@PathVariable Long id) {
        HrTraining training = hrTrainingService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(training));
    }

    /**
     * Updates a training record.
     *
     * @param id       the record ID to update
     * @param training the updated fields
     * @return API response with the updated record
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HrTraining>> update(@PathVariable Long id,
                                                           @Valid @RequestBody HrTraining training) {
        HrTraining updated = hrTrainingService.update(id, training);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Deletes a training record.
     *
     * @param id the record ID to delete
     * @return API response indicating success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hrTrainingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
