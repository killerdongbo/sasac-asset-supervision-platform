package com.sasac.platform.performance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.performance.dto.IndicatorDefDTO;
import com.sasac.platform.performance.dto.SalaryBudgetDTO;
import com.sasac.platform.performance.entity.PerfIndicatorDef;
import com.sasac.platform.performance.entity.PerfSalaryBudget;
import com.sasac.platform.performance.mapper.PerfIncentiveMapper;
import com.sasac.platform.performance.mapper.PerfIndicatorDefMapper;
import com.sasac.platform.performance.mapper.PerfSalaryBudgetMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfPerformanceServiceTest {

    @Mock
    private PerfIndicatorDefMapper indicatorDefMapper;

    @Mock
    private PerfSalaryBudgetMapper salaryBudgetMapper;

    @Mock
    private PerfIncentiveMapper incentiveMapper;

    @InjectMocks
    private PerfPerformanceService service;

    @Captor
    private ArgumentCaptor<PerfIndicatorDef> indicatorCaptor;

    @Captor
    private ArgumentCaptor<PerfSalaryBudget> budgetCaptor;

    @Test
    void shouldDefineIndicators() {
        IndicatorDefDTO dto1 = new IndicatorDefDTO();
        dto1.setTenantId(1L);
        dto1.setOrgId(10L);
        dto1.setCycleYear(2024);
        dto1.setIndicatorCode("REVENUE");
        dto1.setIndicatorName("营业收入");
        dto1.setWeight(new BigDecimal("60"));
        dto1.setTargetValue(new BigDecimal("1000000"));

        IndicatorDefDTO dto2 = new IndicatorDefDTO();
        dto2.setTenantId(1L);
        dto2.setOrgId(10L);
        dto2.setCycleYear(2024);
        dto2.setIndicatorCode("PROFIT");
        dto2.setIndicatorName("净利润");
        dto2.setWeight(new BigDecimal("40"));
        dto2.setTargetValue(new BigDecimal("500000"));

        when(indicatorDefMapper.insert(any(PerfIndicatorDef.class))).thenReturn(1);

        PerfIndicatorDef saved1 = new PerfIndicatorDef();
        saved1.setIndicatorCode("REVENUE");
        saved1.setIndicatorName("营业收入");

        PerfIndicatorDef saved2 = new PerfIndicatorDef();
        saved2.setIndicatorCode("PROFIT");
        saved2.setIndicatorName("净利润");

        when(indicatorDefMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(saved1, saved2));

        List<PerfIndicatorDef> results = service.defineIndicators(List.of(dto1, dto2));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getIndicatorCode()).isEqualTo("REVENUE");
        assertThat(results.get(1).getIndicatorCode()).isEqualTo("PROFIT");

        verify(indicatorDefMapper).insert(indicatorCaptor.capture());
        PerfIndicatorDef captured = indicatorCaptor.getValue();
        assertThat(captured.getIndicatorCode()).isEqualTo("REVENUE");
    }

    @Test
    void shouldThrowWhenIndicatorListEmpty() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.defineIndicators(Collections.emptyList()));
        assertThat(ex.getMessage()).isEqualTo("指标数据不能为空");
    }

    @Test
    void shouldThrowWhenIndicatorListNull() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.defineIndicators(null));
        assertThat(ex.getMessage()).isEqualTo("指标数据不能为空");
    }

    @Test
    void shouldRejectWeightSumNot100() {
        IndicatorDefDTO dto1 = new IndicatorDefDTO();
        dto1.setTenantId(1L);
        dto1.setOrgId(10L);
        dto1.setCycleYear(2024);
        dto1.setIndicatorCode("REVENUE");
        dto1.setWeight(new BigDecimal("50"));
        dto1.setTargetValue(new BigDecimal("1000000"));

        IndicatorDefDTO dto2 = new IndicatorDefDTO();
        dto2.setTenantId(1L);
        dto2.setOrgId(10L);
        dto2.setCycleYear(2024);
        dto2.setIndicatorCode("PROFIT");
        dto2.setWeight(new BigDecimal("30"));
        dto2.setTargetValue(new BigDecimal("500000"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.defineIndicators(List.of(dto1, dto2)));
        assertThat(ex.getMessage()).isEqualTo("指标权重之和必须为100%");
    }

    @Test
    void shouldCalculateScores() {
        PerfIndicatorDef indicator = new PerfIndicatorDef();
        indicator.setId(1L);
        indicator.setTenantId(1L);
        indicator.setOrgId(10L);
        indicator.setCycleYear(2024);
        indicator.setIndicatorCode("REVENUE");
        indicator.setIndicatorName("营业收入");
        indicator.setWeight(new BigDecimal("50"));
        indicator.setTargetValue(new BigDecimal("100"));
        indicator.setActualValue(new BigDecimal("90"));

        when(indicatorDefMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(indicator))
                .thenReturn(List.of(indicator));
        when(indicatorDefMapper.updateById(any(PerfIndicatorDef.class))).thenReturn(1);

        List<PerfIndicatorDef> results = service.calculateScores(1L, 10L, 2024);

        assertThat(results).hasSize(1);
        PerfIndicatorDef result = results.get(0);
        // score = min(100, 90/100 * 100) * 50 / 100 = 90 * 50 / 100 = 45.0
        assertThat(result.getScore()).isEqualByComparingTo(new BigDecimal("45.0"));

        verify(indicatorDefMapper).updateById(indicatorCaptor.capture());
        assertThat(indicatorCaptor.getValue().getScore())
                .isEqualByComparingTo(new BigDecimal("45.0"));
    }

    @Test
    void shouldHandleNullActualInScoreCalculation() {
        PerfIndicatorDef indicator = new PerfIndicatorDef();
        indicator.setId(1L);
        indicator.setTenantId(1L);
        indicator.setOrgId(10L);
        indicator.setCycleYear(2024);
        indicator.setWeight(new BigDecimal("50"));
        indicator.setTargetValue(new BigDecimal("100"));
        indicator.setActualValue(null);

        when(indicatorDefMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(indicator))
                .thenReturn(List.of(indicator));
        when(indicatorDefMapper.updateById(any(PerfIndicatorDef.class))).thenReturn(1);

        List<PerfIndicatorDef> results = service.calculateScores(1L, 10L, 2024);

        assertThat(results.get(0).getScore()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldRejectWeightSumWhenDefiningIndicators() {
        IndicatorDefDTO dto = new IndicatorDefDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setCycleYear(2024);
        dto.setIndicatorCode("REVENUE");
        dto.setWeight(new BigDecimal("100"));
        dto.setTargetValue(new BigDecimal("1000000"));

        when(indicatorDefMapper.insert(any(PerfIndicatorDef.class))).thenReturn(1);

        PerfIndicatorDef saved = new PerfIndicatorDef();
        saved.setIndicatorCode("REVENUE");
        saved.setIndicatorName("营业收入");

        when(indicatorDefMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(saved));

        List<PerfIndicatorDef> results = service.defineIndicators(List.of(dto));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIndicatorCode()).isEqualTo("REVENUE");
    }

    @Test
    void shouldAdjustSalaryBudget() {
        PerfSalaryBudget existingBudget = new PerfSalaryBudget();
        existingBudget.setId(1L);
        existingBudget.setTenantId(1L);
        existingBudget.setOrgId(10L);
        existingBudget.setBudgetYear(2024);
        existingBudget.setTotalBudget(new BigDecimal("1000000"));
        existingBudget.setApprovedBudget(new BigDecimal("800000"));
        existingBudget.setStatus("APPROVED");

        when(salaryBudgetMapper.selectById(1L)).thenReturn(existingBudget);
        when(salaryBudgetMapper.updateById(any(PerfSalaryBudget.class))).thenReturn(1);
        when(salaryBudgetMapper.selectById(1L)).thenReturn(existingBudget);

        PerfSalaryBudget result = service.adjustBudget(1L, new BigDecimal("100000"), "绩效奖金调整");

        assertThat(result.getApprovedBudget()).isEqualByComparingTo(new BigDecimal("900000"));
        assertThat(result.getAdjustmentReason()).isEqualTo("绩效奖金调整");
        assertThat(result.getStatus()).isEqualTo("ADJUSTED");

        verify(salaryBudgetMapper).updateById(budgetCaptor.capture());
        PerfSalaryBudget captured = budgetCaptor.getValue();
        assertThat(captured.getApprovedBudget()).isEqualByComparingTo(new BigDecimal("900000"));
        assertThat(captured.getAdjustmentReason()).isEqualTo("绩效奖金调整");
        assertThat(captured.getStatus()).isEqualTo("ADJUSTED");
    }

    @Test
    void shouldThrowWhenSalaryBudgetNotFound() {
        when(salaryBudgetMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.adjustBudget(999L, new BigDecimal("50000"), "调整"));
        assertThat(ex.getMessage()).isEqualTo("薪酬预算记录不存在");
    }
}
