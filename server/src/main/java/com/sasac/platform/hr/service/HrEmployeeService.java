package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.EmployeeCreateDTO;
import com.sasac.platform.hr.dto.EmployeeQueryDTO;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.mapper.HrEmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for HR employee CRUD operations.
 */
@Service
@RequiredArgsConstructor
public class HrEmployeeService {

    private final HrEmployeeMapper hrEmployeeMapper;

    /**
     * Creates a new employee from the provided DTO.
     *
     * @param dto the employee creation data
     * @return the created employee with generated ID
     * @throws BusinessException if the employee number already exists
     */
    @Transactional
    public HrEmployee create(EmployeeCreateDTO dto) {
        // Check employee number uniqueness
        LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrEmployee::getEmployeeNo, dto.getEmployeeNo());
        Long count = hrEmployeeMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("工号已存在");
        }

        HrEmployee employee = new HrEmployee();
        BeanUtils.copyProperties(dto, employee);

        hrEmployeeMapper.insert(employee);

        return employee;
    }

    /**
     * Retrieves an employee by ID.
     *
     * @param id the employee ID
     * @return the employee
     * @throws BusinessException if the employee is not found
     */
    public HrEmployee getById(Long id) {
        HrEmployee employee = hrEmployeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        return employee;
    }

    /**
     * Queries employees with dynamic filters and pagination.
     *
     * @param query the query DTO containing filters
     * @return list of matching employees
     */
    public Page<HrEmployee> query(EmployeeQueryDTO query) {
        LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();

        if (query.getOrgId() != null) {
            wrapper.eq(HrEmployee::getOrgId, query.getOrgId());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(HrEmployee::getDeptId, query.getDeptId());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(HrEmployee::getStatus, query.getStatus());
        }
        if (query.getEmploymentType() != null && !query.getEmploymentType().isBlank()) {
            wrapper.eq(HrEmployee::getEmploymentType, query.getEmploymentType());
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w
                    .like(HrEmployee::getName, query.getKeyword())
                    .or()
                    .like(HrEmployee::getEmployeeNo, query.getKeyword())
            );
        }

        wrapper.orderByDesc(HrEmployee::getId);

        return hrEmployeeMapper.selectPage(
                new Page<>(query.getPage(), query.getLimit()),
                wrapper
        );
    }

    /**
     * Updates an existing employee. Immutable fields (id, employeeNo, tenantId,
     * createdAt) are protected from modification.
     *
     * @param id     the employee ID to update
     * @param update the object containing fields to update
     * @return the updated employee
     * @throws BusinessException if the employee is not found
     */
    @Transactional
    public HrEmployee update(Long id, HrEmployee update) {
        HrEmployee existing = getById(id);

        BeanUtils.copyProperties(update, existing, "id", "employeeNo", "tenantId",
                "createdAt", "updatedAt", "deleted");

        hrEmployeeMapper.updateById(existing);

        // Return a fresh copy from the database
        return hrEmployeeMapper.selectById(id);
    }

    /**
     * Soft-deletes an employee by ID.
     *
     * @param id the employee ID to delete
     * @throws BusinessException if the employee is not found
     */
    @Transactional
    public void delete(Long id) {
        getById(id);
        hrEmployeeMapper.deleteById(id);
    }
}
