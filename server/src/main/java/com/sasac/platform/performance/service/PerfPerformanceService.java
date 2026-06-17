package com.sasac.platform.performance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.performance.dto.IncentiveDTO;
import com.sasac.platform.performance.dto.IndicatorDefDTO;
import com.sasac.platform.performance.dto.SalaryBudgetDTO;
import com.sasac.platform.performance.entity.PerfIncentive;
import com.sasac.platform.performance.entity.PerfIndicatorDef;
import com.sasac.platform.performance.entity.PerfSalaryBudget;
import com.sasac.platform.performance.mapper.PerfIncentiveMapper;
import com.sasac.platform.performance.mapper.PerfIndicatorDefMapper;
import com.sasac.platform.performance.mapper.PerfSalaryBudgetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service for performance and compensation business logic.
 */
@Service
@RequiredArgsConstructor
public class PerfPerformanceService {

    private final PerfIndicatorDefMapper indicatorDefMapper;
    private final PerfSalaryBudgetMapper salaryBudgetMapper;
    private final PerfIncentiveMapper incentiveMapper;

    // ==================== Indicators ====================

    /**
     * Batch saves performance indicators for an organization and year.
     *
     * @param dtos list of indicator definitions
     * @return the saved indicator entities
     */
    @Transactional
    public List<PerfIndicatorDef> defineIndicators(List<IndicatorDefDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BusinessException("指标数据不能为空");
        }

        Long tenantId = dtos.get(0).getTenantId();
        Long orgId = dtos.get(0).getOrgId();
        Integer cycleYear = dtos.get(0).getCycleYear();

        // Delete existing indicators for the same org/year
        indicatorDefMapper.delete(new LambdaQueryWrapper<PerfIndicatorDef>()
                .eq(PerfIndicatorDef::getTenantId, tenantId)
                .eq(PerfIndicatorDef::getOrgId, orgId)
                .eq(PerfIndicatorDef::getCycleYear, cycleYear));

        // Validate weight sum = 100%
        BigDecimal totalWeight = dtos.stream()
                .map(IndicatorDefDTO::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new BusinessException("指标权重之和必须为100%");
        }

        for (IndicatorDefDTO dto : dtos) {
            PerfIndicatorDef entity = new PerfIndicatorDef();
            BeanUtils.copyProperties(dto, entity);

            // Auto-calculate initial score if actualValue is provided
            if (entity.getActualValue() != null && entity.getTargetValue() != null
                    && entity.getTargetValue().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = entity.getActualValue()
                        .multiply(new BigDecimal("100"))
                        .divide(entity.getTargetValue(), 2, RoundingMode.HALF_UP);
                BigDecimal score = ratio.min(new BigDecimal("100"))
                        .multiply(entity.getWeight())
                        .divide(new BigDecimal("100"), 1, RoundingMode.HALF_UP);
                entity.setScore(score);
            }

            indicatorDefMapper.insert(entity);
        }

