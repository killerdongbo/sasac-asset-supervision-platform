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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrSalaryServiceTest {

    @Mock
    private HrSalaryMapper hrSalaryMapper;

    @Mock
    private HrEmployeeMapper hrEmployeeMapper;

    @Mock
    private HrAttendanceMapper hrAttendanceMapper;

    @Mock
    private HrPerformanceMapper hrPerformanceMapper;

    @InjectMocks
    private HrSalaryService hrSalaryService;

    @Test
    void shouldCalculateSalary() {
        // given
        HrEmployee employee = new HrEmployee();
        employee.setId(1L);
        employee.setTenantId(0L);
        employee.setEmployeeNo("EMP001");
        employee.setName("张三");
        employee.setPosition("软件工程师");

        SalaryCalculateDTO dto = new SalaryCalculateDTO();
        dto.setEmployeeId(1L);
        dto.setTenantId(0L);
        dto.setSalaryYear(2024);
        dto.setSalaryMonth(6);

        HrAttendance normalDay = new HrAttendance();
        normalDay.setEmployeeId(1L);
        normalDay.setTenantId(0L);
        normalDay.setStatus("NORMAL");

        HrAttendance absentDay = new HrAttendance();
        absentDay.setEmployeeId(1L);
        absentDay.setTenantId(0L);
        absentDay.setStatus("ABSENT");

        HrPerformance performance = new HrPerformance();
        performance.setEmployeeId(1L);
        performance.setTenantId(0L);
        performance.setFinalScore(new BigDecimal("85"));
        performance.setStatus("CONFIRMED");

        when(hrEmployeeMapper.selectById(1L)).thenReturn(employee);
        when(hrSalaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(hrAttendanceMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(normalDay, normalDay, absentDay));
        when(hrPerformanceMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(performance);

        // when
        HrSalary result = hrSalaryService.calculateMonthlySalary(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("CALCULATED");
        assertThat(result.getEmployeeId()).isEqualTo(1L);
        assertThat(result.getSalaryYear()).isEqualTo(2024);
        assertThat(result.getSalaryMonth()).isEqualTo(6);

        // baseSalary = 8000 (has position)
        assertThat(result.getBaseSalary()).isEqualByComparingTo(new BigDecimal("8000.00"));

        // performancePay = 85 * 100 = 8500
        assertThat(result.getPerformancePay()).isEqualByComparingTo(new BigDecimal("8500.00"));

        // overtimePay = 0
        assertThat(result.getOvertimePay()).isEqualByComparingTo(BigDecimal.ZERO);

        // allowance = 2 * 50 = 100 (2 normal days)
        assertThat(result.getAllowance()).isEqualByComparingTo(new BigDecimal("100.00"));

        // deduction = (8000/22) * 1 absent day = 363.64 (rounded)
        assertThat(result.getDeduction()).isEqualByComparingTo(new BigDecimal("363.64"));

        // grossPay = 8000 + 8500 + 0 + 100 = 16600
        // socialInsurance = 16600 * 0.105 = 1743.00
        // housingFund = 16600 * 0.12 = 1992.00
        // taxableIncome = 16600 - 363.64 - 1743.00 - 1992.00 - 5000 = 7501.36
        // tax = 7501.36 * 0.10 - 210 = 540.14 (rounded)
        // netSalary = 16600 - 363.64 - 1743.00 - 1992.00 - 540.14 = 11961.22
        assertThat(result.getSocialInsurance()).isEqualByComparingTo(new BigDecimal("1743.00"));
        assertThat(result.getHousingFund()).isEqualByComparingTo(new BigDecimal("1992.00"));
        assertThat(result.getTax()).isEqualByComparingTo(new BigDecimal("540.14"));
        assertThat(result.getNetSalary()).isEqualByComparingTo(new BigDecimal("11961.22"));

        ArgumentCaptor<HrSalary> captor = ArgumentCaptor.forClass(HrSalary.class);
        verify(hrSalaryMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("CALCULATED");
    }

    @Test
    void shouldRejectDuplicateCalculation() {
        // given
        HrEmployee employee = new HrEmployee();
        employee.setId(1L);
        employee.setTenantId(0L);
        employee.setEmployeeNo("EMP001");

        SalaryCalculateDTO dto = new SalaryCalculateDTO();
        dto.setEmployeeId(1L);
        dto.setTenantId(0L);
        dto.setSalaryYear(2024);
        dto.setSalaryMonth(6);

        when(hrEmployeeMapper.selectById(1L)).thenReturn(employee);
        when(hrSalaryMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> hrSalaryService.calculateMonthlySalary(dto));
        assertThat(ex.getMessage()).contains("该月份薪资已计算");
    }

    @Test
    void shouldQueryByEmployee() {
        // given
        SalaryQueryDTO query = new SalaryQueryDTO();
        query.setTenantId(0L);
        query.setEmployeeId(1L);
        query.setSalaryYear(2024);
        query.setSalaryMonth(6);
        query.setPage(1);
        query.setLimit(20);

        Page<HrSalary> expectedPage = new Page<>(1, 20);
        when(hrSalaryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        // when
        Page<HrSalary> result = hrSalaryService.queryByEmployee(query);

        // then
        assertThat(result).isNotNull();
        verify(hrSalaryMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldConfirmSalary() {
        // given
        HrSalary salary = new HrSalary();
        salary.setId(1L);
        salary.setEmployeeId(1L);
        salary.setStatus("CALCULATED");

        HrSalary confirmed = new HrSalary();
        confirmed.setId(1L);
        confirmed.setEmployeeId(1L);
        confirmed.setStatus("CONFIRMED");

        when(hrSalaryMapper.selectById(1L)).thenReturn(salary).thenReturn(confirmed);

        // when
        HrSalary result = hrSalaryService.confirm(1L);

        // then
        assertThat(result.getStatus()).isEqualTo("CONFIRMED");
        ArgumentCaptor<HrSalary> captor = ArgumentCaptor.forClass(HrSalary.class);
        verify(hrSalaryMapper).updateById(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("CONFIRMED");
    }
}
