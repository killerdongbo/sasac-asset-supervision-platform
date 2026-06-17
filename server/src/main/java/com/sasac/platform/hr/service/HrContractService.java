package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.ContractCreateDTO;
import com.sasac.platform.hr.entity.HrContract;
import com.sasac.platform.hr.mapper.HrContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for HR contract CRUD operations.
 */
@Service
@RequiredArgsConstructor
public class HrContractService {

    private final HrContractMapper hrContractMapper;

    /**
     * Creates a new contract from the provided DTO.
     *
     * @param dto the contract creation data
     * @return the created contract with generated ID
     */
    @Transactional
    public HrContract create(ContractCreateDTO dto) {
        HrContract contract = new HrContract();
        BeanUtils.copyProperties(dto, contract);
        contract.setStatus("ACTIVE");

        hrContractMapper.insert(contract);

        return contract;
    }

    /**
     * Retrieves a contract by ID.
     *
     * @param id the contract ID
     * @return the contract
     * @throws BusinessException if the contract is not found
     */
    public HrContract getById(Long id) {
        HrContract contract = hrContractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        return contract;
    }

    /**
     * Queries contracts with filters and pagination.
     *
     * @param employeeId optional employee ID filter
     * @param tenantId   tenant ID filter
     * @param page       page number (1-based)
     * @param limit      page size
     * @return page of matching contracts
     */
    public Page<HrContract> query(Long employeeId, Long tenantId, int page, int limit) {
        LambdaQueryWrapper<HrContract> wrapper = new LambdaQueryWrapper<>();

        if (employeeId != null) {
            wrapper.eq(HrContract::getEmployeeId, employeeId);
        }
        if (tenantId != null) {
            wrapper.eq(HrContract::getTenantId, tenantId);
        }

        wrapper.orderByDesc(HrContract::getSignDate);

        return hrContractMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    /**
     * Finds contracts expiring within the specified number of days.
     *
     * @param tenantId tenant ID filter
     * @param days     number of days from now
     * @return list of expiring contracts
     */
    public List<HrContract> findExpiring(Long tenantId, int days) {
        LambdaQueryWrapper<HrContract> wrapper = new LambdaQueryWrapper<>();
        LocalDate deadline = LocalDate.now().plusDays(days);

        wrapper.eq(HrContract::getTenantId, tenantId)
                .eq(HrContract::getStatus, "ACTIVE")
                .eq(HrContract::getIsUnlimited, false)
                .isNotNull(HrContract::getEndDate)
                .le(HrContract::getEndDate, deadline);

        return hrContractMapper.selectList(wrapper);
    }

    /**
     * Updates an existing contract. Immutable fields (id, tenantId, createdAt,
     * updatedAt, deleted) are protected from modification.
     *
     * @param id     the contract ID to update
     * @param update the object containing fields to update
     * @return the updated contract
     * @throws BusinessException if the contract is not found
     */
    @Transactional
    public HrContract update(Long id, HrContract update) {
        HrContract existing = getById(id);

        BeanUtils.copyProperties(update, existing, "id", "tenantId", "createdAt",
                "updatedAt", "deleted");

        hrContractMapper.updateById(existing);

        return hrContractMapper.selectById(id);
    }

    /**
     * Soft-deletes a contract by ID.
     *
     * @param id the contract ID to delete
     * @throws BusinessException if the contract is not found
     */
    @Transactional
    public void delete(Long id) {
        getById(id);
        hrContractMapper.deleteById(id);
    }
}