        return indicatorDefMapper.selectList(new LambdaQueryWrapper<PerfIndicatorDef>()
                .eq(PerfIndicatorDef::getTenantId, tenantId)
                .eq(PerfIndicatorDef::getOrgId, orgId)
                .eq(PerfIndicatorDef::getCycleYear, cycleYear));
    }

    /**
     * Updates the actual value for a single indicator and recalculates its score.
     *
     * @param id           the indicator ID
     * @param actualValue  the actual achieved value
     * @return the updated indicator entity
     */
    @Transactional
    public PerfIndicatorDef recordActual(Long id, BigDecimal actualValue) {
        PerfIndicatorDef indicator = indicatorDefMapper.selectById(id);
        if (indicator == null) {
            throw new BusinessException("指标不存在");
        }

        indicator.setActualValue(actualValue);

        // Recalculate score: score = min(100, actual/target * 100) * weight / 100
        if (indicator.getTargetValue() != null && indicator.getTargetValue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ratio = actualValue
                    .multiply(new BigDecimal("100"))
                    .divide(indicator.getTargetValue(), 2, RoundingMode.HALF_UP);
            BigDecimal rawScore = ratio.min(new BigDecimal("100"))
                    .multiply(indicator.getWeight())
                    .divide(new BigDecimal("100"), 1, RoundingMode.HALF_UP);
            indicator.setScore(rawScore);
        }

        indicatorDefMapper.updateById(indicator);
        return indicator;
    }

    /**
     * Calculates scores for all indicators of an organization in a given year.
     * For each indicator: weighted score = min(100, actual/target * 100) * weight / 100.
     * Total score = sum of all weighted scores.
     *
     * @param tenantId the tenant ID
     * @param orgId    the organization ID
     * @param year     the cycle year
     * @return list of indicators with updated scores
     */
    @Transactional
    public List<PerfIndicatorDef> calculateScores(Long tenantId, Long orgId, int year) {
        List<PerfIndicatorDef> indicators = indicatorDefMapper.selectList(
                new LambdaQueryWrapper<PerfIndicatorDef>()
                        .eq(PerfIndicatorDef::getTenantId, tenantId)
                        .eq(PerfIndicatorDef::getOrgId, orgId)
                        .eq(PerfIndicatorDef::getCycleYear, year));

        for (PerfIndicatorDef indicator : indicators) {
            if (indicator.getActualValue() != null && indicator.getTargetValue() != null
                    && indicator.getTargetValue().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = indicator.getActualValue()
                        .multiply(new BigDecimal("100"))
                        .divide(indicator.getTargetValue(), 2, RoundingMode.HALF_UP);
                BigDecimal rawScore = ratio.min(new BigDecimal("100"))
                        .multiply(indicator.getWeight())
                        .divide(new BigDecimal("100"), 1, RoundingMode.HALF_UP);
                indicator.setScore(rawScore);
            } else {
                indicator.setScore(BigDecimal.ZERO);
            }
            indicatorDefMapper.updateById(indicator);
        }

        return indicatorDefMapper.selectList(new LambdaQueryWrapper<PerfIndicatorDef>()
                .eq(PerfIndicatorDef::getTenantId, tenantId)
                .eq(PerfIndicatorDef::getOrgId, orgId)
                .eq(PerfIndicatorDef::getCycleYear, year));
    }

    /**
     * Returns all indicators with scores for an organization and year.
     *
     * @param orgId the organization ID
     * @param year  the cycle year
     * @return list of indicators
     */
    public List<PerfIndicatorDef> getIndicatorSummary(Long tenantId, Long orgId, int year) {
        return indicatorDefMapper.selectList(
                new LambdaQueryWrapper<PerfIndicatorDef>()
                        .eq(PerfIndicatorDef::getTenantId, tenantId)
                        .eq(PerfIndicatorDef::getOrgId, orgId)
                        .eq(PerfIndicatorDef::getCycleYear, year)
                        .orderByAsc(PerfIndicatorDef::getIndicatorCode));
    }

    // ==================== Salary Budget ====================

    /**
     * Creates or updates a salary budget entry.
     *
     * @param dto the salary budget data
     * @return the saved budget entity
     */
    @Transactional
    public PerfSalaryBudget manageSalaryBudget(SalaryBudgetDTO dto) {
        PerfSalaryBudget budget;
        if (dto.getId() != null) {
            budget = salaryBudgetMapper.selectById(dto.getId());
            if (budget == null) {
                throw new BusinessException("薪酬预算记录不存在");
            }
            BeanUtils.copyProperties(dto, budget, "id", "tenantId", "orgId");
        } else {
            budget = new PerfSalaryBudget();
            BeanUtils.copyProperties(dto, budget);
            if (budget.getStatus() == null) {
                budget.setStatus("DRAFT");
            }
        }
        salaryBudgetMapper.insertOrUpdate(budget);
        return budget;
    }

    /**
     * Adjusts the approved budget for a salary budget entry.
     *
     * @param id           the budget ID
     * @param adjustment   the adjustment amount (positive or negative)
     * @param reason       the adjustment reason
     * @return the updated budget entity
     */
    @Transactional
    public PerfSalaryBudget adjustBudget(Long id, BigDecimal adjustment, String reason) {
        PerfSalaryBudget budget = salaryBudgetMapper.selectById(id);
        if (budget == null) {
            throw new BusinessException("薪酬预算记录不存在");
        }
        BigDecimal newApproved = budget.getApprovedBudget() != null
                ? budget.getApprovedBudget().add(adjustment)
                : adjustment;
        budget.setApprovedBudget(newApproved);
        budget.setAdjustmentReason(reason);
        budget.setStatus("ADJUSTED");
        salaryBudgetMapper.updateById(budget);
        return budget;
    }

    /**
     * Queries salary budgets with optional year filter.
     *
     * @param tenantId   the tenant ID
     * @param budgetYear optional year filter
     * @param page       page number
     * @param limit      page size
     * @return paginated result
     */
    public Page<PerfSalaryBudget> querySalaryBudgets(Long tenantId, Integer budgetYear, int page, int limit) {
        LambdaQueryWrapper<PerfSalaryBudget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerfSalaryBudget::getTenantId, tenantId);
        if (budgetYear != null) {
            wrapper.eq(PerfSalaryBudget::getBudgetYear, budgetYear);
        }
        wrapper.orderByDesc(PerfSalaryBudget::getBudgetYear);
        return salaryBudgetMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    /**
     * Gets a single salary budget by ID.
     *
     * @param id the budget ID
     * @return the budget entity
     */
    public PerfSalaryBudget getSalaryBudget(Long id) {
        PerfSalaryBudget budget = salaryBudgetMapper.selectById(id);
        if (budget == null) {
            throw new BusinessException("薪酬预算记录不存在");
        }
        return budget;
    }

    /**
     * Deletes a salary budget by ID.
     *
     * @param id the budget ID
     */
    @Transactional
    public void deleteSalaryBudget(Long id) {
        PerfSalaryBudget budget = salaryBudgetMapper.selectById(id);
        if (budget == null) {
            throw new BusinessException("薪酬预算记录不存在");
        }
        salaryBudgetMapper.deleteById(id);
    }

    // ==================== Incentives ====================

    /**
     * Creates an equity incentive record.
     *
     * @param dto the incentive data
     * @return the created incentive entity
     */
    @Transactional
    public PerfIncentive createIncentive(IncentiveDTO dto) {
        PerfIncentive incentive = new PerfIncentive();
        BeanUtils.copyProperties(dto, incentive);
        if (incentive.getStatus() == null) {
            incentive.setStatus("PENDING");
        }
        incentiveMapper.insert(incentive);
        return incentive;
    }

    /**
     * Queries incentives with optional filters.
     *
     * @param tenantId      the tenant ID
     * @param incentiveType optional type filter
     * @param page          page number
     * @param limit         page size
     * @return paginated result
     */
    public Page<PerfIncentive> queryIncentives(Long tenantId, String incentiveType, int page, int limit) {
        LambdaQueryWrapper<PerfIncentive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerfIncentive::getTenantId, tenantId);
        if (incentiveType != null && !incentiveType.isBlank()) {
            wrapper.eq(PerfIncentive::getIncentiveType, incentiveType);
        }
        wrapper.orderByDesc(PerfIncentive::getId);
        return incentiveMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    /**
     * Gets a single incentive by ID.
     *
     * @param id the incentive ID
     * @return the incentive entity
     */
    public PerfIncentive getIncentive(Long id) {
        PerfIncentive incentive = incentiveMapper.selectById(id);
        if (incentive == null) {
            throw new BusinessException("激励记录不存在");
        }
        return incentive;
    }

    /**
     * Deletes an incentive by ID.
     *
     * @param id the incentive ID
     */
    @Transactional
    public void deleteIncentive(Long id) {
        PerfIncentive incentive = incentiveMapper.selectById(id);
        if (incentive == null) {
            throw new BusinessException("激励记录不存在");
        }
        incentiveMapper.deleteById(id);
    }
}
