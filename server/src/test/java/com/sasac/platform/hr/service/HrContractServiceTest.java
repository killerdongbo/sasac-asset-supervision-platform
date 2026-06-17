package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.ContractCreateDTO;
import com.sasac.platform.hr.entity.HrContract;
import com.sasac.platform.hr.mapper.HrContractMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrContractServiceTest {

    @Mock
    private HrContractMapper hrContractMapper;

    @InjectMocks
    private HrContractService hrContractService;

    @Test
    void shouldCreateContract() {
        // given
        ContractCreateDTO dto = new ContractCreateDTO();
        dto.setEmployeeId(1L);
        dto.setTenantId(0L);
        dto.setContractNo("CT-2024-001");
        dto.setSignDate(LocalDate.of(2024, 1, 1));
        dto.setStartDate(LocalDate.of(2024, 1, 1));
        dto.setEndDate(LocalDate.of(2025, 1, 1));
        dto.setContractType("FIXED_TERM");

        when(hrContractMapper.insert(any(HrContract.class))).thenReturn(1);

        // when
        HrContract result = hrContractService.create(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContractNo()).isEqualTo("CT-2024-001");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");

        ArgumentCaptor<HrContract> captor = ArgumentCaptor.forClass(HrContract.class);
        verify(hrContractMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldFindExpiringContracts() {
        // given
        HrContract contract1 = new HrContract();
        contract1.setId(1L);
        contract1.setTenantId(0L);
        contract1.setEmployeeId(1L);
        contract1.setStatus("ACTIVE");
        contract1.setIsUnlimited(false);
        contract1.setEndDate(LocalDate.now().plusDays(15));

        HrContract contract2 = new HrContract();
        contract2.setId(2L);
        contract2.setTenantId(0L);
        contract2.setEmployeeId(2L);
        contract2.setStatus("ACTIVE");
        contract2.setIsUnlimited(false);
        contract2.setEndDate(LocalDate.now().plusDays(25));

        when(hrContractMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(contract1, contract2));

        // when
        List<HrContract> result = hrContractService.findExpiring(0L, 30);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEndDate()).isBeforeOrEqualTo(LocalDate.now().plusDays(30));
        verify(hrContractMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldQueryContracts() {
        // given
        Page<HrContract> expectedPage = new Page<>(1, 20);
        when(hrContractMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(expectedPage);

        // when
        Page<HrContract> result = hrContractService.query(1L, 0L, 1, 20);

        // then
        assertThat(result).isNotNull();
        verify(hrContractMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldThrowWhenContractNotFound() {
        // given
        when(hrContractMapper.selectById(anyLong())).thenReturn(null);

        // when & then
        assertThrows(BusinessException.class, () -> hrContractService.getById(999L));
    }
}
