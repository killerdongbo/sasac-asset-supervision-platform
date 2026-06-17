package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.EmployeeChangeDTO;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.entity.HrEmployeeChange;
import com.sasac.platform.hr.mapper.HrEmployeeChangeMapper;
import com.sasac.platform.hr.mapper.HrEmployeeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrEmployeeChangeServiceTest {

    @Mock
    private HrEmployeeChangeMapper hrEmployeeChangeMapper;

    @Mock
    private HrEmployeeMapper hrEmployeeMapper;

    @InjectMocks
    private HrEmployeeChangeService hrEmployeeChangeService;

    @Test
    void shouldCreateChange() {
        // given
        HrEmployee employee = new HrEmployee();
        employee.setId(1L);
        employee.setEmployeeNo("EMP001");
        employee.setName("张三");

        EmployeeChangeDTO dto = new EmployeeChangeDTO();
        dto.setEmployeeId(1L);
        dto.setTenantId(0L);
        dto.setChangeType("POSITION");
        dto.setBeforeValue("专员");
        dto.setAfterValue("主管");
        dto.setEffectiveDate(LocalDate.of(2024, 6, 1));
        dto.setReason("晋升");

        when(hrEmployeeMapper.selectById(1L)).thenReturn(employee);
        when(hrEmployeeChangeMapper.insert(any(HrEmployeeChange.class))).thenReturn(1);

        // when
        HrEmployeeChange result = hrEmployeeChangeService.create(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getChangeType()).isEqualTo("POSITION");

        ArgumentCaptor<HrEmployeeChange> captor = ArgumentCaptor.forClass(HrEmployeeChange.class);
        verify(hrEmployeeChangeMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("PENDING");
    }

    @Test
    void shouldThrowWhenEmployeeNotFound() {
        // given
        EmployeeChangeDTO dto = new EmployeeChangeDTO();
        dto.setEmployeeId(999L);
        dto.setTenantId(0L);

        when(hrEmployeeMapper.selectById(999L)).thenReturn(null);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> hrEmployeeChangeService.create(dto));
        assertThat(ex.getMessage()).contains("员工不存在");
    }

    @Test
    void shouldQueryChanges() {
        // given
        Page<HrEmployeeChange> expectedPage = new Page<>(1, 20);
        when(hrEmployeeChangeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        // when
        Page<HrEmployeeChange> result = hrEmployeeChangeService.query(1L, 0L, 1, 20);

        // then
        assertThat(result).isNotNull();
        verify(hrEmployeeChangeMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }
}
