package com.sasac.platform.supervision.approval.service;

import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.supervision.approval.entity.ApprovalDef;
import com.sasac.platform.supervision.approval.entity.ApprovalInstance;
import com.sasac.platform.supervision.approval.entity.ApprovalNode;
import com.sasac.platform.supervision.approval.mapper.ApprovalDefMapper;
import com.sasac.platform.supervision.approval.mapper.ApprovalInstanceMapper;
import com.sasac.platform.supervision.approval.mapper.ApprovalNodeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ApprovalServiceTest {

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ApprovalDefMapper approvalDefMapper;

    @Autowired
    private ApprovalNodeMapper approvalNodeMapper;

    @Autowired
    private ApprovalInstanceMapper approvalInstanceMapper;

    private ApprovalDef testDef;
    private ApprovalNode node1;
    private ApprovalNode node2;
    private ApprovalNode node3;

    @BeforeEach
    void setUp() {
        // Create an approval definition with 3 nodes
        testDef = new ApprovalDef();
        testDef.setDefName("资产处置审批流程");
        testDef.setBizType("DISPOSAL");
        testDef.setStatus("ACTIVE");
        testDef.setDescription("资产处置三级审批流程");
        testDef.setTenantId(0L);

        node1 = new ApprovalNode();
        node1.setNodeOrder(1);
        node1.setApproverRole("DEPARTMENT_HEAD");
        node1.setConditionExpr(null);
        node1.setCanReject(true);
        node1.setTimeoutHours(48);

        node2 = new ApprovalNode();
        node2.setNodeOrder(2);
        node2.setApproverRole("FINANCE_MANAGER");
        node2.setConditionExpr("asset.originalValue > 100000");
        node2.setCanReject(true);
        node2.setTimeoutHours(72);

        node3 = new ApprovalNode();
        node3.setNodeOrder(3);
        node3.setApproverRole("CEO");
        node3.setConditionExpr(null);
        node3.setCanReject(true);
        node3.setTimeoutHours(120);
    }

    @Test
    void shouldCompleteFullApprovalFlow() {
        // Given: create def with 3 nodes
        ApprovalDef def = approvalService.createDef(testDef);
        node1.setDefId(def.getId());
        node2.setDefId(def.getId());
        node3.setDefId(def.getId());
        approvalService.addNode(node1);
        approvalService.addNode(node2);
        approvalService.addNode(node3);

        // When: start an instance
        ApprovalInstance instance = approvalService.startInstance(def.getId(), 100L, "DISPOSAL", 1L);

        // Then: instance is PENDING at node 1
        assertThat(instance.getStatus()).isEqualTo("PENDING");
        assertThat(instance.getCurrentNode()).isEqualTo(1);

        // When: approve node 1
        approvalService.approve(instance.getId(), 2L, true, "部门负责人审批通过");

        // Then: advance to node 2
        ApprovalInstance afterNode1 = approvalInstanceMapper.selectById(instance.getId());
        assertThat(afterNode1.getStatus()).isEqualTo("PENDING");
        assertThat(afterNode1.getCurrentNode()).isEqualTo(2);

        // When: approve node 2
        approvalService.approve(instance.getId(), 3L, true, "财务经理审批通过");

        // Then: advance to node 3
        ApprovalInstance afterNode2 = approvalInstanceMapper.selectById(instance.getId());
        assertThat(afterNode2.getStatus()).isEqualTo("PENDING");
        assertThat(afterNode2.getCurrentNode()).isEqualTo(3);

        // When: approve node 3 (last node)
        approvalService.approve(instance.getId(), 4L, true, "CEO审批通过");

        // Then: instance is APPROVED
        ApprovalInstance afterNode3 = approvalInstanceMapper.selectById(instance.getId());
        assertThat(afterNode3.getStatus()).isEqualTo("APPROVED");
        assertThat(afterNode3.getCurrentNode()).isEqualTo(3);

        // Then: verify node results JSON is stored
        assertThat(afterNode3.getNodeResults()).isNotNull();
        assertThat(afterNode3.getNodeResults()).contains("DEPARTMENT_HEAD");
        assertThat(afterNode3.getNodeResults()).contains("FINANCE_MANAGER");
        assertThat(afterNode3.getNodeResults()).contains("CEO");
    }

    @Test
    void shouldRejectAtSecondNode() {
        // Given: create def with 2 nodes
        ApprovalDef def = approvalService.createDef(testDef);
        node1.setDefId(def.getId());
        node2.setDefId(def.getId());
        approvalService.addNode(node1);
        approvalService.addNode(node2);

        // When: start instance
        ApprovalInstance instance = approvalService.startInstance(def.getId(), 200L, "TRANSFER", 1L);

        // When: approve first node
        approvalService.approve(instance.getId(), 2L, true, "部门负责人审批通过");

        // When: reject second node
        approvalService.approve(instance.getId(), 3L, false, "财务经理驳回");

        // Then: instance status is REJECTED
        ApprovalInstance rejected = approvalInstanceMapper.selectById(instance.getId());
        assertThat(rejected.getStatus()).isEqualTo("REJECTED");
        assertThat(rejected.getCurrentNode()).isEqualTo(2);
    }

    @Test
    void shouldGetPendingApprovalsByRole() {
        // Given: create def with nodes
        ApprovalDef def = approvalService.createDef(testDef);
        node1.setDefId(def.getId());
        node2.setDefId(def.getId());
        approvalService.addNode(node1);
        approvalService.addNode(node2);

        // When: start two instances
        ApprovalInstance inst1 = approvalService.startInstance(def.getId(), 300L, "PURCHASE", 1L);
        ApprovalInstance inst2 = approvalService.startInstance(def.getId(), 301L, "PURCHASE", 5L);

        // Then: DEPARTMENT_HEAD should see 2 pending
        List<ApprovalInstance> pendingForDept = approvalService.getPendingApprovals(0L, "DEPARTMENT_HEAD");
        assertThat(pendingForDept).hasSize(2);

        // When: approve first node of inst1
        approvalService.approve(inst1.getId(), 2L, true, "审批通过");

        // Then: DEPARTMENT_HEAD sees 1 pending, FINANCE_MANAGER sees 1 pending
        List<ApprovalInstance> deptPending = approvalService.getPendingApprovals(0L, "DEPARTMENT_HEAD");
        assertThat(deptPending).hasSize(1);
        assertThat(deptPending.get(0).getId()).isEqualTo(inst2.getId());

        List<ApprovalInstance> financePending = approvalService.getPendingApprovals(0L, "FINANCE_MANAGER");
        assertThat(financePending).hasSize(1);
        assertThat(financePending.get(0).getId()).isEqualTo(inst1.getId());
    }

    @Test
    void shouldGetMyRequests() {
        // Given
        ApprovalDef def = approvalService.createDef(testDef);
        node1.setDefId(def.getId());
        approvalService.addNode(node1);

        // When
        ApprovalInstance inst1 = approvalService.startInstance(def.getId(), 400L, "REPORT", 1L);
        ApprovalInstance inst2 = approvalService.startInstance(def.getId(), 401L, "REPORT", 1L);
        ApprovalInstance inst3 = approvalService.startInstance(def.getId(), 402L, "REPORT", 9L);

        // Then
        List<ApprovalInstance> myRequests = approvalService.getMyRequests(1L);
        assertThat(myRequests).hasSize(2);

        List<ApprovalInstance> otherRequests = approvalService.getMyRequests(9L);
        assertThat(otherRequests).hasSize(1);
        assertThat(otherRequests.get(0).getId()).isEqualTo(inst3.getId());
    }

    @Test
    void shouldThrowWhenStartingInstanceForNonExistentDef() {
        assertThatThrownBy(() -> approvalService.startInstance(-999L, 500L, "DISPOSAL", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("审批定义不存在");
    }

    @Test
    void shouldCreateDefWithNodes() {
        // When
        ApprovalDef def = approvalService.createDef(testDef);
        node1.setDefId(def.getId());
        node2.setDefId(def.getId());
        approvalService.addNode(node1);
        approvalService.addNode(node2);

        // Then: def persisted
        ApprovalDef foundDef = approvalDefMapper.selectById(def.getId());
        assertThat(foundDef).isNotNull();
        assertThat(foundDef.getDefName()).isEqualTo("资产处置审批流程");
        assertThat(foundDef.getBizType()).isEqualTo("DISPOSAL");
        assertThat(foundDef.getStatus()).isEqualTo("ACTIVE");

        // Then: nodes persisted
        List<ApprovalNode> nodes = approvalNodeMapper.selectByDefId(def.getId());
        assertThat(nodes).hasSize(2);
        assertThat(nodes.get(0).getNodeOrder()).isEqualTo(1);
        assertThat(nodes.get(0).getApproverRole()).isEqualTo("DEPARTMENT_HEAD");
        assertThat(nodes.get(1).getNodeOrder()).isEqualTo(2);
        assertThat(nodes.get(1).getApproverRole()).isEqualTo("FINANCE_MANAGER");
    }
}
