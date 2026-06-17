package com.sasac.platform.supervision.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.supervision.dto.AuditPlanDTO;
import com.sasac.platform.supervision.dto.CaseDTO;
import com.sasac.platform.supervision.dto.FindingDTO;
import com.sasac.platform.supervision.entity.SupAuditFinding;
import com.sasac.platform.supervision.entity.SupAuditPlan;
import com.sasac.platform.supervision.entity.SupRectification;
import com.sasac.platform.supervision.entity.SupViolationCase;
import com.sasac.platform.supervision.mapper.SupAuditFindingMapper;
import com.sasac.platform.supervision.mapper.SupAuditPlanMapper;
import com.sasac.platform.supervision.mapper.SupRectificationMapper;
import com.sasac.platform.supervision.mapper.SupViolationCaseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupSupervisionServiceTest {

    @Mock
    private SupAuditPlanMapper supAuditPlanMapper;

    @Mock
    private SupAuditFindingMapper supAuditFindingMapper;

    @Mock
    private SupRectificationMapper supRectificationMapper;

    @Mock
    private SupViolationCaseMapper supViolationCaseMapper;

    @InjectMocks
    private SupSupervisionService service;

    @Captor
    private ArgumentCaptor<SupAuditPlan> auditPlanCaptor;

    @Captor
    private ArgumentCaptor<SupAuditFinding> findingCaptor;

    @Captor
    private ArgumentCaptor<SupRectification> rectificationCaptor;

    @Captor
    private ArgumentCaptor<SupViolationCase> caseCaptor;

    @Test
    void shouldCreateAuditPlan() {
        AuditPlanDTO dto = new AuditPlanDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setPlanYear(2024);
        dto.setPlanName("2024年度财务审计");
        dto.setAuditScope("财务报表");
        dto.setAuditTeam("审计一组");

        when(supAuditPlanMapper.insert(any(SupAuditPlan.class))).thenReturn(1);

        SupAuditPlan result = service.createAuditPlan(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("DRAFT");
        assertThat(result.getPlanName()).isEqualTo("2024年度财务审计");
        assertThat(result.getPlanYear()).isEqualTo(2024);
        assertThat(result.getCreatedAt()).isNotNull();

        verify(supAuditPlanMapper).insert(auditPlanCaptor.capture());
        SupAuditPlan captured = auditPlanCaptor.getValue();
        assertThat(captured.getStatus()).isEqualTo("DRAFT");
        assertThat(captured.getPlanName()).isEqualTo("2024年度财务审计");
    }

    @Test
    void shouldCreateAuditPlanWithSpecifiedStatus() {
        AuditPlanDTO dto = new AuditPlanDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setPlanYear(2024);
        dto.setPlanName("2024年度内控审计");
        dto.setStatus("APPROVED");

        when(supAuditPlanMapper.insert(any(SupAuditPlan.class))).thenReturn(1);

        SupAuditPlan result = service.createAuditPlan(dto);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void shouldThrowWhenAuditPlanNotFound() {
        when(supAuditPlanMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getAuditPlan(999L));
        assertThat(ex.getMessage()).isEqualTo("审计计划不存在");
    }

    @Test
    void shouldRecordFinding() {
        FindingDTO dto = new FindingDTO();
        dto.setTenantId(1L);
        dto.setPlanId(100L);
        dto.setTitle("资金审批流程缺失");
        dto.setSeverity("HIGH");
        dto.setDescription("发现资金审批环节缺少复核机制");

        when(supAuditFindingMapper.insert(any(SupAuditFinding.class))).thenReturn(1);

        SupAuditFinding result = service.recordFinding(dto);

        assertThat(result).isNotNull();
        assertThat(result.getFindingNo()).startsWith("FX-");
        assertThat(result.getStatus()).isEqualTo("OPEN");
        assertThat(result.getTitle()).isEqualTo("资金审批流程缺失");
        assertThat(result.getSeverity()).isEqualTo("HIGH");

        verify(supAuditFindingMapper).insert(findingCaptor.capture());
        SupAuditFinding captured = findingCaptor.getValue();
        assertThat(captured.getFindingNo()).startsWith("FX-");
        assertThat(captured.getStatus()).isEqualTo("OPEN");
    }

    @Test
    void shouldEscalateOverdueRectification() {
        SupRectification overdue = new SupRectification();
        overdue.setId(1L);
        overdue.setTenantId(1L);
        overdue.setFindingId(100L);
        overdue.setTaskTitle("修复资金审批流程");
        overdue.setDeadline(LocalDate.now().minusDays(5));
        overdue.setStatus("PENDING");

        when(supRectificationMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(overdue));
        when(supRectificationMapper.updateById(any(SupRectification.class))).thenReturn(1);

        List<SupRectification> results = service.checkOverdueRectifications(1L);

        assertThat(results).hasSize(1);
        SupRectification result = results.get(0);
        assertThat(result.getStatus()).isEqualTo("ESCALATED");

        verify(supRectificationMapper).updateById(rectificationCaptor.capture());
        assertThat(rectificationCaptor.getValue().getStatus()).isEqualTo("ESCALATED");
    }

    @Test
    void shouldNotEscalateNonOverdueRectifications() {
        when(supRectificationMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<SupRectification> results = service.checkOverdueRectifications(1L);

        assertThat(results).isEmpty();
    }

    @Test
    void shouldInvestigateCase() {
        SupViolationCase violationCase = new SupViolationCase();
        violationCase.setId(1L);
        violationCase.setTenantId(1L);
        violationCase.setCaseNo("WJ-20240101-1234");
        violationCase.setCaseTitle("违规担保案件");
        violationCase.setStatus("INVESTIGATING");

        when(supViolationCaseMapper.selectById(1L)).thenReturn(violationCase);
        when(supViolationCaseMapper.updateById(any(SupViolationCase.class))).thenReturn(1);
        when(supViolationCaseMapper.selectById(1L)).thenReturn(violationCase);

        SupViolationCase result = service.investigate(1L, "经查实存在违规行为", new BigDecimal("500000"));

        assertThat(result).isNotNull();
        assertThat(result.getInvestigationResult()).isEqualTo("经查实存在违规行为");
        assertThat(result.getAssetLoss()).isEqualByComparingTo(new BigDecimal("500000"));

        verify(supViolationCaseMapper).updateById(caseCaptor.capture());
        SupViolationCase captured = caseCaptor.getValue();
        assertThat(captured.getInvestigationResult()).isEqualTo("经查实存在违规行为");
        assertThat(captured.getAssetLoss()).isEqualByComparingTo(new BigDecimal("500000"));
    }

    @Test
    void shouldThrowWhenCaseNotFound() {
        when(supViolationCaseMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.investigate(999L, "调查结果", null));
        assertThat(ex.getMessage()).isEqualTo("案件不存在");
    }

    @Test
    void shouldOpenCase() {
        CaseDTO dto = new CaseDTO();
        dto.setTenantId(1L);
        dto.setCaseTitle("违规经营案件");
        dto.setViolationType("FRAUD");
        dto.setSuspectId(100L);
        dto.setSuspectName("张三");

        when(supViolationCaseMapper.insert(any(SupViolationCase.class))).thenReturn(1);

        SupViolationCase result = service.openCase(dto);

        assertThat(result).isNotNull();
        assertThat(result.getCaseNo()).startsWith("WJ-");
        assertThat(result.getStatus()).isEqualTo("INVESTIGATING");
        assertThat(result.getCaseTitle()).isEqualTo("违规经营案件");
        assertThat(result.getSuspectName()).isEqualTo("张三");
    }

    @Test
    void shouldDecidePunishment() {
        SupViolationCase violationCase = new SupViolationCase();
        violationCase.setId(1L);
        violationCase.setTenantId(1L);
        violationCase.setCaseNo("WJ-20240101-5678");
        violationCase.setCaseTitle("违规担保案件");
        violationCase.setStatus("INVESTIGATING");

        when(supViolationCaseMapper.selectById(1L)).thenReturn(violationCase);
        when(supViolationCaseMapper.updateById(any(SupViolationCase.class))).thenReturn(1);
        when(supViolationCaseMapper.selectById(1L)).thenReturn(violationCase);

        SupViolationCase result = service.decidePunishment(1L, "警告处分");

        assertThat(result).isNotNull();
        assertThat(result.getPunishmentDecision()).isEqualTo("警告处分");
        assertThat(result.getStatus()).isEqualTo("RESOLVED");
    }
}
