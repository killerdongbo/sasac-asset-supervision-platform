package com.sasac.platform.supervision.approval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.supervision.approval.entity.*;
import com.sasac.platform.supervision.approval.mapper.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    private final ApprovalDefMapper approvalDefMapper;
    private final ApprovalNodeMapper approvalNodeMapper;
    private final ApprovalInstanceMapper approvalInstanceMapper;
    private final ApprovalAddSignMapper addSignMapper;
    private final ApprovalTimeoutRecordMapper timeoutMapper;
    private final ObjectMapper objectMapper;

    // ---- Def & Node Management ----

    public List<ApprovalDef> getAllDefs() {
        return approvalDefMapper.selectList(null);
    }

    public ApprovalDef getDef(Long defId) {
        return approvalDefMapper.selectById(defId);
    }

    @Transactional
    public ApprovalDef createDef(ApprovalDef def) {
        def.setCreatedAt(LocalDateTime.now());
        def.setUpdatedAt(LocalDateTime.now());
        approvalDefMapper.insert(def);
        return def;
    }

    @Transactional
    public ApprovalNode addNode(ApprovalNode node) {
        ApprovalDef def = approvalDefMapper.selectById(node.getDefId());
        if (def == null) throw new BusinessException("审批定义不存在");
        node.setCreatedAt(LocalDateTime.now());
        node.setUpdatedAt(LocalDateTime.now());
        approvalNodeMapper.insert(node);
        return node;
    }

    @Transactional
    public ApprovalNode updateNode(Long nodeId, ApprovalNode update) {
        ApprovalNode node = approvalNodeMapper.selectById(nodeId);
        if (node == null) throw new BusinessException("审批节点不存在");
        node.setApproverRole(update.getApproverRole());
        node.setApproveMode(update.getApproveMode());
        node.setAllowAddSign(update.getAllowAddSign());
        node.setTimeoutHours(update.getTimeoutHours());
        node.setTimeoutAction(update.getTimeoutAction());
        node.setConditionType(update.getConditionType());
        node.setConditionValue(update.getConditionValue());
        node.setEscalateRole(update.getEscalateRole());
        node.setCanReject(update.getCanReject());
        node.setUpdatedAt(LocalDateTime.now());
        approvalNodeMapper.updateById(node);
        return node;
    }

    public List<ApprovalNode> getNodesByDefId(Long defId) {
        return approvalNodeMapper.selectByDefId(defId);
    }

    @Transactional
    public void deleteNode(Long nodeId) {
        approvalNodeMapper.deleteById(nodeId);
    }

    // ---- Instance Lifecycle ----

    @Transactional
    public ApprovalInstance startInstance(Long defId, Long bizId, String bizType, Long submitterId) {
        ApprovalDef def = approvalDefMapper.selectById(defId);
        if (def == null) throw new BusinessException("审批定义不存在");

        List<ApprovalNode> nodes = approvalNodeMapper.selectByDefId(defId);
        if (nodes.isEmpty()) throw new BusinessException("审批定义未配置审批节点");

        int startNode = evaluateStartNode(nodes, null);

        ApprovalInstance instance = new ApprovalInstance();
        instance.setDefId(defId);
        instance.setBizId(bizId);
        instance.setBizType(bizType);
        instance.setStatus("PENDING");
        instance.setCurrentNode(startNode);
        instance.setNodeResults("[]");
        instance.setSubmitterId(submitterId);
        instance.setTenantId(def.getTenantId());
        instance.setCreatedAt(LocalDateTime.now());
        instance.setUpdatedAt(LocalDateTime.now());
        approvalInstanceMapper.insert(instance);
        return instance;
    }

    /**
     * 审批（支持：单人/会签/或签模式）
     */
    @Transactional
    public void approve(Long instanceId, Long approverId, boolean approved, String remark) {
        ApprovalInstance instance = approvalInstanceMapper.selectById(instanceId);
        if (instance == null) throw new BusinessException("审批实例不存在");
        if (!"PENDING".equals(instance.getStatus())) throw new BusinessException("审批实例状态不允许操作");

        List<ApprovalNode> nodes = approvalNodeMapper.selectByDefId(instance.getDefId());
        int currentOrder = instance.getCurrentNode();

        ApprovalNode currentNode = nodes.stream()
                .filter(n -> n.getNodeOrder().equals(currentOrder))
                .findFirst()
                .orElseThrow(() -> new BusinessException("当前审批节点不存在"));

        String mode = currentNode.getApproveMode() != null ? currentNode.getApproveMode() : "SINGLE";

        // 记录节点结果
        String updatedNodeResults = appendNodeResult(instance.getNodeResults(),
                currentNode.getNodeOrder(), currentNode.getApproverRole(),
                approverId, approved, remark);
        instance.setNodeResults(updatedNodeResults);

        if (!approved) {
            instance.setStatus("REJECTED");
            instance.setUpdatedAt(LocalDateTime.now());
            approvalInstanceMapper.updateById(instance);
            return;
        }

        // 会签模式：检查是否所有人已审批
        if ("COUNTER_SIGN".equals(mode)) {
            boolean allApproved = checkCounterSignComplete(currentNode, instanceId);
            if (!allApproved) {
                instance.setUpdatedAt(LocalDateTime.now());
                approvalInstanceMapper.updateById(instance);
                return; // 等待其他审批人
            }
        }

        // 或签/单人/会签完成：推进到下一节点
        advanceToNextNode(instance, nodes, currentOrder);
    }

    /**
     * 加签：动态添加审批人到当前节点
     */
    @Transactional
    public void addSign(Long instanceId, Long approverId, Long addSignUserId,
                        String addSignUserName, String reason) {
        ApprovalInstance instance = approvalInstanceMapper.selectById(instanceId);
        if (instance == null) throw new BusinessException("审批实例不存在");
        if (!"PENDING".equals(instance.getStatus())) throw new BusinessException("审批实例状态不允许操作");

        List<ApprovalNode> nodes = approvalNodeMapper.selectByDefId(instance.getDefId());
        ApprovalNode currentNode = nodes.stream()
                .filter(n -> n.getNodeOrder().equals(instance.getCurrentNode()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("当前审批节点不存在"));

        if (!Boolean.TRUE.equals(currentNode.getAllowAddSign())) {
            throw new BusinessException("当前节点不允许加签");
        }

        ApprovalAddSign addSign = new ApprovalAddSign();
        addSign.setInstanceId(instanceId);
        addSign.setNodeOrder(instance.getCurrentNode());
        addSign.setAddSignUserId(addSignUserId);
        addSign.setAddSignUserName(addSignUserName);
        addSign.setReason(reason);
        addSignMapper.insert(addSign);
    }

    /**
     * 加签审批
     */
    @Transactional
    public void addSignApprove(Long addSignId, boolean approved, String remark) {
        ApprovalAddSign addSign = addSignMapper.selectById(addSignId);
        if (addSign == null) throw new BusinessException("加签记录不存在");
        if (addSign.getApproved() != null) throw new BusinessException("已审批过");

        addSign.setApproved(approved);
        addSign.setRemark(remark);
        addSignMapper.updateById(addSign);

        if (!approved) {
            ApprovalInstance instance = approvalInstanceMapper.selectById(addSign.getInstanceId());
            if (instance != null) {
                instance.setStatus("REJECTED");
                instance.setUpdatedAt(LocalDateTime.now());
                approvalInstanceMapper.updateById(instance);
            }
        }
    }

    /**
     * 取消加签
     */
    @Transactional
    public void cancelAddSign(Long addSignId) {
        addSignMapper.deleteById(addSignId);
    }

    // ---- Queries ----

    public List<ApprovalInstance> getPendingApprovals(Long tenantId, String roleCode) {
        return approvalInstanceMapper.selectPendingByRole(tenantId, roleCode);
    }

    public List<ApprovalInstance> getMyRequests(Long submitterId) {
        return approvalInstanceMapper.selectList(
                new LambdaQueryWrapper<ApprovalInstance>()
                        .eq(ApprovalInstance::getSubmitterId, submitterId)
                        .orderByDesc(ApprovalInstance::getCreatedAt));
    }

    public ApprovalInstance getInstance(Long instanceId) {
        return approvalInstanceMapper.selectById(instanceId);
    }

    public List<ApprovalAddSign> getAddSigns(Long instanceId, Integer nodeOrder) {
        return addSignMapper.selectList(
                new LambdaQueryWrapper<ApprovalAddSign>()
                        .eq(ApprovalAddSign::getInstanceId, instanceId)
                        .eq(ApprovalAddSign::getNodeOrder, nodeOrder));
    }

    // ---- 超时扫描器 (每30秒) ----

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void scanTimeoutInstances() {
        List<ApprovalInstance> pendingInstances = approvalInstanceMapper.selectList(
                new LambdaQueryWrapper<ApprovalInstance>().eq(ApprovalInstance::getStatus, "PENDING"));

        for (ApprovalInstance instance : pendingInstances) {
            List<ApprovalNode> nodes = approvalNodeMapper.selectByDefId(instance.getDefId());
            ApprovalNode currentNode = nodes.stream()
                    .filter(n -> n.getNodeOrder().equals(instance.getCurrentNode()))
                    .findFirst().orElse(null);
            if (currentNode == null || currentNode.getTimeoutHours() == null
                    || currentNode.getTimeoutHours() <= 0) continue;

            LocalDateTime deadline = instance.getCreatedAt().plusHours(currentNode.getTimeoutHours());
            if (LocalDateTime.now().isBefore(deadline)) continue;

            // 超时处理
            String action = currentNode.getTimeoutAction() != null
                    ? currentNode.getTimeoutAction() : "ESCALATE";
            handleTimeout(instance, currentNode, action);
        }
    }

    private void handleTimeout(ApprovalInstance instance, ApprovalNode node, String action) {
        if ("AUTO_APPROVE".equals(action)) {
            instance.setCurrentNode(instance.getCurrentNode() + 1);
            instance.setUpdatedAt(LocalDateTime.now());
            approvalInstanceMapper.updateById(instance);
            log.info("实例{}节点{}超时自动通过", instance.getId(), node.getNodeOrder());
        } else if ("AUTO_REJECT".equals(action)) {
            instance.setStatus("REJECTED");
            instance.setUpdatedAt(LocalDateTime.now());
            approvalInstanceMapper.updateById(instance);
            log.info("实例{}节点{}超时自动驳回", instance.getId(), node.getNodeOrder());
        } else {
            ApprovalTimeoutRecord record = new ApprovalTimeoutRecord();
            record.setInstanceId(instance.getId());
            record.setNodeOrder(node.getNodeOrder());
            record.setActionTaken("ESCALATE");
            record.setEscalateToRole(node.getEscalateRole());
            timeoutMapper.insert(record);
            log.info("实例{}节点{}超时升级到{}", instance.getId(),
                    node.getNodeOrder(), node.getEscalateRole());
        }
    }

    // ---- Internal Helpers ----

    private boolean checkCounterSignComplete(ApprovalNode node, Long instanceId) {
        List<ApprovalAddSign> addSigns = getAddSigns(instanceId, node.getNodeOrder());
        for (ApprovalAddSign as : addSigns) {
            if (as.getApproved() == null) return false; // 加签人未审批
        }
        return true;
    }

    private void advanceToNextNode(ApprovalInstance instance, List<ApprovalNode> nodes, int currentOrder) {
        int nextOrder = currentOrder + 1;
        int maxOrder = nodes.stream().mapToInt(ApprovalNode::getNodeOrder).max().orElse(0);

        // 跳过禁用节点和条件不满足的节点
        while (nextOrder <= maxOrder) {
            final int order = nextOrder;
            ApprovalNode next = nodes.stream()
                    .filter(n -> n.getNodeOrder().equals(order)).findFirst().orElse(null);
            if (next != null && isNodeActive(next, instance)) {
                break;
            }
            nextOrder++;
        }

        if (nextOrder > maxOrder) {
            instance.setStatus("APPROVED");
        } else {
            instance.setCurrentNode(nextOrder);
            instance.setStatus("PENDING");
        }
        instance.setUpdatedAt(LocalDateTime.now());
        approvalInstanceMapper.updateById(instance);
    }

    private boolean isNodeActive(ApprovalNode node, ApprovalInstance instance) {
        if (node.getConditionType() == null || node.getConditionValue() == null) return true;
        // 简单条件过滤：condition_value 非空意味着节点始终启用
        // 实际业务中可根据 bizId 查询业务数据判断金额/类型
        return true;
    }

    private int evaluateStartNode(List<ApprovalNode> nodes, Object context) {
        // 跳过条件不满足的首个节点
        for (ApprovalNode node : nodes) {
            if (isNodeActive(node, null)) {
                return node.getNodeOrder();
            }
        }
        return nodes.get(0).getNodeOrder();
    }

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
