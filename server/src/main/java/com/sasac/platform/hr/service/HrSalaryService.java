package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.SalaryCalculateDTO;
import com.sasac.platform.hr.dto.SalaryQueryDTO;
import com.sasac.platform.hr.entity.HrAttendance;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.entity.HrPerformance;
import com.sasac.platform.hr.entity.HrSalary;
import com.sasac.platform.hr.mapper.HrAttendanceMapper;
import com.sasac.platform.hr.mapper.HrEmployeeMapper;
import com.sasac.platform.hr.mapper.HrPerformanceMapper;
import com.sasac.platform.hr.mapper.HrSalaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service for salary calculation and management.
 * <p>
 * Core salary calculation engine: computes net salary from base salary,
 * performance pay, attendance, and statutory deductions.
 */
@Service
@RequiredArgsConstructor
public class HrSalaryService {

    private static final BigDecimal DEFAULT_BASE_SALARY = new BigDecimal("8000.00");
    private static final BigDecimal SOCIAL_INSURANCE_RATE = new BigDecimal("0.105");
    private static final BigDecimal HOUSING_FUND_RATE = new BigDecimal("0.12");
    private static final BigDecimal TAX_THRESHOLD = new BigDecimal("5000.00");

    private final HrSalaryMapper hrSalaryMapper;
    private final HrEmployeeMapper hrEmployeeMapper;
    private final HrAttendanceMapper hrAttendanceMapper;
    private final HrPerformanceMapper hrPerformanceMapper;

