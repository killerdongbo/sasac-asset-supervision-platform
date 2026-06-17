package com.sasac.platform.decision.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.decision.dto.DecisionItemCreateDTO;
import com.sasac.platform.decision.dto.MeetingCreateDTO;
import com.sasac.platform.decision.dto.ResolutionCreateDTO;
import com.sasac.platform.decision.dto.SupervisionCreateDTO;
import com.sasac.platform.decision.entity.DecisionItem;
import com.sasac.platform.decision.entity.DecisionMeeting;
import com.sasac.platform.decision.entity.DecisionResolution;
import com.sasac.platform.decision.entity.DecisionSupervision;
import com.sasac.platform.decision.mapper.DecisionItemMapper;
import com.sasac.platform.decision.mapper.DecisionMeetingMapper;
import com.sasac.platform.decision.mapper.DecisionResolutionMapper;
import com.sasac.platform.decision.mapper.DecisionSupervisionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionServiceTest {

    @Mock
    private DecisionItemMapper itemMapper;

    @Mock
    private DecisionMeetingMapper meetingMapper;

    @Mock
    private DecisionResolutionMapper resolutionMapper;

    @Mock
    private DecisionSupervisionMapper supervisionMapper;

    @InjectMocks
    private DecisionService decisionService;

    @Test
    void shouldSubmitDecisionItem() {
        // given
        DecisionItemCreateDTO dto = new DecisionItemCreateDTO();
        dto.setTenantId(0L);
        dto.setOrgId(1L);
        dto.setItemType("INVESTMENT");
        dto.setTitle("关于投资项目的决策");

        when(itemMapper.insert(any(DecisionItem.class))).thenReturn(1);

        // when
        DecisionItem result = decisionService.submitItem(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getItemNo()).startsWith("DJ-");
        assertThat(result.getStatus()).isEqualTo("DRAFT");
        assertThat(result.getTitle()).isEqualTo("关于投资项目的决策");

        ArgumentCaptor<DecisionItem> captor = ArgumentCaptor.forClass(DecisionItem.class);
        verify(itemMapper).insert(captor.capture());
        DecisionItem captured = captor.getValue();
        assertThat(captured.getItemNo()).startsWith("DJ-");
        assertThat(captured.getStatus()).isEqualTo("DRAFT");
    }

    @Test
    void shouldCreateMeeting() {
        // given
        MeetingCreateDTO dto = new MeetingCreateDTO();
        dto.setTenantId(0L);
        dto.setOrgId(1L);
        dto.setTitle("董事会议");
        dto.setScheduledAt(LocalDateTime.of(2024, 6, 15, 9, 0));
        dto.setLocation("会议室A");

        when(meetingMapper.insert(any(DecisionMeeting.class))).thenReturn(1);

        // when
        DecisionMeeting result = decisionService.createMeeting(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMeetingNo()).startsWith("MT-");
        assertThat(result.getStatus()).isEqualTo("SCHEDULED");

        ArgumentCaptor<DecisionMeeting> captor = ArgumentCaptor.forClass(DecisionMeeting.class);
        verify(meetingMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("SCHEDULED");
    }

    @Test
    void shouldCreateResolution() {
        // given
        DecisionMeeting meeting = new DecisionMeeting();
        meeting.setId(1L);
        meeting.setTenantId(0L);
        meeting.setMeetingNo("MT-123456");
        meeting.setStatus("SCHEDULED");

        ResolutionCreateDTO dto = new ResolutionCreateDTO();
        dto.setTitle("关于投资项目的决议");
        dto.setContent("批准投资项目");

        when(meetingMapper.selectById(1L)).thenReturn(meeting);
        when(resolutionMapper.insert(any(DecisionResolution.class))).thenReturn(1);

        // when
        DecisionResolution result = decisionService.createResolution(1L, dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getResolutionNo()).startsWith("RES-");
        assertThat(result.getStatus()).isEqualTo("APPROVED");
        assertThat(result.getMeetingId()).isEqualTo(1L);
        assertThat(result.getTenantId()).isEqualTo(0L);
    }

    @Test
    void shouldThrowWhenMeetingNotFound() {
        // given
        ResolutionCreateDTO dto = new ResolutionCreateDTO();
        dto.setTitle("决议");
        dto.setContent("内容");

        when(meetingMapper.selectById(anyLong())).thenReturn(null);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> decisionService.createResolution(999L, dto));
        assertThat(ex.getMessage()).contains("会议不存在");
    }

    @Test
    void shouldQueryPendingSupervisions() {
        // given
        DecisionSupervision sup1 = new DecisionSupervision();
        sup1.setId(1L);
        sup1.setTenantId(0L);
        sup1.setStatus("PENDING");

        DecisionSupervision sup2 = new DecisionSupervision();
        sup2.setId(2L);
        sup2.setTenantId(0L);
        sup2.setStatus("PENDING");

        when(supervisionMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(sup1, sup2));

        // when
        List<DecisionSupervision> result = decisionService.queryPendingSupervisions(0L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(s -> "PENDING".equals(s.getStatus()));
    }

    @Test
    void shouldQueryItems() {
        // given
        Page<DecisionItem> expectedPage = new Page<>(1, 20);
        when(itemMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        // when
        Page<DecisionItem> result = decisionService.queryItems(0L, "INVESTMENT", "DRAFT", 1, 20);

        // then
        assertThat(result).isNotNull();
        verify(itemMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldCreateSupervision() {
        // given
        DecisionResolution resolution = new DecisionResolution();
        resolution.setId(1L);
        resolution.setResolutionNo("RES-123");

        SupervisionCreateDTO dto = new SupervisionCreateDTO();
        dto.setTenantId(0L);
        dto.setTaskTitle("跟进投资");

        when(resolutionMapper.selectById(1L)).thenReturn(resolution);
        when(supervisionMapper.insert(any(DecisionSupervision.class))).thenReturn(1);

        // when
        DecisionSupervision result = decisionService.createSupervision(1L, dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getResolutionId()).isEqualTo(1L);
    }
}
