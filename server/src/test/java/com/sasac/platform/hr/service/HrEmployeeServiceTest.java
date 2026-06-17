package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.EmployeeCreateDTO;
import com.sasac.platform.hr.dto.EmployeeQueryDTO;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.mapper.HrEmployeeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrEmployeeServiceTest {

    @Mock
    private HrEmployeeMapper hrEmployeeMapper;

    @InjectMocks
    private HrEmployeeService hrEmployeeService;

    @Test
    void shouldCreateEmployee() {
        // given
        EmployeeCreateDTO dto = new EmployeeCreateDTO();
        dto.setEmployeeNo("EMP001");
        dto.setName("张三");
        dto.setOrgId(1L);
        dto.setTenantId(0L);
        dto.setPhone("13800138000");

        when(hrEmployeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(hrEmployeeMapper.insert(any(HrEmployee.class))).thenReturn(1);

        // when
        HrEmployee result = hrEmployeeService.create(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmployeeNo()).isEqualTo("EMP001");
        assertThat(result.getName()).isEqualTo("张三");
        assertThat(result.getOrgId()).isEqualTo(1L);

        ArgumentCaptor<HrEmployee> captor = ArgumentCaptor.forClass(HrEmployee.class);
        verify(hrEmployeeMapper).insert(captor.capture());
        HrEmployee captured = captor.getValue();
        assertThat(captured.getEmployeeNo()).isEqualTo("EMP001");
        assertThat(captured.getName()).isEqualTo("张三");
    }

    @Test
    void shouldRejectDuplicateEmployeeNo() {
        // given
        EmployeeCreateDTO dto = new EmployeeCreateDTO();
        dto.setEmployeeNo("EMP001");
        dto.setName("张三");
        dto.setOrgId(1L);
        dto.setTenantId(0L);

        when(hrEmployeeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> hrEmployeeService.create(dto));
        assertThat(ex.getMessage()).contains("工号已存在");
    }

    @Test
    void shouldQueryWithFilters() {
        // given
        EmployeeQueryDTO query = new EmployeeQueryDTO();
        query.setOrgId(1L);
        query.setDeptId(2L);
        query.setStatus("ACTIVE");
        query.setEmploymentType("FULL_TIME");
        query.setKeyword("张三");
        query.setPage(1);
        query.setLimit(20);

        Page<HrEmployee> expectedPage = new Page<>(1, 20);
        when(hrEmployeeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        // when
        Page<HrEmployee> result = hrEmployeeService.query(query);

        // then
        assertThat(result).isNotNull();
        verify(hrEmployeeMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldUpdateProtectImmutableFields() {
        // given
        HrEmployee existing = new HrEmployee();
        existing.setId(1L);
        existing.setEmployeeNo("EMP001");
        existing.setTenantId(0L);
        existing.setName("张三");
        existing.setOrgId(1L);
        existing.setStatus("ACTIVE");

        HrEmployee update = new HrEmployee();
        update.setId(999L);
        update.setEmployeeNo("CHANGED");
        update.setTenantId(999L);
        update.setName("张三（改）");
        update.setStatus("RESIGNED");

        when(hrEmployeeMapper.selectById(1L)).thenReturn(existing);
        when(hrEmployeeMapper.updateById(any(HrEmployee.class))).thenReturn(1);
        when(hrEmployeeMapper.selectById(1L)).thenReturn(existing);

        // when
        HrEmployee result = hrEmployeeService.update(1L, update);

        // then
        assertThat(result).isNotNull();
        ArgumentCaptor<HrEmployee> captor = ArgumentCaptor.forClass(HrEmployee.class);
        verify(hrEmployeeMapper).updateById(captor.capture());
        HrEmployee captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo(1L);
        assertThat(captured.getEmployeeNo()).isEqualTo("EMP001");
        assertThat(captured.getTenantId()).isEqualTo(0L);
    }

    @Test
    void shouldDeleteEmployee() {
        // given
        HrEmployee existing = new HrEmployee();
        existing.setId(1L);
        existing.setEmployeeNo("EMP001");
        existing.setName("张三");

        when(hrEmployeeMapper.selectById(1L)).thenReturn(existing);

        // when
        hrEmployeeService.delete(1L);

        // then
        verify(hrEmployeeMapper).deleteById(1L);
    }

    @Test
    void shouldThrowWhenEmployeeNotFound() {
        // given
        when(hrEmployeeMapper.selectById(anyLong())).thenReturn(null);

        // when & then
        assertThrows(BusinessException.class, () -> hrEmployeeService.getById(999L));
    }
}
