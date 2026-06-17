package com.sasac.platform.majorevent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.majorevent.dto.EventDTO;
import com.sasac.platform.majorevent.dto.GuaranteeDTO;
import com.sasac.platform.majorevent.entity.MeEvent;
import com.sasac.platform.majorevent.entity.MeGuarantee;
import com.sasac.platform.majorevent.mapper.MeEventMapper;
import com.sasac.platform.majorevent.mapper.MeGuaranteeMapper;
import com.sasac.platform.majorevent.mapper.MeLawsuitMapper;
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
class MeMajorEventServiceTest {

    @Mock
    private MeEventMapper meEventMapper;

    @Mock
    private MeLawsuitMapper meLawsuitMapper;

    @Mock
    private MeGuaranteeMapper meGuaranteeMapper;

    @InjectMocks
    private MeMajorEventService service;

    @Captor
    private ArgumentCaptor<MeEvent> eventCaptor;

    @Captor
    private ArgumentCaptor<MeGuarantee> guaranteeCaptor;

    @Test
    void shouldReportEvent() {
        EventDTO dto = new EventDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setEventType("ACCIDENT");
        dto.setTitle("生产安全事故");
        dto.setDescription("车间发生火灾");
        dto.setImpactAssessment("造成3人受伤");
        dto.setHandlingPlan("立即启动应急预案");

        when(meEventMapper.insert(any(MeEvent.class))).thenReturn(1);

        MeEvent result = service.report(dto);

        assertThat(result).isNotNull();
        assertThat(result.getEventNo()).startsWith("ZDSX-");
        assertThat(result.getStatus()).isEqualTo("REPORTED");
        assertThat(result.getTitle()).isEqualTo("生产安全事故");
        assertThat(result.getReportedAt()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();

        verify(meEventMapper).insert(eventCaptor.capture());
        MeEvent captured = eventCaptor.getValue();
        assertThat(captured.getEventNo()).startsWith("ZDSX-");
        assertThat(captured.getStatus()).isEqualTo("REPORTED");
        assertThat(captured.getTitle()).isEqualTo("生产安全事故");
    }

    @Test
    void shouldApproveEvent() {
        MeEvent event = new MeEvent();
        event.setId(1L);
        event.setTenantId(1L);
        event.setOrgId(10L);
        event.setEventNo("ZDSX-20240101-1234");
        event.setTitle("生产安全事故");
        event.setStatus("REPORTED");

        when(meEventMapper.selectById(1L)).thenReturn(event);
        when(meEventMapper.updateById(any(MeEvent.class))).thenReturn(1);
        when(meEventMapper.selectById(1L)).thenReturn(event);

        MeEvent result = service.approve(1L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(meEventMapper).updateById(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void shouldResolveEvent() {
        MeEvent event = new MeEvent();
        event.setId(1L);
        event.setTenantId(1L);
        event.setOrgId(10L);
        event.setEventNo("ZDSX-20240101-5678");
        event.setTitle("环保事件");
        event.setStatus("APPROVED");

        when(meEventMapper.selectById(1L)).thenReturn(event);
        when(meEventMapper.updateById(any(MeEvent.class))).thenReturn(1);
        when(meEventMapper.selectById(1L)).thenReturn(event);

        MeEvent result = service.resolve(1L);

        assertThat(result.getStatus()).isEqualTo("RESOLVED");
        assertThat(result.getResolvedAt()).isNotNull();

        verify(meEventMapper).updateById(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getStatus()).isEqualTo("RESOLVED");
    }

    @Test
    void shouldThrowWhenEventNotFound() {
        when(meEventMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getEvent(999L));
        assertThat(ex.getMessage()).isEqualTo("重大事件不存在");
    }

    @Test
    void shouldThrowWhenApproveNonExistentEvent() {
        when(meEventMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.approve(999L));
        assertThat(ex.getMessage()).isEqualTo("重大事件不存在");
    }

    @Test
    void shouldTrackEventProgress() {
        MeEvent event = new MeEvent();
        event.setId(1L);
        event.setTenantId(1L);
        event.setOrgId(10L);
        event.setEventNo("ZDSX-20240101-9012");
        event.setTitle("质量事故");
        event.setHandlingPlan("已安排人员调查");
        event.setStatus("APPROVED");

        when(meEventMapper.selectById(1L)).thenReturn(event);
        when(meEventMapper.updateById(any(MeEvent.class))).thenReturn(1);
        when(meEventMapper.selectById(1L)).thenReturn(event);

        MeEvent result = service.track(1L, "已完成现场勘察");

        assertThat(result.getHandlingPlan()).contains("已完成现场勘察");
    }

    @Test
    void shouldFindExpiringGuarantees() {
        MeGuarantee guarantee = new MeGuarantee();
        guarantee.setId(1L);
        guarantee.setTenantId(1L);
        guarantee.setEventId(100L);
        guarantee.setGuaranteeType("LOAN_GUARANTEE");
        guarantee.setBeneficiary("供应商A");
        guarantee.setGuaranteeAmount(new BigDecimal("500000"));
        guarantee.setGuaranteePeriodStart(LocalDate.now().minusMonths(6));
        guarantee.setGuaranteePeriodEnd(LocalDate.now().plusDays(15));
        guarantee.setRiskLevel("MEDIUM");
        guarantee.setStatus("ACTIVE");

        when(meGuaranteeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(guarantee));

        List<MeGuarantee> results = service.findExpiringGuarantees(1L, 30);

        assertThat(results).hasSize(1);
        MeGuarantee result = results.get(0);
        assertThat(result.getBeneficiary()).isEqualTo("供应商A");
        assertThat(result.getRiskLevel()).isEqualTo("MEDIUM");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldReturnEmptyWhenNoExpiringGuarantees() {
        when(meGuaranteeMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<MeGuarantee> results = service.findExpiringGuarantees(1L, 30);

        assertThat(results).isEmpty();
    }

    @Test
    void shouldDeleteEvent() {
        MeEvent event = new MeEvent();
        event.setId(1L);
        event.setTenantId(1L);

        when(meEventMapper.selectById(1L)).thenReturn(event);
        when(meEventMapper.deleteById(1L)).thenReturn(1);

        service.deleteEvent(1L);

        verify(meEventMapper).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentEvent() {
        when(meEventMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deleteEvent(999L));
        assertThat(ex.getMessage()).isEqualTo("重大事件不存在");
    }
}
