package com.sasac.platform.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.property.dto.AssessmentDTO;
import com.sasac.platform.property.dto.ChangeDTO;
import com.sasac.platform.property.dto.RegistrationDTO;
import com.sasac.platform.property.dto.TransactionDTO;
import com.sasac.platform.property.entity.PrAssessment;
import com.sasac.platform.property.entity.PrChange;
import com.sasac.platform.property.entity.PrRegistration;
import com.sasac.platform.property.entity.PrTransactionMonitor;
import com.sasac.platform.property.service.PrPropertyService;
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
import java.util.Map;

/**
 * REST controller for property rights management.
 */
@RestController
@RequestMapping("/api/property")
@RequiredArgsConstructor
public class PrPropertyController {

    private final PrPropertyService prPropertyService;

    /**
     * Registers a new property right.
     *
     * @param dto the registration data
     * @return API response with the created registration
     */
    @PostMapping("/registrations")
    public ResponseEntity<ApiResponse<PrRegistration>> register(@Valid @RequestBody RegistrationDTO dto) {
        PrRegistration created = prPropertyService.register(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries property registrations with filters and pagination.
     *
     * @param tenantId the tenant ID
     * @param regType  the registration type filter
     * @param status   the status filter
     * @param keyword  the search keyword
     * @param page     the page number
     * @param limit    the page size
     * @return API response with paginated registrations
     */
    @GetMapping("/registrations")
    public ResponseEntity<ApiResponse<List<PrRegistration>>> queryRegistrations(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String regType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        Page<PrRegistration> result = prPropertyService.queryRegistrations(tenantId, regType, status, keyword, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Retrieves a registration by ID.
     *
     * @param id the registration ID
     * @return API response with the registration
     */
    @GetMapping("/registrations/{id}")
    public ResponseEntity<ApiResponse<PrRegistration>> getRegistration(@PathVariable Long id) {
        PrRegistration registration = prPropertyService.getRegistrationById(id);
        return ResponseEntity.ok(ApiResponse.success(registration));
    }

    /**
     * Records a property right change for a registration.
     *
     * @param id  the registration ID
     * @param dto the change data
     * @return API response with the created change record
     */
    @PostMapping("/registrations/{id}/changes")
    public ResponseEntity<ApiResponse<PrChange>> recordChange(@PathVariable Long id,
                                                              @Valid @RequestBody ChangeDTO dto) {
        PrChange created = prPropertyService.recordChange(id, dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Creates a new assessment filing.
     *
     * @param dto the assessment data
     * @return API response with the created assessment
     */
    @PostMapping("/assessments")
    public ResponseEntity<ApiResponse<PrAssessment>> assess(@Valid @RequestBody AssessmentDTO dto) {
        PrAssessment created = prPropertyService.assess(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries assessments with pagination.
     *
     * @param tenantId the tenant ID
     * @param page     the page number
     * @param limit    the page size
     * @return API response with paginated assessments
     */
    @GetMapping("/assessments")
    public ResponseEntity<ApiResponse<List<PrAssessment>>> queryAssessments(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        Page<PrAssessment> result = prPropertyService.queryAssessments(tenantId, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Records a transaction monitoring entry.
     *
     * @param dto the transaction data
     * @return API response with the created transaction record
     */
    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<PrTransactionMonitor>> monitorTransaction(
            @Valid @RequestBody TransactionDTO dto) {
        PrTransactionMonitor created = prPropertyService.monitorTransaction(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries transaction monitors with optional abnormal filter.
     *
     * @param tenantId   the tenant ID
     * @param isAbnormal filter for abnormal transactions only
     * @param page       the page number
     * @param limit      the page size
     * @return API response with paginated transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<PrTransactionMonitor>>> queryTransactions(
            @RequestParam Long tenantId,
            @RequestParam(required = false) Boolean isAbnormal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        Page<PrTransactionMonitor> result = prPropertyService.queryTransactions(tenantId, isAbnormal, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Builds the property tree showing enterprise ownership hierarchy.
     *
     * @param tenantId the tenant ID
     * @return API response with the tree structure
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> buildPropertyTree(
            @RequestParam Long tenantId) {
        List<Map<String, Object>> tree = prPropertyService.buildPropertyTree(tenantId);
        return ResponseEntity.ok(ApiResponse.success(tree));
    }
}