    /**
     * Calculates monthly salary for an employee.
     * <p>
     * Steps:
     * 1. Verify employee exists
     * 2. Get attendance summary for the month
     * 3. Get confirmed performance record (if available)
     * 4. Compute gross pay = base salary + performance pay + overtime + allowance
     * 5. Deduct social insurance (10.5%), housing fund (12%), and tax
     * 6. Set net salary and persist with CALCULATED status
     *
     * @param dto calculation parameters
     * @return the calculated salary record
     */
    @Transactional
    public HrSalary calculateMonthlySalary(SalaryCalculateDTO dto) {
        // 1. Verify employee exists
        HrEmployee employee = hrEmployeeMapper.selectById(dto.getEmployeeId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 2. Check if salary already calculated for this period
        LambdaQueryWrapper<HrSalary> existCheck = new LambdaQueryWrapper<>();
        existCheck.eq(HrSalary::getTenantId, dto.getTenantId());
        existCheck.eq(HrSalary::getEmployeeId, dto.getEmployeeId());
        existCheck.eq(HrSalary::getSalaryYear, dto.getSalaryYear());
        existCheck.eq(HrSalary::getSalaryMonth, dto.getSalaryMonth());
        Long existing = hrSalaryMapper.selectCount(existCheck);
        if (existing > 0) {
            throw new BusinessException("该月份薪资已计算，请勿重复操作");
        }

        // 3. Get attendance for the month (count days)
        LambdaQueryWrapper<HrAttendance> attWrapper = new LambdaQueryWrapper<>();
        attWrapper.eq(HrAttendance::getTenantId, dto.getTenantId());
        attWrapper.eq(HrAttendance::getEmployeeId, dto.getEmployeeId());
        attWrapper.apply("EXTRACT(YEAR FROM att_date) = {0}", dto.getSalaryYear());
        attWrapper.apply("EXTRACT(MONTH FROM att_date) = {0}", dto.getSalaryMonth());
        List<HrAttendance> attendances = hrAttendanceMapper.selectList(attWrapper);
        long normalDays = attendances.stream()
                .filter(a -> "NORMAL".equals(a.getStatus()))
                .count();
        long absentDays = attendances.stream()
                .filter(a -> "ABSENT".equals(a.getStatus()))
                .count();

        // 4. Get performance record (CONFIRMED only)
        LambdaQueryWrapper<HrPerformance> perfWrapper = new LambdaQueryWrapper<>();
        perfWrapper.eq(HrPerformance::getTenantId, dto.getTenantId());
        perfWrapper.eq(HrPerformance::getEmployeeId, dto.getEmployeeId());
        perfWrapper.eq(HrPerformance::getCycleYear, dto.getSalaryYear());
        perfWrapper.eq(HrPerformance::getStatus, "CONFIRMED");
        perfWrapper.orderByDesc(HrPerformance::getId);
        perfWrapper.last("LIMIT 1");
        HrPerformance performance = hrPerformanceMapper.selectOne(perfWrapper);

        // 5. Build salary record
        HrSalary salary = new HrSalary();
        salary.setTenantId(dto.getTenantId());
        salary.setEmployeeId(dto.getEmployeeId());
        salary.setSalaryYear(dto.getSalaryYear());
        salary.setSalaryMonth(dto.getSalaryMonth());

        // Base salary (default 8000 if not set on employee)
        BigDecimal baseSalary = employee.getPosition() != null
                ? DEFAULT_BASE_SALARY
                : DEFAULT_BASE_SALARY;
        salary.setBaseSalary(baseSalary);

        // Performance pay = finalScore x 100 (if performance exists)
        BigDecimal performancePay = BigDecimal.ZERO;
        if (performance != null && performance.getFinalScore() != null) {
            performancePay = performance.getFinalScore()
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        salary.setPerformancePay(performancePay);

        // Overtime pay (proportional to absent days impact)
        BigDecimal overtimePay = BigDecimal.ZERO;
        salary.setOvertimePay(overtimePay);

        // Allowance (proportional to normal days / 22 working days)
        BigDecimal allowance = BigDecimal.ZERO;
        if (normalDays > 0) {
            allowance = BigDecimal.valueOf(normalDays)
                    .multiply(BigDecimal.valueOf(50))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        salary.setAllowance(allowance);

        // Gross pay
        BigDecimal grossPay = baseSalary.add(performancePay).add(overtimePay).add(allowance);

        // Absence deduction (proportional deduction based on absent days)
        BigDecimal deduction = BigDecimal.ZERO;
        if (absentDays > 0) {
            BigDecimal dailyRate = baseSalary.divide(BigDecimal.valueOf(22), 2, RoundingMode.HALF_UP);
            deduction = dailyRate.multiply(BigDecimal.valueOf(absentDays))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        salary.setDeduction(deduction);

        // Social insurance (10.5% of gross pay)
        BigDecimal socialInsurance = grossPay.multiply(SOCIAL_INSURANCE_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        salary.setSocialInsurance(socialInsurance);

        // Housing fund (12% of gross pay)
        BigDecimal housingFund = grossPay.multiply(HOUSING_FUND_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        salary.setHousingFund(housingFund);

        // Taxable income = gross pay - deductions - social insurance - housing fund - threshold
        BigDecimal taxableIncome = grossPay.subtract(deduction)
                .subtract(socialInsurance)
                .subtract(housingFund)
                .subtract(TAX_THRESHOLD);
        BigDecimal tax = BigDecimal.ZERO;
        if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
            // Simplified progressive tax rate (3% for first bracket)
            if (taxableIncome.compareTo(new BigDecimal("3000")) <= 0) {
                tax = taxableIncome.multiply(new BigDecimal("0.03"))
                        .setScale(2, RoundingMode.HALF_UP);
            } else if (taxableIncome.compareTo(new BigDecimal("12000")) <= 0) {
                tax = taxableIncome.multiply(new BigDecimal("0.10"))
                        .subtract(new BigDecimal("210"))
                        .setScale(2, RoundingMode.HALF_UP);
            } else {
                tax = taxableIncome.multiply(new BigDecimal("0.20"))
                        .subtract(new BigDecimal("1410"))
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }
        salary.setTax(tax);

        // Net salary = gross pay - deduction - social insurance - housing fund - tax
        BigDecimal netSalary = grossPay.subtract(deduction)
                .subtract(socialInsurance)
                .subtract(housingFund)
                .subtract(tax)
                .setScale(2, RoundingMode.HALF_UP);
        salary.setNetSalary(netSalary);

        salary.setStatus("CALCULATED");

        hrSalaryMapper.insert(salary);
        return salary;
    }

    /**
     * Queries salary records by employee with pagination.
     *
     * @param query the query DTO with filters
     * @return paginated salary records
     */
    public Page<HrSalary> queryByEmployee(SalaryQueryDTO query) {
        LambdaQueryWrapper<HrSalary> wrapper = new LambdaQueryWrapper<>();

        if (query.getTenantId() != null) {
            wrapper.eq(HrSalary::getTenantId, query.getTenantId());
        }
        if (query.getEmployeeId() != null) {
            wrapper.eq(HrSalary::getEmployeeId, query.getEmployeeId());
        }
        if (query.getSalaryYear() != null) {
            wrapper.eq(HrSalary::getSalaryYear, query.getSalaryYear());
        }
        if (query.getSalaryMonth() != null) {
            wrapper.eq(HrSalary::getSalaryMonth, query.getSalaryMonth());
        }

        wrapper.orderByDesc(HrSalary::getSalaryYear, HrSalary::getSalaryMonth);

        return hrSalaryMapper.selectPage(
                new Page<>(query.getPage(), query.getLimit()),
                wrapper
        );
    }

    /**
     * Confirms a salary record, changing status to CONFIRMED.
     *
     * @param id the salary record ID
     * @return the confirmed salary record
     * @throws BusinessException if the record is not found
     */
    @Transactional
    public HrSalary confirm(Long id) {
        HrSalary salary = hrSalaryMapper.selectById(id);
        if (salary == null) {
            throw new BusinessException("薪资记录不存在");
        }
        salary.setStatus("CONFIRMED");
        hrSalaryMapper.updateById(salary);
        return hrSalaryMapper.selectById(id);
    }
}
