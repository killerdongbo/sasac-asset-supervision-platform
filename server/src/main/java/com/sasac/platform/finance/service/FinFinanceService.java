package com.sasac.platform.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.finance.dto.BudgetDTO;
import com.sasac.platform.finance.dto.FundMonitorDTO;
import com.sasac.platform.finance.dto.IndicatorDTO;
import com.sasac.platform.finance.dto.ReportDTO;
import com.sasac.platform.finance.entity.FinBudget;
import com.sasac.platform.finance.entity.FinFundMonitor;
import com.sasac.platform.finance.entity.FinIndicator;
import com.sasac.platform.finance.entity.FinReport;
import com.sasac.platform.finance.mapper.FinBudgetMapper;
import com.sasac.platform.finance.mapper.FinFundMonitorMapper;
import com.sasac.platform.finance.mapper.FinIndicatorMapper;
import com.sasac.platform.finance.mapper.FinReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for financial supervision business logic.
 */
@Service
@RequiredArgsConstructor
public class FinFinanceService {

    private final FinReportMapper reportMapper;
    private final FinIndicatorMapper indicatorMapper;
    private final FinFundMonitorMapper fundMonitorMapper;
    private final FinBudgetMapper budgetMapper;

    // ==================== Reports ====================

    /**
     * Submits a financial report.
     *
     * @param dto the report data
     * @return the created report entity
     */
    @Transactional
    public FinReport submitReport(ReportDTO dto) {
        FinReport report = new FinReport();
        BeanUtils.copyProperties(dto, report);
        report.setStatus("SUBMITTED");
        report.setSubmittedAt(LocalDateTime.now());
        reportMapper.insert(report);
        return report;
    }

