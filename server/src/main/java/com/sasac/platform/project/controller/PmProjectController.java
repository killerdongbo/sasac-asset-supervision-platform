package com.sasac.platform.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.project.dto.AcceptanceDTO;
import com.sasac.platform.project.dto.ProjectCreateDTO;
import com.sasac.platform.project.dto.ProjectQueryDTO;
import com.sasac.platform.project.dto.ProgressRecordDTO;
import com.sasac.platform.project.entity.PmAcceptance;
import com.sasac.platform.project.entity.PmProject;
import com.sasac.platform.project.entity.PmProgress;
import com.sasac.platform.project.service.PmProjectService;
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
 * REST controller for project management CRUD and lifecycle operations.
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class PmProjectController {

    private final PmProjectService pmProjectService;

    /**
     * Creates a new project.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PmProject>> create(@Valid @RequestBody ProjectCreateDTO dto) {
        PmProject created = pmProjectService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries projects with filters and pagination.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PmProject>>> query(@ModelAttribute ProjectQueryDTO query) {
        Page<PmProject> page = pmProjectService.query(query);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(page.getTotal())
                .page((int) page.getCurrent())
                .limit((int) page.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(page.getRecords(), meta));
    }

    /**
     * Retrieves a project by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PmProject>> getById(@PathVariable Long id) {
        PmProject project = pmProjectService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    /**
     * Updates a project.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PmProject>> update(@PathVariable Long id,
                                                         @RequestBody PmProject update) {
        PmProject updated = pmProjectService.update(id, update);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Soft-deletes a project by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        pmProjectService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Records a progress report for a project.
     */
    @PostMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<PmProgress>> recordProgress(@PathVariable Long id,
                                                                   @Valid @RequestBody ProgressRecordDTO dto) {
        dto.setProjectId(id);
        PmProgress progress = pmProjectService.recordProgress(dto);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    /**
     * Retrieves progress history for a project.
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<List<PmProgress>>> getProgressHistory(@PathVariable Long id,
                                                                             @ModelAttribute ProgressRecordDTO dto) {
        List<PmProgress> history = pmProjectService.getProgressHistory(id, dto.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * Accepts (completes) a project with an acceptance record.
     */
    @PostMapping("/{id}/acceptance")
    public ResponseEntity<ApiResponse<Void>> acceptProject(@PathVariable Long id,
                                                           @Valid @RequestBody AcceptanceDTO dto) {
        pmProjectService.acceptProject(id, dto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Retrieves acceptance record for a project.
     */
    @GetMapping("/{id}/acceptance")
    public ResponseEntity<ApiResponse<List<PmAcceptance>>> getAcceptance(@PathVariable Long id) {
        List<PmAcceptance> records = pmProjectService.getAcceptanceByProjectId(id);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Retrieves budget records for a project.
     */
    @GetMapping("/{id}/budgets")
    public ResponseEntity<ApiResponse<List<com.sasac.platform.project.entity.PmBudget>>> getBudgets(@PathVariable Long id) {
        List<com.sasac.platform.project.entity.PmBudget> records = pmProjectService.getBudgetsByProjectId(id);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
