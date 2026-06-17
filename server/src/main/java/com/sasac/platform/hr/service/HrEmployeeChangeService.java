package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.EmployeeChangeDTO;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.entity.HrEmployeeChange;
import com.sasac.platform.hr.mapper.HrEmployeeChangeMapper;
import com.sasac.platform.hr.mapper.HrEmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for HR employee change record operations.
 */
@Service
@RequiredArgsConstructor
public class HrEmployeeChangeService {

    private final HrEmployeeChangeMapper hrEmployeeChangeMapper;
    private final HrEmployeeMapper hrEmployeeMapper;

    /**
     * Creates a new employee change record.
     *
     * @param dto the change creation data
     * @return the created change record with generated ID
     * @throws BusinessException if the referenced employee is not found
     */
    @Transactional
    public HrEmployeeChange create(EmployeeChangeDTO dto) {
        // Verify employee exists
        HrEmployee employee = hrEmployeeMapper.selectById(dto.getEmployeeId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        HrEmployeeChange change = new HrEmployeeChange();
        BeanUtils.copyProperties(dto, change);
        change.setStatus("PENDING");

        hrEmployeeChangeMapper.insert(change);

        return change;
    }

    /**
     * Queries employee change records with filters and pagination.
     *
     * @param employeeId optional employee ID filter
     * @param tenantId   tenant ID filter
     * @param page       page number (1-based)
     * @param limit      page size
     * @return page of matching change records
     */
    public Page<HrEmployeeChange> query(Long employeeId, Long tenantId, int page, int limit) {
        LambdaQueryWrapper<HrEmployeeChange> wrapper = new LambdaQueryWrapper<>();

        if (employeeId != null) {
            wrapper.eq(HrEmployeeChange::getEmployeeId, employeeId);
        }
        if (tenantId != null) {
            wrapper.eq(HrEmployeeChange::getTenantId, tenantId);
        }

        wrapper.orderByDesc(HrEmployeeChange::getEffectiveDate);

        return hrEmployeeChangeMapper.selectPage(new Page<>(page, limit), wrapper);
    }
}
