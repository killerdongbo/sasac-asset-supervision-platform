package com.sasac.platform.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Salary entity storing monthly payroll records for employees.
 * <p>
 * Maps to the {@code hr_salary} table and tracks base salary, performance pay,
 * deductions (social insurance, housing fund, tax), and net salary.
 */
@Getter
@Setter
@TableName("hr_salary")
public class HrSalary extends BaseEntity {

    private Long tenantId;

    private Long employeeId;

    private Integer salaryYear;

    private Integer salaryMonth;

    private BigDecimal baseSalary;

    private BigDecimal performancePay;

    private BigDecimal overtimePay;

    private BigDecimal allowance;

    private BigDecimal deduction;

    private BigDecimal socialInsurance;

    private BigDecimal housingFund;

    private BigDecimal tax;

    private BigDecimal netSalary;

    private String status;
}
