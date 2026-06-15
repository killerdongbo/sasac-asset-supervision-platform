package com.sasac.platform.supervision.approval.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.supervision.approval.dto.*;
import com.sasac.platform.supervision.approval.entity.*;
import com.sasac.platform.supervision.approval.service.ApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @GetMapping("/approval-defs")
    public ResponseEntity<ApiResponse<List<ApprovalDef>>> getDefs() {
        return ResponseEntity.ok(ApiResponse.success(approvalService.getAllDefs()));
    }

    /**
     * Creates a new approval definition.
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
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String roleCode) {
        List<ApprovalInstance> instances = approvalService.getPendingApprovals(tenantId, roleCode);
        return ResponseEntity.ok(ApiResponse.success(instances));
    }

    /**
     * Retrieves approval instances submitted by a specific user.
     */
    @GetMapping("/approval-instances/my-requests")
    public ResponseEntity<ApiResponse<List<ApprovalInstance>>> getMyRequests(
            @RequestParam Long submitterId) {
        return ResponseEntity.ok(ApiResponse.success(approvalService.getMyRequests(submitterId)));
    }

    /** Get instance detail */
    @GetMapping("/approval-instances/{id}")
    public ResponseEntity<ApiResponse<ApprovalInstance>> getInstance(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(approvalService.getInstance(id)));
    }

    /** Get node list for a definition */
    @GetMapping("/approval-defs/{defId}/nodes")
    public ResponseEntity<ApiResponse<List<ApprovalNode>>> getNodes(@PathVariable Long defId) {
        return ResponseEntity.ok(ApiResponse.success(approvalService.getNodesByDefId(defId)));
    }

    /** Update approval node */
    @PutMapping("/approval-defs/{defId}/nodes/{nodeId}")
    public ResponseEntity<ApiResponse<ApprovalNode>> updateNode(
            @PathVariable Long defId, @PathVariable Long nodeId, @RequestBody ApprovalNode node) {
        return ResponseEntity.ok(ApiResponse.success(approvalService.updateNode(nodeId, node)));
    }

    /** Delete approval node */
    @DeleteMapping("/approval-defs/{defId}/nodes/{nodeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNode(
            @PathVariable Long defId, @PathVariable Long nodeId) {
        approvalService.deleteNode(nodeId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /** Get def detail */
    @GetMapping("/approval-defs/{defId}")
    public ResponseEntity<ApiResponse<ApprovalDef>> getDef(@PathVariable Long defId) {
        return ResponseEntity.ok(ApiResponse.success(approvalService.getDef(defId)));
    }

    // ---- 加签 (Add Sign) ----

    @PostMapping("/approval-instances/{id}/add-sign")
    public ResponseEntity<ApiResponse<Void>> addSign(@PathVariable Long id,
                                                      @RequestBody Map<String, Object> body) {
        approvalService.addSign(
                id,
                Long.parseLong(body.get("approverId").toString()),
                Long.parseLong(body.get("addSignUserId").toString()),
                (String) body.get("addSignUserName"),
                (String) body.get("reason"));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/approval-add-signs/{addSignId}/approve")
    public ResponseEntity<ApiResponse<Void>> addSignApprove(
            @PathVariable Long addSignId, @RequestBody Map<String, Object> body) {
        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        String remark = (String) body.getOrDefault("remark", "");
        approvalService.addSignApprove(addSignId, approved, remark);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/approval-add-signs/{addSignId}")
    public ResponseEntity<ApiResponse<Void>> cancelAddSign(@PathVariable Long addSignId) {
        approvalService.cancelAddSign(addSignId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/approval-instances/{id}/add-signs")
    public ResponseEntity<ApiResponse<List<ApprovalAddSign>>> getAddSigns(
            @PathVariable Long id, @RequestParam Integer nodeOrder) {
        return ResponseEntity.ok(ApiResponse.success(approvalService.getAddSigns(id, nodeOrder)));
    }
}
