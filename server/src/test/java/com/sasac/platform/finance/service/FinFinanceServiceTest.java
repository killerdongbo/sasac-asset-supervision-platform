package com.sasac.platform.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.finance.dto.BudgetDTO;
import com.sasac.platform.finance.dto.FundMonitorDTO;
import com.sasac.platform.finance.dto.ReportDTO;
import com.sasac.platform.finance.entity.FinBudget;
import com.sasac.platform.finance.entity.FinFundMonitor;
import com.sasac.platform.finance.entity.FinReport;
import com.sasac.platform.finance.mapper.FinBudgetMapper;
import com.sasac.platform.finance.mapper.FinFundMonitorMapper;
import com.sasac.platform.finance.mapper.FinIndicatorMapper;
import com.sasac.platform.finance.mapper.FinReportMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinFinanceServiceTest {

    @Mock
    private FinReportMapper reportMapper;

    @Mock
    private FinIndicatorMapper indicatorMapper;

    @Mock
    private FinFundMonitorMapper fundMonitorMapper;

    @Mock
    private FinBudgetMapper budgetMapper;

    @InjectMocks
    private FinFinanceService service;

    @Captor
    private ArgumentCaptor<FinReport> reportCaptor;

    @Captor
    private ArgumentCaptor<FinFundMonitor> fundMonitorCaptor;

    @Captor
    private ArgumentCaptor<FinBudget> budgetCaptor;

    @Test
    void shouldSubmitReport() {
        ReportDTO dto = new ReportDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setReportType("BALANCE_SHEET");
        dto.setReportYear(2024);
        dto.setReportPeriod(12);
        dto.setReportData("{\"totalAssets\":\"1000000\"}");

        when(reportMapper.insert(any(FinReport.class))).thenReturn(1);

        FinReport result = service.submitReport(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("SUBMITTED");
        assertThat(result.getSubmittedAt()).isNotNull();
        assertThat(result.getReportData()).isEqualTo("{\"totalAssets\":\"1000000\"}");
        assertThat(result.getReportType()).isEqualTo("BALANCE_SHEET");

        verify(reportMapper).insert(reportCaptor.capture());
        FinReport captured = reportCaptor.getValue();
        assertThat(captured.getStatus()).isEqualTo("SUBMITTED");
        assertThat(captured.getReportData()).isEqualTo("{\"totalAssets\":\"1000000\"}");
    }

    @Test
    void shouldThrowWhenReportNotFound() {
        when(reportMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getReport(999L));
        assertThat(ex.getMessage()).isEqualTo("财务报表不存在");
    }

    @Test
    void shouldFlagLargeFund() {
        FundMonitorDTO dto = new FundMonitorDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setAmount(new BigDecimal("5000000"));
        dto.setTransactionDate(LocalDateTime.now());
        dto.setTransactionNo("TX-001");
        dto.setPayer("企业A");
        dto.setPayee("企业B");

        when(fundMonitorMapper.insert(any(FinFundMonitor.class))).thenReturn(1);

        FinFundMonitor result = service.monitorFund(dto);

        assertThat(result).isNotNull();
        assertThat(result.getIsFlagged()).isTrue();
        assertThat(result.getFlagReason()).contains("单日累计金额超过500万元");

        verify(fundMonitorMapper).insert(fundMonitorCaptor.capture());
        FinFundMonitor captured = fundMonitorCaptor.getValue();
        assertThat(captured.getIsFlagged()).isTrue();
    }

    @Test
    void shouldFlagSingleTransactionOverOneMillion() {
        FundMonitorDTO dto = new FundMonitorDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setAmount(new BigDecimal("1500000"));
        dto.setTransactionDate(LocalDateTime.now());
        dto.setTransactionNo("TX-002");

        when(fundMonitorMapper.insert(any(FinFundMonitor.class))).thenReturn(1);

        FinFundMonitor result = service.monitorFund(dto);

        assertThat(result.getIsFlagged()).isTrue();
        assertThat(result.getFlagReason()).isEqualTo("单笔金额超过100万元");
    }

    @Test
    void shouldNotFlagSmallFund() {
        FundMonitorDTO dto = new FundMonitorDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setAmount(new BigDecimal("50000"));
        dto.setTransactionDate(LocalDateTime.now());
        dto.setTransactionNo("TX-003");

        when(fundMonitorMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        when(fundMonitorMapper.insert(any(FinFundMonitor.class))).thenReturn(1);

        FinFundMonitor result = service.monitorFund(dto);

        assertThat(result.getIsFlagged()).isFalse();
        assertThat(result.getFlagReason()).isNull();
    }

    @Test
    void shouldCalculateBudgetExecution() {
        FinBudget budget = new FinBudget();
        budget.setId(1L);
        budget.setTenantId(1L);
        budget.setOrgId(10L);
        budget.setBudgetYear(2024);
        budget.setBudgetType("OPERATING");
        budget.setPlannedAmount(new BigDecimal("100"));
        budget.setActualAmount(new BigDecimal("85"));

        when(budgetMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(budget))
                .thenReturn(List.of(budget));
        when(budgetMapper.updateById(any(FinBudget.class))).thenReturn(1);

        List<FinBudget> results = service.checkBudgetExecution(1L, 2024);

        assertThat(results).hasSize(1);
        FinBudget result = results.get(0);
        assertThat(result.getExecutedPct()).isEqualByComparingTo(new BigDecimal("85.00"));

        verify(budgetMapper).updateById(budgetCaptor.capture());
        assertThat(budgetCaptor.getValue().getExecutedPct())
                .isEqualByComparingTo(new BigDecimal("85.00"));
    }

    @Test
    void shouldHandleEmptyBudgets() {
        when(budgetMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        List<FinBudget> results = service.checkBudgetExecution(1L, 2024);

        assertThat(results).isEmpty();
    }

    @Test
    void shouldCreateBudget() {
        BudgetDTO dto = new BudgetDTO();
        dto.setTenantId(1L);
        dto.setOrgId(10L);
        dto.setBudgetYear(2024);
        dto.setBudgetType("OPERATING");
        dto.setPlannedAmount(new BigDecimal("500000"));

        when(budgetMapper.insert(any(FinBudget.class))).thenReturn(1);

        FinBudget result = service.createBudget(dto);

        assertThat(result).isNotNull();
        assertThat(result.getPlannedAmount()).isEqualByComparingTo(new BigDecimal("500000"));
        assertThat(result.getActualAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getExecutedPct()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
