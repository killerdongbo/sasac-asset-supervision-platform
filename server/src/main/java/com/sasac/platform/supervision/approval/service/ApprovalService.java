package com.sasac.platform.supervision.approval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.supervision.approval.entity.ApprovalDef;
import com.sasac.platform.supervision.approval.entity.ApprovalInstance;
import com.sasac.platform.supervision.approval.entity.ApprovalNode;
import com.sasac.platform.supervision.approval.mapper.ApprovalDefMapper;
import com.sasac.platform.supervision.approval.mapper.ApprovalInstanceMapper;
import com.sasac.platform.supervision.approval.mapper.ApprovalNodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing configurable approval workflows.
 * <p>
 * Supports creating approval definitions with ordered nodes, starting
 * approval instances, advancing through nodes on approval, and querying
 * pending and submitted approvals.
 */
@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalDefMapper approvalDefMapper;
    private final ApprovalNodeMapper approvalNodeMapper;
    private final ApprovalInstanceMapper approvalInstanceMapper;
    private final ObjectMapper objectMapper;

    // ---- Def & Node Management ----

    /**
     * Creates a new approval definition.
     *
     * @param def the approval definition to create
     * @return the saved definition with generated ID
     */
    public List<ApprovalDef> getAllDefs() {
        return approvalDefMapper.selectList(null);
    }

    @Transactional
    public ApprovalDef createDef(ApprovalDef def) {
        def.setCreatedAt(LocalDateTime.now());
        def.setUpdatedAt(LocalDateTime.now());
        approvalDefMapper.insert(def);
        return def;
    }

    /**
     * Adds an approval node to an existing definition.
     *
     * @param node the approval node to add
     * @return the saved node with generated ID
     * @throws BusinessException if the definition does not exist
     */
    @Transactional
    public ApprovalNode addNode(ApprovalNode node) {
        ApprovalDef def = approvalDefMapper.selectById(node.getDefId());
        if (def == null) {
            throw new BusinessException("审批定义不存在");
        }
        node.setCreatedAt(LocalDateTime.now());
        node.setUpdatedAt(LocalDateTime.now());
        approvalNodeMapper.insert(node);
        return node;
    }

    // ---- Instance Lifecycle ----

    /**
     * Starts a new approval instance for the given definition and business record.
     *
     * @param defId      the approval definition ID
     * @param bizId      the business record ID
     * @param bizType    the business type
     * @param submitterId the user ID submitting the request
     * @return the created approval instance
     * @throws BusinessException if the definition does not exist
     */
    @Transactional
    public ApprovalInstance startInstance(Long defId, Long bizId, String bizType, Long submitterId) {
        ApprovalDef def = approvalDefMapper.selectById(defId);
        if (def == null) {
            throw new BusinessException("审批定义不存在");
        }

        List<ApprovalNode> nodes = approvalNodeMapper.selectByDefId(defId);
        if (nodes.isEmpty()) {
            throw new BusinessException("审批定义未配置审批节点");
        }

        ApprovalInstance instance = new ApprovalInstance();
        instance.setDefId(defId);
        instance.setBizId(bizId);
        instance.setBizType(bizType);
        instance.setStatus("PENDING");
        instance.setCurrentNode(1);
        instance.setNodeResults("[]");
        instance.setSubmitterId(submitterId);
        instance.setTenantId(def.getTenantId());
        instance.setCreatedAt(LocalDateTime.now());
        instance.setUpdatedAt(LocalDateTime.now());
        approvalInstanceMapper.insert(instance);
        return instance;
    }

    /**
     * Approves or rejects the current node of an approval instance.
     * <p>
     * If rejected, the instance status is set to REJECTED immediately.
     * If approved and more nodes remain, advances currentNode by 1.
     * If approved and this was the last node, sets status to APPROVED.
     *
     * @param instanceId the approval instance ID
     * @param approverId the user ID of the approver
     * @param approved   true for approve, false for reject
     * @param remark     optional approval remark
     * @throws BusinessException if the instance does not exist or is not pending
     */
    @Transactional
    public void approve(Long instanceId, Long approverId, boolean approved, String remark) {
        ApprovalInstance instance = approvalInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException("审批实例不存在");
        }
        if (!"PENDING".equals(instance.getStatus())) {
            throw new BusinessException("审批实例状态不允许操作");
        }

        List<ApprovalNode> nodes = approvalNodeMapper.selectByDefId(instance.getDefId());
        int currentOrder = instance.getCurrentNode();

        // Find the current node
        ApprovalNode currentNode = nodes.stream()
                .filter(n -> n.getNodeOrder().equals(currentOrder))
                .findFirst()
                .orElseThrow(() -> new BusinessException("当前审批节点不存在"));

        // Record the node result
        String updatedNodeResults = appendNodeResult(
                instance.getNodeResults(),
                currentNode.getNodeOrder(),
                currentNode.getApproverRole(),
                approverId,
                approved,
                remark
        );
        instance.setNodeResults(updatedNodeResults);

        if (!approved) {
            // Rejection: mark instance as REJECTED
            instance.setStatus("REJECTED");
            instance.setUpdatedAt(LocalDateTime.now());
            approvalInstanceMapper.updateById(instance);
            return;
        }

        // Approval: check if there is a next node
        int nextOrder = currentOrder + 1;
        boolean hasNextNode = nodes.stream().anyMatch(n -> n.getNodeOrder().equals(nextOrder));

        if (hasNextNode) {
            instance.setCurrentNode(nextOrder);
            instance.setStatus("PENDING");
        } else {
            // Last node approved
            instance.setStatus("APPROVED");
        }

        instance.setUpdatedAt(LocalDateTime.now());
        approvalInstanceMapper.updateById(instance);
    }

    // ---- Queries ----

    /**
     * Retrieves all pending approval instances where the current node's
     * approver role matches the given role code, scoped to a tenant.
     * <p>
     * Uses a single SQL JOIN query for efficient retrieval instead of
     * loading all instances and filtering in memory.
     *
     * @param tenantId the tenant ID
     * @param roleCode the approver role code to filter by
     * @return list of pending approval instances
     */
    public List<ApprovalInstance> getPendingApprovals(Long tenantId, String roleCode) {
        return approvalInstanceMapper.selectPendingByRole(tenantId, roleCode);
    }

    /**
     * Retrieves all approval instances submitted by a specific user.
     *
     * @param submitterId the submitter user ID
     * @return list of approval instances submitted by the user
     */
    public List<ApprovalInstance> getMyRequests(Long submitterId) {
        return approvalInstanceMapper.selectList(
                new LambdaQueryWrapper<ApprovalInstance>()
                        .eq(ApprovalInstance::getSubmitterId, submitterId)
                        .orderByDesc(ApprovalInstance::getCreatedAt)
        );
    }

    // ---- Internal Helpers ----

    /**
     * Appends a node approval result to the existing JSON array string.
     *
     * @param existingJson the existing JSON array string (or empty array "[]")
     * @param nodeOrder    the current node order
     * @param approverRole the approver role
     * @param approverId   the approver user ID
     * @param approved     whether approved
     * @param remark       the approval remark
     * @return updated JSON array string
     */
    private String appendNodeResult(String existingJson, Integer nodeOrder,
                                    String approverRole, Long approverId,
                                    boolean approved, String remark) {
        try {
            ArrayNode array;
            if (existingJson == null || existingJson.isBlank()) {
                array = objectMapper.createArrayNode();
            } else {
                array = (ArrayNode) objectMapper.readTree(existingJson);
            }

            ObjectNode entry = objectMapper.createObjectNode();
            entry.put("nodeOrder", nodeOrder);
            entry.put("approverRole", approverRole);
            entry.put("approverId", approverId);
            entry.put("approved", approved);
            entry.put("remark", remark != null ? remark : "");
            entry.put("timestamp", LocalDateTime.now().toString());

            array.add(entry);
            return objectMapper.writeValueAsString(array);
        } catch (JsonProcessingException e) {
            throw new BusinessException("审批结果序列化失败");
        }
    }
}