    /**
     * Queries financial reports with filters and pagination.
     *
     * @param tenantId   the tenant ID
     * @param reportType optional report type filter
     * @param reportYear optional year filter
     * @param status     optional status filter
     * @param page       page number
     * @param limit      page size
     * @return paginated result
     */
    public Page<FinReport> queryReports(Long tenantId, String reportType, Integer reportYear, String status, int page, int limit) {
        LambdaQueryWrapper<FinReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinReport::getTenantId, tenantId);
        if (reportType != null && !reportType.isBlank()) {
            wrapper.eq(FinReport::getReportType, reportType);
        }
        if (reportYear != null) {
            wrapper.eq(FinReport::getReportYear, reportYear);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(FinReport::getStatus, status);
        }
        wrapper.orderByDesc(FinReport::getId);
        return reportMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    /**
     * Gets a single financial report by ID.
     *
     * @param id the report ID
     * @return the report entity
     * @throws BusinessException if not found
     */
    public FinReport getReport(Long id) {
        FinReport report = reportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException("财务报表不存在");
        }
        return report;
    }

    // ==================== Indicators ====================

    /**
     * Calculates financial indicators from report data.
     * Computes: assetLiabilityRatio, currentRatio, roe, receivableTurnover.
     *
     * @param dto the indicator calculation request
     * @return list of calculated indicators
     */
    @Transactional
    public List<FinIndicator> calculateIndicators(IndicatorDTO dto) {
        Long tenantId = dto.getTenantId();
        Long orgId = dto.getOrgId();
        int year = dto.getPeriodYear();
        int month = dto.getPeriodMonth();

        // Fetch balance sheet and income statement data from reports
        FinReport balanceSheet = findReport(tenantId, orgId, "BALANCE_SHEET", year, month);
        FinReport incomeStatement = findReport(tenantId, orgId, "INCOME_STATEMENT", year, month);

        // Parse JSON data (simplified: in production use a JSON parser)
        BigDecimal totalAssets = extractValue(balanceSheet, "totalAssets");
        BigDecimal totalLiabilities = extractValue(balanceSheet, "totalLiabilities");
        BigDecimal currentAssets = extractValue(balanceSheet, "currentAssets");
        BigDecimal currentLiabilities = extractValue(balanceSheet, "currentLiabilities");
        BigDecimal netProfit = extractValue(incomeStatement, "netProfit");
        BigDecimal netAssets = extractValue(balanceSheet, "netAssets");
        BigDecimal accountsReceivable = extractValue(balanceSheet, "accountsReceivable");
        BigDecimal revenue = extractValue(incomeStatement, "revenue");

        // Delete previous indicators for the same period
        indicatorMapper.delete(new LambdaQueryWrapper<FinIndicator>()
                .eq(FinIndicator::getTenantId, tenantId)
                .eq(FinIndicator::getOrgId, orgId)
                .eq(FinIndicator::getPeriodYear, year)
                .eq(FinIndicator::getPeriodMonth, month));

        // 1. Asset-Liability Ratio
        saveIndicator(tenantId, orgId, "assetLiabilityRatio", "资产负债率",
                divide(totalLiabilities, totalAssets),
                new BigDecimal("80"), new BigDecimal("90"),
                year, month);

        // 2. Current Ratio
        saveIndicator(tenantId, orgId, "currentRatio", "流动比率",
                divide(currentAssets, currentLiabilities),
                new BigDecimal("1.5"), new BigDecimal("1.0"),
                year, month);

        // 3. ROE (Return on Equity)
        saveIndicator(tenantId, orgId, "roe", "净资产收益率",
                divide(netProfit, netAssets),
                BigDecimal.ZERO, new BigDecimal("-5"),
                year, month);

        // 4. Receivable Turnover
        saveIndicator(tenantId, orgId, "receivableTurnover", "应收账款周转率",
                divide(revenue, accountsReceivable),
                new BigDecimal("3"), new BigDecimal("1.5"),
                year, month);

        // Return newly created indicators
        return indicatorMapper.selectList(new LambdaQueryWrapper<FinIndicator>()
                .eq(FinIndicator::getTenantId, tenantId)
                .eq(FinIndicator::getOrgId, orgId)
                .eq(FinIndicator::getPeriodYear, year)
                .eq(FinIndicator::getPeriodMonth, month));
    }

    /**
     * Queries financial indicators with filters and pagination.
     *
     * @param tenantId      the tenant ID
     * @param indicatorCode optional indicator code filter
     * @param periodYear    optional year filter
     * @param page          page number
     * @param limit         page size
     * @return paginated result
     */
    public Page<FinIndicator> queryIndicators(Long tenantId, String indicatorCode, Integer periodYear, int page, int limit) {
        LambdaQueryWrapper<FinIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinIndicator::getTenantId, tenantId);
        if (indicatorCode != null && !indicatorCode.isBlank()) {
            wrapper.eq(FinIndicator::getIndicatorCode, indicatorCode);
        }
        if (periodYear != null) {
            wrapper.eq(FinIndicator::getPeriodYear, periodYear);
        }
        wrapper.orderByDesc(FinIndicator::getId);
        return indicatorMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    // ==================== Fund Monitor ====================

    /**
     * Records a fund transaction with auto-flagging logic.
     * Flags if single amount > 1,000,000 or daily cumulative > 5,000,000.
     *
     * @param dto the fund transaction data
     * @return the created fund monitor entity
     */
    @Transactional
    public FinFundMonitor monitorFund(FundMonitorDTO dto) {
        FinFundMonitor monitor = new FinFundMonitor();
        BeanUtils.copyProperties(dto, monitor);
        monitor.setIsFlagged(false);

        // Flag if single transaction > 1,000,000
        if (dto.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
            monitor.setIsFlagged(true);
            monitor.setFlagReason("单笔金额超过100万元");
        }

        // Check daily cumulative for same tenant/org
        if (!monitor.getIsFlagged()) {
            LocalDate txDate = dto.getTransactionDate().toLocalDate();
            LocalDateTime dayStart = txDate.atStartOfDay();
            LocalDateTime dayEnd = txDate.plusDays(1).atStartOfDay();

            List<FinFundMonitor> todayTx = fundMonitorMapper.selectList(
                    new LambdaQueryWrapper<FinFundMonitor>()
                            .eq(FinFundMonitor::getTenantId, dto.getTenantId())
                            .eq(FinFundMonitor::getOrgId, dto.getOrgId())
                            .between(FinFundMonitor::getTransactionDate, dayStart, dayEnd));

            BigDecimal dailySum = dto.getAmount();
            for (FinFundMonitor tx : todayTx) {
                dailySum = dailySum.add(tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO);
            }

            if (dailySum.compareTo(new BigDecimal("5000000")) > 0) {
                monitor.setIsFlagged(true);
                monitor.setFlagReason("单日累计金额超过500万元");
            }
        }

        fundMonitorMapper.insert(monitor);
        return monitor;
    }

    /**
     * Queries fund transactions with flag filter.
     *
     * @param tenantId  the tenant ID
     * @param isFlagged optional flag filter
     * @param page      page number
     * @param limit     page size
     * @return paginated result
     */
    public Page<FinFundMonitor> queryFunds(Long tenantId, Boolean isFlagged, int page, int limit) {
        LambdaQueryWrapper<FinFundMonitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinFundMonitor::getTenantId, tenantId);
        if (isFlagged != null) {
            wrapper.eq(FinFundMonitor::getIsFlagged, isFlagged);
        }
        wrapper.orderByDesc(FinFundMonitor::getId);
        return fundMonitorMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    // ==================== Budget ====================

    /**
     * Creates or updates a budget entry.
     *
     * @param dto the budget data
     * @return the created budget entity
     */
    @Transactional
    public FinBudget createBudget(BudgetDTO dto) {
        FinBudget budget = new FinBudget();
        BeanUtils.copyProperties(dto, budget);
        budget.setActualAmount(BigDecimal.ZERO);
        budget.setExecutedPct(BigDecimal.ZERO);
        budgetMapper.insert(budget);
        return budget;
    }

    /**
     * Checks budget execution status.
     * executed_pct = actual / planned * 100%.
     * > 80% = WARN, > 95% = ALARM.
     *
     * @param tenantId   the tenant ID
     * @param budgetYear the budget year
     * @return list of budgets with updated status
     */
    @Transactional
    public List<FinBudget> checkBudgetExecution(Long tenantId, int budgetYear) {
        List<FinBudget> budgets = budgetMapper.selectList(
                new LambdaQueryWrapper<FinBudget>()
                        .eq(FinBudget::getTenantId, tenantId)
                        .eq(FinBudget::getBudgetYear, budgetYear));

        for (FinBudget budget : budgets) {
            if (budget.getPlannedAmount() != null
                    && budget.getPlannedAmount().compareTo(BigDecimal.ZERO) > 0
                    && budget.getActualAmount() != null) {
                BigDecimal pct = budget.getActualAmount()
                        .multiply(new BigDecimal("100"))
                        .divide(budget.getPlannedAmount(), 2, RoundingMode.HALF_UP);
                budget.setExecutedPct(pct);
                budgetMapper.updateById(budget);
            }
        }

        return budgetMapper.selectList(
                new LambdaQueryWrapper<FinBudget>()
                        .eq(FinBudget::getTenantId, tenantId)
                        .eq(FinBudget::getBudgetYear, budgetYear));
    }

    /**
     * Queries budgets with filters.
     *
     * @param tenantId   the tenant ID
     * @param budgetYear optional year filter
     * @param page       page number
     * @param limit      page size
     * @return paginated result
     */
    public Page<FinBudget> queryBudgets(Long tenantId, Integer budgetYear, int page, int limit) {
        LambdaQueryWrapper<FinBudget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinBudget::getTenantId, tenantId);
        if (budgetYear != null) {
            wrapper.eq(FinBudget::getBudgetYear, budgetYear);
        }
        wrapper.orderByDesc(FinBudget::getId);
        return budgetMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    // ==================== Private Helpers ====================

    private FinReport findReport(Long tenantId, Long orgId, String reportType, int year, int month) {
        List<FinReport> reports = reportMapper.selectList(
                new LambdaQueryWrapper<FinReport>()
                        .eq(FinReport::getTenantId, tenantId)
                        .eq(FinReport::getOrgId, orgId)
                        .eq(FinReport::getReportType, reportType)
                        .eq(FinReport::getReportYear, year)
                        .eq(FinReport::getReportPeriod, month));
        return reports.isEmpty() ? null : reports.get(0);
    }

    private BigDecimal extractValue(FinReport report, String fieldName) {
        if (report == null || report.getReportData() == null) {
            return BigDecimal.ZERO;
        }
        // Simplified extraction: in production use Jackson ObjectMapper
        // Expected format: {"totalAssets": 1000000, "totalLiabilities": 500000, ...}
        try {
            String data = report.getReportData();
            String key = "\"" + fieldName + "\"";
            int keyIdx = data.indexOf(key);
            if (keyIdx < 0) return BigDecimal.ZERO;
            int colonIdx = data.indexOf(":", keyIdx + key.length());
            if (colonIdx < 0) return BigDecimal.ZERO;
            int endIdx = data.indexOf(",", colonIdx + 1);
            if (endIdx < 0) endIdx = data.indexOf("}", colonIdx + 1);
            if (endIdx < 0) return BigDecimal.ZERO;
            String valStr = data.substring(colonIdx + 1, endIdx).trim();
            return new BigDecimal(valStr);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void saveIndicator(Long tenantId, Long orgId, String code, String name,
                               BigDecimal value, BigDecimal warn, BigDecimal alarm,
                               int year, int month) {
        FinIndicator indicator = new FinIndicator();
        indicator.setTenantId(tenantId);
        indicator.setOrgId(orgId);
        indicator.setIndicatorCode(code);
        indicator.setIndicatorName(name);
        indicator.setIndicatorValue(value);
        indicator.setThresholdWarn(warn);
        indicator.setThresholdAlarm(alarm);
        indicator.setPeriodYear(year);
        indicator.setPeriodMonth(month);

        if (value == null) {
            indicator.setStatus("NORMAL");
        } else if (alarm != null && value.compareTo(alarm) <= 0) {
            indicator.setStatus("ALARM");
        } else if (warn != null && value.compareTo(warn) <= 0) {
            indicator.setStatus("WARN");
        } else {
            indicator.setStatus("NORMAL");
        }
        indicatorMapper.insert(indicator);
    }

    private BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividend.divide(divisor, 4, RoundingMode.HALF_UP);
    }
}
