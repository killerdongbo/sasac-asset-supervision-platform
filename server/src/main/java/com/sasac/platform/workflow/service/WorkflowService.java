package com.sasac.platform.workflow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.workflow.entity.WorkflowDef;
import com.sasac.platform.workflow.entity.WorkflowInstance;
import com.sasac.platform.workflow.entity.WorkflowTask;
import com.sasac.platform.workflow.mapper.WorkflowDefMapper;
import com.sasac.platform.workflow.mapper.WorkflowInstanceMapper;
import com.sasac.platform.workflow.mapper.WorkflowTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Core workflow execution engine.
 * <p>
 * Manages the full lifecycle of workflow definitions, instances, and approval tasks.
 * Parses the visual graph JSON to drive state transitions through nodes,
 * edges, gateways, parallel forks, and joins.
 */
@Slf4j
@Service
public class WorkflowService {

    private final WorkflowDefMapper defMapper;
    private final WorkflowInstanceMapper instanceMapper;
    private final WorkflowTaskMapper taskMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorkflowService(WorkflowDefMapper defMapper, WorkflowInstanceMapper instanceMapper,
                           WorkflowTaskMapper taskMapper) {
        this.defMapper = defMapper;
        this.instanceMapper = instanceMapper;
        this.taskMapper = taskMapper;
    }

    // ========== Section 1: Definition CRUD ==========

    /**
     * List all workflow definitions for a given tenant, ordered by creation time descending.
     */
    public List<WorkflowDef> listDefs(Long tenantId) {
        return defMapper.selectList(
            new LambdaQueryWrapper<WorkflowDef>()
                .eq(WorkflowDef::getTenantId, tenantId)
                .orderByDesc(WorkflowDef::getCreatedAt));
    }

    /**
     * Get a single workflow definition by ID.
     */
    public WorkflowDef getDef(Long id) {
        return defMapper.selectById(id);
    }

    /**
     * Create a new workflow definition.
     */
    @Transactional
    public WorkflowDef createDef(WorkflowDef def) {
        if (def.getGraphJson() == null) {
            def.setGraphJson("{\"nodes\":[],\"edges\":[]}");
        }
        def.setStatus("ACTIVE");
        defMapper.insert(def);
        return def;
    }

