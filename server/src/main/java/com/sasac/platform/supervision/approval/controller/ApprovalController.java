package com.sasac.platform.supervision.approval.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.supervision.approval.dto.ApprovalActionRequest;
import com.sasac.platform.supervision.approval.dto.ApprovalStartRequest;
import com.sasac.platform.supervision.approval.entity.ApprovalDef;
import com.sasac.platform.supervision.approval.entity.ApprovalInstance;
import com.sasac.platform.supervision.approval.entity.ApprovalNode;
import com.sasac.platform.supervision.approval.service.ApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
 * REST controller for configurable approval workflow operations.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * Creates a new approval definition.
     *
     * @param def the approval definition
     * @return API response with the created definition
     */
    @PostMapping("/approval-defs")
    public ResponseEntity<ApiResponse<ApprovalDef>> createDef(@Valid @RequestBody ApprovalDef def) {
        ApprovalDef created = approvalService.createDef(def);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Adds an approval node to an existing definition.
     *
     * @param defId the definition ID
     * @param node  the approval node
     * @return API response with the created node
     */
    @PostMapping("/approval-defs/{defId}/nodes")
    public ResponseEntity<ApiResponse<ApprovalNode>> addNode(@PathVariable Long defId,
                                                             @Valid @RequestBody ApprovalNode node) {
        node.setDefId(defId);
        ApprovalNode created = approvalService.addNode(node);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Starts a new approval instance.
     *
     * @param request the start request
     * @return API response with the created instance
     */
    @PostMapping("/approval-instances")
    public ResponseEntity<ApiResponse<ApprovalInstance>> startInstance(
            @Valid @RequestBody ApprovalStartRequest request) {
        ApprovalInstance instance = approvalService.startInstance(
                request.getDefId(), request.getBizId(),
                request.getBizType(), request.getSubmitterId());
        return ResponseEntity.ok(ApiResponse.success(instance));
    }

    /**
     * Approves or rejects an approval instance at its current node.
     *
     * @param id      the approval instance ID
     * @param request the approve request
     * @return API response indicating success
     */
    @PutMapping("/approval-instances/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long id,
                                                     @Valid @RequestBody ApprovalActionRequest request) {
        approvalService.approve(id, request.getApproverId(),
                request.getApproved(),
                request.getRemark() != null ? request.getRemark() : "");
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Retrieves pending approval instances for a given tenant and role.
     *
     * @param tenantId the tenant ID
     * @param roleCode the approver role code
     * @return API response with the list of pending instances
     */
    @GetMapping("/approval-instances/pending")
    public ResponseEntity<ApiResponse<List<ApprovalInstance>>> getPendingApprovals(
            @RequestParam Long tenantId,
            @RequestParam String roleCode) {
        List<ApprovalInstance> instances = approvalService.getPendingApprovals(tenantId, roleCode);
        return ResponseEntity.ok(ApiResponse.success(instances));
    }

    /**
     * Retrieves approval instances submitted by a specific user.
     *
     * @param submitterId the submitter user ID
     * @return API response with the list of instances
     */
    @GetMapping("/approval-instances/my-requests")
    public ResponseEntity<ApiResponse<List<ApprovalInstance>>> getMyRequests(
            @RequestParam Long submitterId) {
        List<ApprovalInstance> instances = approvalService.getMyRequests(submitterId);
        return ResponseEntity.ok(ApiResponse.success(instances));
    }
}
