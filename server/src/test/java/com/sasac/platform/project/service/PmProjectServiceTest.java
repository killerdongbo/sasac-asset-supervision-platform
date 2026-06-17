package com.sasac.platform.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.project.dto.AcceptanceDTO;
import com.sasac.platform.project.dto.ProjectCreateDTO;
import com.sasac.platform.project.dto.ProjectQueryDTO;
import com.sasac.platform.project.dto.ProgressRecordDTO;
import com.sasac.platform.project.entity.PmAcceptance;
import com.sasac.platform.project.entity.PmProject;
import com.sasac.platform.project.entity.PmProgress;
import com.sasac.platform.project.mapper.PmAcceptanceMapper;
import com.sasac.platform.project.mapper.PmBudgetMapper;
import com.sasac.platform.project.mapper.PmProjectMapper;
import com.sasac.platform.project.mapper.PmProgressMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PmProjectServiceTest {

    @Mock
    private PmProjectMapper pmProjectMapper;

    @Mock
    private PmProgressMapper pmProgressMapper;

    @Mock
    private PmAcceptanceMapper pmAcceptanceMapper;

    @Mock
    private PmBudgetMapper pmBudgetMapper;

    @InjectMocks
    private PmProjectService pmProjectService;

    @Test
    void shouldCreateProject() {
        // given
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setName("智能监管平台二期");
        dto.setProjectType("IT");
        dto.setOrgId(1L);
        dto.setTenantId(0L);
        dto.setBudgetTotal(new BigDecimal("5000000"));
        dto.setStartDate(LocalDate.of(2024, 1, 1));
        dto.setPlannedEndDate(LocalDate.of(2024, 12, 31));

        when(pmProjectMapper.insert(any(PmProject.class))).thenReturn(1);

        // when
        PmProject result = pmProjectService.create(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProjectNo()).startsWith("XM-");
        assertThat(result.getStatus()).isEqualTo("DRAFT");
        assertThat(result.getName()).isEqualTo("智能监管平台二期");

        ArgumentCaptor<PmProject> captor = ArgumentCaptor.forClass(PmProject.class);
        verify(pmProjectMapper).insert(captor.capture());
        PmProject captured = captor.getValue();
        assertThat(captured.getProjectNo()).startsWith("XM-");
        assertThat(captured.getStatus()).isEqualTo("DRAFT");
    }

    @Test
    void shouldRecordProgress() {
        // given
        PmProject project = new PmProject();
        project.setId(1L);
        project.setName("智能监管平台二期");
        project.setStatus("IN_PROGRESS");

        ProgressRecordDTO dto = new ProgressRecordDTO();
        dto.setProjectId(1L);
        dto.setTenantId(0L);
        dto.setReportDate(LocalDate.now());
        dto.setProgressPct(new BigDecimal("50.00"));
        dto.setCompletedWork("完成需求分析");
        dto.setNextPlan("开始系统设计");

        when(pmProjectMapper.selectById(1L)).thenReturn(project);
        when(pmProgressMapper.insert(any(PmProgress.class))).thenReturn(1);

        // when
        PmProgress result = pmProjectService.recordProgress(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProjectId()).isEqualTo(1L);
        assertThat(result.getCompletedWork()).isEqualTo("完成需求分析");

        ArgumentCaptor<PmProgress> captor = ArgumentCaptor.forClass(PmProgress.class);
        verify(pmProgressMapper).insert(captor.capture());
        assertThat(captor.getValue().getProjectId()).isEqualTo(1L);
    }

    @Test
    void shouldAcceptProject() {
        // given
        PmProject project = new PmProject();
        project.setId(1L);
        project.setName("智能监管平台二期");
        project.setStatus("IN_PROGRESS");

        AcceptanceDTO dto = new AcceptanceDTO();
        dto.setTenantId(0L);
        dto.setAcceptanceDate(LocalDate.of(2024, 12, 31));
        dto.setResult("PASS");
        dto.setReviewOpinion("通过验收");

        when(pmProjectMapper.selectById(1L)).thenReturn(project);

        // when
        pmProjectService.acceptProject(1L, dto);

        // then
        ArgumentCaptor<PmProject> projectCaptor = ArgumentCaptor.forClass(PmProject.class);
        verify(pmProjectMapper).updateById(projectCaptor.capture());
        assertThat(projectCaptor.getValue().getStatus()).isEqualTo("COMPLETED");
        assertThat(projectCaptor.getValue().getActualEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));

        ArgumentCaptor<PmAcceptance> acceptanceCaptor = ArgumentCaptor.forClass(PmAcceptance.class);
        verify(pmAcceptanceMapper).insert(acceptanceCaptor.capture());
        assertThat(acceptanceCaptor.getValue().getProjectId()).isEqualTo(1L);
        assertThat(acceptanceCaptor.getValue().getResult()).isEqualTo("PASS");
    }

    @Test
    void shouldQueryProjects() {
        // given
        ProjectQueryDTO q = new ProjectQueryDTO();
        q.setKeyword("智能监管");
        q.setStatus("DRAFT");
        q.setProjectType("IT");
        q.setPage(1);
        q.setLimit(20);

        Page<PmProject> expectedPage = new Page<>(1, 20);
        when(pmProjectMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        // when
        Page<PmProject> result = pmProjectService.query(q);

        // then
        assertThat(result).isNotNull();
        verify(pmProjectMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldThrowWhenProjectNotFound() {
        // given
        when(pmProjectMapper.selectById(anyLong())).thenReturn(null);

        // when & then
        assertThrows(BusinessException.class, () -> pmProjectService.getById(999L));
    }

    @Test
    void shouldGetProgressHistory() {
        // given
        PmProgress progress = new PmProgress();
        progress.setId(1L);
        progress.setProjectId(1L);
        progress.setTenantId(0L);

        when(pmProgressMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(progress));

        // when
        List<PmProgress> result = pmProjectService.getProgressHistory(1L, 0L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProjectId()).isEqualTo(1L);
    }
}
