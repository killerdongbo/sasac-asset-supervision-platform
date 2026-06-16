package com.sasac.platform.workflow.controller;

import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.workflow.dto.ApproveRequest;
import com.sasac.platform.workflow.dto.StartInstanceRequest;
import com.sasac.platform.workflow.entity.WorkflowDef;
import com.sasac.platform.workflow.entity.WorkflowInstance;
import com.sasac.platform.workflow.entity.WorkflowTask;
import com.sasac.platform.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the visual workflow designer.
 * <p>
 * Exposes endpoints for managing workflow definitions, starting instances,
 * processing approval tasks, and querying workflow state.
 */
@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // ========== Definition Endpoints ==========

    /**
     * List all workflow definitions for the current tenant.
     */
    @GetMapping
    public ApiResponse<List<WorkflowDef>> listDefs(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "1") Long tenantId) {
        return ApiResponse.success(workflowService.listDefs(tenantId));
    }

    /**
     * Create a new workflow definition.
     */
    @PostMapping
    public ApiResponse<WorkflowDef> createDef(@RequestBody WorkflowDef def) {
        return ApiResponse.success(workflowService.createDef(def));
    }

    /**
     * Get a single workflow definition by ID.
     */
    @GetMapping("/{id}")
    public ApiResponse<WorkflowDef> getDef(@PathVariable Long id) {
        WorkflowDef def = workflowService.getDef(id);
        if (def == null) {
            throw new BusinessException("流程定义不存在");
        }
        return ApiResponse.success(def);
    }

    /**
     * Update an existing workflow definition.
     */
    @PutMapping("/{id}")
    public ApiResponse<WorkflowDef> updateDef(@PathVariable Long id, @RequestBody WorkflowDef update) {
        return ApiResponse.success(workflowService.updateDef(id, update));
    }

    /**
     * Delete a workflow definition by ID.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDef(@PathVariable Long id) {
        workflowService.deleteDef(id);
        return ApiResponse.success(null);
    }

    // ========== Instance Endpoints ==========

    /**
     * Start a new workflow instance from a given definition.
     */
    @PostMapping("/{id}/start")
    public ApiResponse<WorkflowInstance> startInstance(
            @PathVariable Long id,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "1") Long tenantId,
            @Valid @RequestBody StartInstanceRequest req) {
        Long submitterId = 0L;
        return ApiResponse.success(
            workflowService.startInstance(id, submitterId, tenantId,
                req.getBizId(), req.getBizType(), req.getContextJson()));
    }

    /**
     * Get a single workflow instance by ID.
     */
    @GetMapping("/instances/{id}")
    public ApiResponse<WorkflowInstance> getInstance(@PathVariable Long id) {
        return ApiResponse.success(workflowService.getInstance(id));
    }

    // ========== Task Endpoints ==========

    /**
     * Approve or reject a workflow task.
     */
    @PutMapping("/tasks/{taskId}/approve")
    public ApiResponse<Void> approveTask(@PathVariable Long taskId,
                                          @Valid @RequestBody ApproveRequest req) {
        workflowService.approveTask(taskId, req.getApproverId(), req.getApproved(), req.getRemark());
        return ApiResponse.success(null);
    }

    /**
     * Get all pending tasks, optionally filtered by role code.
     */
    @GetMapping("/tasks/pending")
    public ApiResponse<List<WorkflowTask>> pendingTasks(
            @RequestParam(defaultValue = "") String roleCode) {
        return ApiResponse.success(workflowService.getPendingTasks(1L, roleCode));
    }

    /**
     * Get all workflow instances submitted by a given user.
     */
    @GetMapping("/tasks/my-requests")
    public ApiResponse<List<WorkflowInstance>> myRequests(
            @RequestParam(defaultValue = "0") Long submitterId) {
        return ApiResponse.success(workflowService.getMyRequests(submitterId));
    }
}
