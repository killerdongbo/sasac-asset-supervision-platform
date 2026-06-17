package com.sasac.platform.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.property.dto.AssessmentDTO;
import com.sasac.platform.property.dto.RegistrationDTO;
import com.sasac.platform.property.dto.TransactionDTO;
import com.sasac.platform.property.entity.PrAssessment;
import com.sasac.platform.property.entity.PrRegistration;
import com.sasac.platform.property.entity.PrTransactionMonitor;
import com.sasac.platform.property.mapper.PrAssessmentMapper;
import com.sasac.platform.property.mapper.PrChangeMapper;
import com.sasac.platform.property.mapper.PrRegistrationMapper;
import com.sasac.platform.property.mapper.PrTransactionMonitorMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrPropertyServiceTest {

    @Mock
    private PrRegistrationMapper prRegistrationMapper;

    @Mock
    private PrChangeMapper prChangeMapper;

    @Mock
    private PrAssessmentMapper prAssessmentMapper;

    @Mock
    private PrTransactionMonitorMapper prTransactionMonitorMapper;

    @InjectMocks
    private PrPropertyService service;

    @Captor
    private ArgumentCaptor<PrRegistration> registrationCaptor;

    @Captor
    private ArgumentCaptor<PrAssessment> assessmentCaptor;

    @Captor
    private ArgumentCaptor<PrTransactionMonitor> monitorCaptor;

    @Test
    void shouldRegisterProperty() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setRegType("INITIAL");
        dto.setEnterpriseName("测试企业");
        dto.setPropertyType("STATE_OWNED");
        dto.setEquityPct(new BigDecimal("60.00"));
        dto.setRegisteredCapital(new BigDecimal("1000000"));

        when(prRegistrationMapper.insert(any(PrRegistration.class))).thenReturn(1);

        PrRegistration result = service.register(dto);

        assertThat(result).isNotNull();
        assertThat(result.getRegNo()).startsWith("CQ-");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getEnterpriseName()).isEqualTo("测试企业");
        assertThat(result.getTenantId()).isEqualTo(1L);
        assertThat(result.getOrgId()).isEqualTo(10L);

        verify(prRegistrationMapper).insert(registrationCaptor.capture());
        PrRegistration captured = registrationCaptor.getValue();
        assertThat(captured.getRegNo()).startsWith("CQ-");
        assertThat(captured.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldThrowWhenRegistrationNotFound() {
        when(prRegistrationMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getRegistrationById(999L));
        assertThat(ex.getMessage()).isEqualTo("产权登记记录不存在");
    }

    @Test
    void shouldCalculateAssessmentDeviation() {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setTenantId(1L);
        dto.setAssessNo("PG-2024-001");
        dto.setAssessPurpose("资产重组");
        dto.setBookValue(new BigDecimal("100"));
        dto.setAssessedValue(new BigDecimal("85"));

        when(prAssessmentMapper.insert(any(PrAssessment.class))).thenReturn(1);

        PrAssessment result = service.assess(dto);

        assertThat(result).isNotNull();
        assertThat(result.getDeviationPct()).isEqualByComparingTo(new BigDecimal("15.00"));
        assertThat(result.getApprovalStatus()).isEqualTo("PENDING");

        verify(prAssessmentMapper).insert(assessmentCaptor.capture());
        assertThat(assessmentCaptor.getValue().getDeviationPct())
                .isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void shouldHandleZeroBookValueInAssessment() {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setTenantId(1L);
        dto.setAssessNo("PG-2024-002");
        dto.setBookValue(BigDecimal.ZERO);
        dto.setAssessedValue(new BigDecimal("100"));

        when(prAssessmentMapper.insert(any(PrAssessment.class))).thenReturn(1);

        PrAssessment result = service.assess(dto);

        assertThat(result.getDeviationPct()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldMarkAbnormalTransaction() {
        TransactionDTO dto = new TransactionDTO();
        dto.setTenantId(1L);
        dto.setListingPrice(new BigDecimal("100"));
        dto.setTransactionPrice(new BigDecimal("80"));
        dto.setProjectName("产权转让项目");

        when(prTransactionMonitorMapper.insert(any(PrTransactionMonitor.class))).thenReturn(1);

        PrTransactionMonitor result = service.monitorTransaction(dto);

        assertThat(result).isNotNull();
        assertThat(result.getIsAbnormal()).isTrue();
        assertThat(result.getPriceDeviationPct())
                .isEqualByComparingTo(new BigDecimal("20.00"));

        verify(prTransactionMonitorMapper).insert(monitorCaptor.capture());
        assertThat(monitorCaptor.getValue().getIsAbnormal()).isTrue();
    }

    @Test
    void shouldNotMarkNormalTransaction() {
        TransactionDTO dto = new TransactionDTO();
        dto.setTenantId(1L);
        dto.setListingPrice(new BigDecimal("100"));
        dto.setTransactionPrice(new BigDecimal("95"));
        dto.setProjectName("正常转让项目");

        when(prTransactionMonitorMapper.insert(any(PrTransactionMonitor.class))).thenReturn(1);

        PrTransactionMonitor result = service.monitorTransaction(dto);

        assertThat(result.getIsAbnormal()).isFalse();
        assertThat(result.getPriceDeviationPct())
                .isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void shouldBuildPropertyTree() {
        PrRegistration reg1 = new PrRegistration();
        reg1.setId(1L);
        reg1.setOrgId(10L);
        reg1.setEnterpriseName("企业A");
        reg1.setRegNo("CQ-20240101-12345");
        reg1.setRegType("INITIAL");
        reg1.setEquityPct(new BigDecimal("60"));

        PrRegistration reg2 = new PrRegistration();
        reg2.setId(2L);
        reg2.setOrgId(10L);
        reg2.setEnterpriseName("企业B");
        reg2.setRegNo("CQ-20240102-67890");
        reg2.setRegType("CHANGE");
        reg2.setEquityPct(new BigDecimal("40"));

        PrRegistration reg3 = new PrRegistration();
        reg3.setId(3L);
        reg3.setOrgId(20L);
        reg3.setEnterpriseName("企业C");
        reg3.setRegNo("CQ-20240103-11111");
        reg3.setRegType("INITIAL");
        reg3.setEquityPct(new BigDecimal("100"));

        when(prRegistrationMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(reg1, reg2, reg3));

        List<Map<String, Object>> tree = service.buildPropertyTree(1L);

        assertThat(tree).hasSize(2);

        Map<String, Object> org10Node = tree.get(0);
        assertThat(org10Node.get("orgId")).isEqualTo(10L);
        assertThat(org10Node.get("label")).isEqualTo("组织-10");

        List<Map<String, Object>> org10Children = (List<Map<String, Object>>) org10Node.get("children");
        assertThat(org10Children).hasSize(2);
        assertThat(org10Children.get(0).get("label")).isEqualTo("企业A");
        assertThat(org10Children.get(1).get("label")).isEqualTo("企业B");

        Map<String, Object> org20Node = tree.get(1);
        assertThat(org20Node.get("orgId")).isEqualTo(20L);

        List<Map<String, Object>> org20Children = (List<Map<String, Object>>) org20Node.get("children");
        assertThat(org20Children).hasSize(1);
        assertThat(org20Children.get(0).get("label")).isEqualTo("企业C");
    }

    @Test
    void shouldBuildEmptyTreeWhenNoRegistrations() {
        when(prRegistrationMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<Map<String, Object>> tree = service.buildPropertyTree(1L);

        assertThat(tree).isEmpty();
    }
}