    /**
     * Update an existing workflow definition.
     */
    @Transactional
    public WorkflowDef updateDef(Long id, WorkflowDef update) {
        WorkflowDef existing = defMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("流程定义不存在");
        }
        existing.setName(update.getName());
        existing.setBizType(update.getBizType());
        existing.setGraphJson(update.getGraphJson());
        existing.setDescription(update.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());
        defMapper.updateById(existing);
        return existing;
    }

    /**
     * Delete a workflow definition by ID.
     */
    @Transactional
    public void deleteDef(Long id) {
        defMapper.deleteById(id);
    }

    // ========== Section 2: Graph Parsing Helpers ==========

    /**
     * Parse the graph JSON of a workflow definition and return a map of node ID to node JsonNode.
     */
    private Map<String, JsonNode> parseNodes(WorkflowDef def) {
        try {
            Map<String, JsonNode> map = new LinkedHashMap<>();
            JsonNode root = objectMapper.readTree(def.getGraphJson());
            for (JsonNode n : root.path("nodes")) {
                map.put(n.path("id").asText(), n);
            }
            return map;
        } catch (Exception e) {
            throw new BusinessException("流程定义解析失败: " + e.getMessage());
        }
    }

    /**
     * Get all outgoing edges from a given node ID.
     */
    private List<JsonNode> getOutgoingEdges(WorkflowDef def, String nodeId) {
        try {
            List<JsonNode> result = new ArrayList<>();
            JsonNode root = objectMapper.readTree(def.getGraphJson());
            for (JsonNode e : root.path("edges")) {
                if (nodeId.equals(e.path("source").asText())) {
                    result.add(e);
                }
            }
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Find the start node in a workflow definition's graph.
     */
    private JsonNode findStartNode(WorkflowDef def) {
        for (JsonNode n : parseNodes(def).values()) {
            if ("start".equals(n.path("type").asText())) {
                return n;
            }
        }
        throw new BusinessException("流程定义中未找到开始节点");
    }

    // ========== Section 3: Instance Lifecycle ==========

    /**
     * Start a new workflow instance.
     * <p>
     * Validates the definition, creates the instance record, locates the start node,
     * follows the first outgoing edge, and creates the initial approval task.
     */
    @Transactional
    public WorkflowInstance startInstance(Long defId, Long submitterId, Long tenantId,
                                           Long bizId, String bizType, String contextJson) {
        WorkflowDef def = defMapper.selectById(defId);
        if (def == null) {
            throw new BusinessException("流程定义不存在");
        }

        WorkflowInstance instance = new WorkflowInstance();
        instance.setDefId(defId);
        instance.setBizId(bizId);
        instance.setBizType(bizType);
        instance.setStatus("PENDING");
        instance.setSubmitterId(submitterId);
        instance.setTenantId(tenantId);
        instance.setContextJson(contextJson);
        instance.setCurrentNodeIds("");

        JsonNode startNode = findStartNode(def);
        List<JsonNode> startEdges = getOutgoingEdges(def, startNode.path("id").asText());
        if (startEdges.isEmpty()) {
            throw new BusinessException("开始节点后无后续节点");
        }

        String targetId = startEdges.get(0).path("target").asText();
        JsonNode firstNode = parseNodes(def).get(targetId);
        if (firstNode == null) {
            throw new BusinessException("流程定义异常：找不到目标节点 " + targetId);
        }
        if (!"approval".equals(firstNode.path("type").asText())) {
            throw new BusinessException("开始节点后必须是审批节点");
        }

        instanceMapper.insert(instance);
        createTask(instance, targetId, firstNode);
        instance.setCurrentNodeIds(targetId);
        instanceMapper.updateById(instance);
        return instance;
    }

    /**
     * Create a new workflow task for the given node definition.
     */
    private void createTask(WorkflowInstance instance, String nodeId, JsonNode nodeDef) {
        WorkflowTask task = new WorkflowTask();
        task.setInstanceId(instance.getId());
        task.setNodeId(nodeId);
        JsonNode data = nodeDef.path("data");
        task.setApproverRole(data.path("approverRole").asText(""));
        task.setAction("PENDING");
        taskMapper.insert(task);
    }

    // ========== Section 4: Approval Engine ==========

    /**
     * Process an approval or rejection for a task and advance the workflow.
     * <p>
     * On approval, the engine advances from the current node to the next node(s)
     * following the graph edges. On rejection, the entire instance is marked as
     * REJECTED and all pending tasks are cancelled.
     */
    @Transactional
    public void approveTask(Long taskId, Long approverId, boolean approved, String remark) {
        WorkflowTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("审批任务不存在");
        }
        if (!"PENDING".equals(task.getAction())) {
            throw new BusinessException("该任务已处理");
        }

        task.setAction(approved ? "APPROVED" : "REJECTED");
        task.setApproverId(approverId);
        task.setRemark(remark);
        task.setCompletedAt(LocalDateTime.now());
        taskMapper.updateById(task);

        WorkflowInstance instance = instanceMapper.selectById(task.getInstanceId());
        if (instance == null) {
            throw new BusinessException("流程实例不存在");
        }

        if (!approved) {
            instance.setStatus("REJECTED");
            cancelPendingTasks(instance.getId());
            instanceMapper.updateById(instance);
            return;
        }

        advanceFromNode(instance, task.getNodeId());
    }

    /**
     * Cancel all pending tasks for a given instance (used on rejection).
     */
    private void cancelPendingTasks(Long instanceId) {
        List<WorkflowTask> pendingTasks = taskMapper.selectList(
            new LambdaQueryWrapper<WorkflowTask>()
                .eq(WorkflowTask::getInstanceId, instanceId)
                .eq(WorkflowTask::getAction, "PENDING"));
        for (WorkflowTask t : pendingTasks) {
            t.setAction("REJECTED");
            t.setCompletedAt(LocalDateTime.now());
            taskMapper.updateById(t);
        }
    }

    /**
     * Advance the workflow from the completed node to the next node(s) along the graph edges.
     */
    private void advanceFromNode(WorkflowInstance instance, String fromNodeId) {
        WorkflowDef def = defMapper.selectById(instance.getDefId());

        // Check if parallel branches are still active
        String currentIds = instance.getCurrentNodeIds() != null ? instance.getCurrentNodeIds() : "";
        String[] activeIds = currentIds.isEmpty() ? new String[0] : currentIds.split(",");
        List<String> remaining = new ArrayList<>();
        for (String id : activeIds) {
            String trimmed = id.trim();
            if (!trimmed.isEmpty() && !trimmed.equals(fromNodeId)) {
                List<WorkflowTask> tasks = taskMapper.selectList(
                    new LambdaQueryWrapper<WorkflowTask>()
                        .eq(WorkflowTask::getInstanceId, instance.getId())
                        .eq(WorkflowTask::getNodeId, trimmed));
                boolean allDone = tasks.stream().allMatch(t -> !"PENDING".equals(t.getAction()));
                if (!allDone) {
                    remaining.add(trimmed);
                }
            }
        }

        // Process outgoing edges from the completed node
        List<JsonNode> edges = getOutgoingEdges(def, fromNodeId);
        if (edges.isEmpty() && remaining.isEmpty()) {
            instance.setStatus("APPROVED");
            instance.setCurrentNodeIds("");
            instanceMapper.updateById(instance);
            return;
        }

        List<String> nextIds = new ArrayList<>(remaining);
        Map<String, JsonNode> nodeMap = parseNodes(def);
        for (JsonNode edge : edges) {
            String targetId = edge.path("target").asText();
            processAdvanceTarget(instance, def, targetId, nodeMap, nextIds);
        }

        instance.setCurrentNodeIds(String.join(",", nextIds));
        if (nextIds.isEmpty() && "PENDING".equals(instance.getStatus())) {
            instance.setStatus("APPROVED");
        }
        instanceMapper.updateById(instance);
    }

    /**
     * Process a single target node when advancing the workflow.
     * <p>
     * Handles different node types: approval, end, gateway, parallel, and join.
     */
    private void processAdvanceTarget(WorkflowInstance instance, WorkflowDef def,
                                       String targetId, Map<String, JsonNode> nodeMap,
                                       List<String> nextIds) {
        JsonNode target = nodeMap.get(targetId);
        if (target == null) {
            return;
        }
        String type = target.path("type").asText();

        switch (type) {
            case "approval":
                createTask(instance, targetId, target);
                nextIds.add(targetId);
                break;
            case "end":
                instance.setStatus("APPROVED");
                break;
            case "gateway":
                String result = evaluateGateway(instance, target);
                List<JsonNode> gwEdges = getOutgoingEdges(def, targetId);
                for (JsonNode e : gwEdges) {
                    String label = e.path("label").asText("是");
                    if (label.equals(result)) {
                        processAdvanceTarget(instance, def, e.path("target").asText(), nodeMap, nextIds);
                        break;
                    }
                }
                break;
            case "parallel":
                List<JsonNode> forkEdges = getOutgoingEdges(def, targetId);
                for (JsonNode e : forkEdges) {
                    processAdvanceTarget(instance, def, e.path("target").asText(), nodeMap, nextIds);
                }
                break;
            case "join":
                nextIds.remove(targetId);
                List<JsonNode> joinEdges = getOutgoingEdges(def, targetId);
                for (JsonNode e : joinEdges) {
                    processAdvanceTarget(instance, def, e.path("target").asText(), nodeMap, nextIds);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Evaluate a gateway condition expression against the instance context.
     */
    private String evaluateGateway(WorkflowInstance instance, JsonNode gateway) {
        String expr = gateway.path("data").path("conditionExpr").asText("");
        if (expr.isBlank()) {
            return "是";
        }
        try {
            JsonNode context = objectMapper.readTree(
                instance.getContextJson() != null ? instance.getContextJson() : "{}");
            if (expr.contains("amount >")) {
                double threshold = Double.parseDouble(expr.replaceAll("[^0-9.]", ""));
                double amount = context.path("amount").asDouble(0);
                return amount > threshold ? "是" : "否";
            }
            if (expr.contains("amount <")) {
                double threshold = Double.parseDouble(expr.replaceAll("[^0-9.]", ""));
                double amount = context.path("amount").asDouble(0);
                return amount < threshold ? "是" : "否";
            }
        } catch (Exception e) {
            log.warn("Gateway evaluation failed for expr: {}", expr, e);
        }
        return "是";
    }

    // ========== Section 5: Query APIs ==========

    /**
     * Get all pending tasks, optionally filtered by approver role.
     */
    public List<WorkflowTask> getPendingTasks(Long tenantId, String roleCode) {
        return taskMapper.selectList(
            new LambdaQueryWrapper<WorkflowTask>()
                .eq(WorkflowTask::getAction, "PENDING")
                .eq(roleCode != null && !roleCode.isEmpty(), WorkflowTask::getApproverRole, roleCode));
    }

    /**
     * Get all workflow instances submitted by a given user.
     */
    public List<WorkflowInstance> getMyRequests(Long submitterId) {
        return instanceMapper.selectList(
            new LambdaQueryWrapper<WorkflowInstance>()
                .eq(WorkflowInstance::getSubmitterId, submitterId)
                .orderByDesc(WorkflowInstance::getCreatedAt));
    }

    /**
     * Get a single workflow instance by ID.
     */
    public WorkflowInstance getInstance(Long id) {
        return instanceMapper.selectById(id);
    }
}
