package com.sasac.platform.investment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.investment.dto.InvestExitDTO;
import com.sasac.platform.investment.dto.InvestPlanCreateDTO;
import com.sasac.platform.investment.dto.InvestProjectCreateDTO;
import com.sasac.platform.investment.dto.InvestProjectQueryDTO;
import com.sasac.platform.investment.entity.InvestDD;
import com.sasac.platform.investment.entity.InvestDeal;
import com.sasac.platform.investment.entity.InvestEquityNode;
import com.sasac.platform.investment.entity.InvestExit;
import com.sasac.platform.investment.entity.InvestPlan;
import com.sasac.platform.investment.entity.InvestPost;
import com.sasac.platform.investment.entity.InvestProject;
import com.sasac.platform.investment.mapper.InvestDDMapper;
import com.sasac.platform.investment.mapper.InvestDealMapper;
import com.sasac.platform.investment.mapper.InvestEquityNodeMapper;
import com.sasac.platform.investment.mapper.InvestExitMapper;
import com.sasac.platform.investment.mapper.InvestPlanMapper;
import com.sasac.platform.investment.mapper.InvestPostMapper;
import com.sasac.platform.investment.mapper.InvestProjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestServiceTest {

    @Mock
    private InvestPlanMapper investPlanMapper;

    @Mock
    private InvestProjectMapper investProjectMapper;

    @Mock
    private InvestDDMapper investDDMapper;

    @Mock
    private InvestDealMapper investDealMapper;

    @Mock
    private InvestPostMapper investPostMapper;

    @Mock
    private InvestExitMapper investExitMapper;

    @Mock
    private InvestEquityNodeMapper investEquityNodeMapper;

    @InjectMocks
    private InvestService investService;

    @Test
    void shouldCreateInvestPlan() {
        // given
        InvestPlanCreateDTO dto = new InvestPlanCreateDTO();
        dto.setPlanYear(2024);
        dto.setPlanName("2024年度投资计划");
        dto.setOrgId(1L);
        dto.setTenantId(0L);
        dto.setTotalBudget(new BigDecimal("100000000"));

        when(investPlanMapper.insert(any(InvestPlan.class))).thenReturn(1);

        // when
        InvestPlan result = investService.createPlan(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlanName()).isEqualTo("2024年度投资计划");
        assertThat(result.getStatus()).isEqualTo("DRAFT");

        ArgumentCaptor<InvestPlan> captor = ArgumentCaptor.forClass(InvestPlan.class);
        verify(investPlanMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("DRAFT");
    }

    @Test
    void shouldCreateInvestProject() {
        // given
        InvestProjectCreateDTO dto = new InvestProjectCreateDTO();
        dto.setProjectName("某科技公司股权投资");
        dto.setInvestType("EQUITY");
        dto.setTargetCompany("某科技公司");
        dto.setInvestAmount(new BigDecimal("50000000"));
        dto.setOrgId(1L);
        dto.setTenantId(0L);

        when(investProjectMapper.insert(any(InvestProject.class))).thenReturn(1);

        // when
        InvestProject result = investService.createProject(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProjectNo()).startsWith("IV-");
        assertThat(result.getPhase()).isEqualTo("PRE_INVEST");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getProjectName()).isEqualTo("某科技公司股权投资");

        ArgumentCaptor<InvestProject> captor = ArgumentCaptor.forClass(InvestProject.class);
        verify(investProjectMapper).insert(captor.capture());
        InvestProject captured = captor.getValue();
        assertThat(captured.getProjectNo()).startsWith("IV-");
        assertThat(captured.getPhase()).isEqualTo("PRE_INVEST");
        assertThat(captured.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldBuildEquityTree() {
        // given
        InvestEquityNode root = new InvestEquityNode();
        root.setId(1L);
        root.setTenantId(0L);
        root.setProjectId(1L);
        root.setCompanyName("集团总公司");
        root.setParentId(null);
        root.setLevel(1);

        InvestEquityNode child = new InvestEquityNode();
        child.setId(2L);
        child.setTenantId(0L);
        child.setProjectId(1L);
        child.setCompanyName("子公司A");
        child.setParentId(1L);
        child.setEquityPct(new BigDecimal("60.00"));
        child.setLevel(2);

        InvestEquityNode grandchild = new InvestEquityNode();
        grandchild.setId(3L);
        grandchild.setTenantId(0L);
        grandchild.setProjectId(1L);
        grandchild.setCompanyName("孙公司A1");
        grandchild.setParentId(2L);
        grandchild.setEquityPct(new BigDecimal("100.00"));
        grandchild.setLevel(3);

        when(investEquityNodeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(root, child, grandchild));

        // when
        InvestEquityNode result = investService.buildEquityTree(1L, 0L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyName()).isEqualTo("集团总公司");
        assertThat(result.getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0).getCompanyName()).isEqualTo("子公司A");
        assertThat(result.getChildren().get(0).getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0).getChildren().get(0).getCompanyName())
                .isEqualTo("孙公司A1");
    }

    @Test
    void shouldBuildEquityTreeWithMultipleRoots() {
        // given
        InvestEquityNode root1 = new InvestEquityNode();
        root1.setId(1L);
        root1.setTenantId(0L);
        root1.setProjectId(1L);
        root1.setCompanyName("主体A");
        root1.setParentId(null);
        root1.setLevel(1);

        InvestEquityNode root2 = new InvestEquityNode();
        root2.setId(2L);
        root2.setTenantId(0L);
        root2.setProjectId(1L);
        root2.setCompanyName("主体B");
        root2.setParentId(null);
        root2.setLevel(1);

        when(investEquityNodeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(root1, root2));

        // when
        InvestEquityNode result = investService.buildEquityTree(1L, 0L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyName()).isEqualTo("投资主体");
        assertThat(result.getChildren()).hasSize(2);
    }

    @Test
    void shouldThrowWhenEquityTreeNotFound() {
        // given
        when(investEquityNodeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of());

        // when & then
        assertThrows(BusinessException.class,
                () -> investService.buildEquityTree(1L, 0L));
    }

    @Test
    void shouldRecordExit() {
        // given
        InvestExitDTO dto = new InvestExitDTO();
        dto.setProjectId(1L);
        dto.setTenantId(0L);
        dto.setExitDate(java.time.LocalDate.of(2024, 12, 31));
        dto.setExitAmount(new BigDecimal("60000000"));
        dto.setExitMethod("EQUITY_TRANSFER");

        when(investExitMapper.insert(any(InvestExit.class))).thenReturn(1);

        // when
        InvestExit result = investService.recordExit(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProjectId()).isEqualTo(1L);
        assertThat(result.getExitMethod()).isEqualTo("EQUITY_TRANSFER");
        assertThat(result.getStatus()).isEqualTo("DRAFT");

        ArgumentCaptor<InvestExit> captor = ArgumentCaptor.forClass(InvestExit.class);
        verify(investExitMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("DRAFT");
    }

    @Test
    void shouldQueryProjects() {
        // given
        InvestProjectQueryDTO q = new InvestProjectQueryDTO();
        q.setKeyword("科技");
        q.setInvestType("EQUITY");
        q.setStatus("ACTIVE");
        q.setPhase("PRE_INVEST");
        q.setPage(1);
        q.setLimit(20);

        Page<InvestProject> expectedPage = new Page<>(1, 20);
        when(investProjectMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        // when
        Page<InvestProject> result = investService.queryProjects(q);

        // then
        assertThat(result).isNotNull();
        verify(investProjectMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldGetProjectById() {
        // given
        InvestProject project = new InvestProject();
        project.setId(1L);
        project.setProjectNo("IV-123");
        project.setProjectName("某科技公司股权投资");

        when(investProjectMapper.selectById(1L)).thenReturn(project);

        // when
        InvestProject result = investService.getProjectById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProjectName()).isEqualTo("某科技公司股权投资");
    }
}
