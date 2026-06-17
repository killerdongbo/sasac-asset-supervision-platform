package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.AttendanceRecordDTO;
import com.sasac.platform.hr.entity.HrAttendance;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.mapper.HrAttendanceMapper;
import com.sasac.platform.hr.mapper.HrEmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for employee attendance recording and querying.
 */
@Service
@RequiredArgsConstructor
public class HrAttendanceService {

    private final HrAttendanceMapper hrAttendanceMapper;
    private final HrEmployeeMapper hrEmployeeMapper;

    /**
     * Records or updates attendance for an employee on a given date.
     *
     * @param dto the attendance record data
     * @return the saved attendance record
     * @throws BusinessException if the employee is not found
     */
    @Transactional
    public HrAttendance record(AttendanceRecordDTO dto) {
        HrEmployee employee = hrEmployeeMapper.selectById(dto.getEmployeeId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // Check if attendance already exists for this date
        LambdaQueryWrapper<HrAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrAttendance::getTenantId, dto.getTenantId());
        wrapper.eq(HrAttendance::getEmployeeId, dto.getEmployeeId());
        wrapper.eq(HrAttendance::getAttDate, dto.getAttDate());
        HrAttendance existing = hrAttendanceMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setCheckIn(dto.getCheckIn());
            existing.setCheckOut(dto.getCheckOut());
            hrAttendanceMapper.updateById(existing);
            return hrAttendanceMapper.selectById(existing.getId());
        }

        HrAttendance attendance = new HrAttendance();
        attendance.setTenantId(dto.getTenantId());
        attendance.setEmployeeId(dto.getEmployeeId());
        attendance.setAttDate(dto.getAttDate());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());

        // Automatically determine status
        if (dto.getCheckIn() == null && dto.getCheckOut() == null) {
            attendance.setStatus("ABSENT");
        } else {
            attendance.setStatus("NORMAL");
        }

        hrAttendanceMapper.insert(attendance);
        return attendance;
    }

    /**
     * Queries attendance records for an employee in a given year/month.
     *
     * @param employeeId the employee ID
     * @param tenantId   the tenant ID
     * @param year       the year
     * @param month      the month
     * @return list of attendance records for the month
     */
    public List<HrAttendance> queryByEmployee(Long employeeId, Long tenantId, int year, int month) {
        LambdaQueryWrapper<HrAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrAttendance::getTenantId, tenantId);
        wrapper.eq(HrAttendance::getEmployeeId, employeeId);
        wrapper.apply("EXTRACT(YEAR FROM att_date) = {0}", year);
        wrapper.apply("EXTRACT(MONTH FROM att_date) = {0}", month);
        wrapper.orderByAsc(HrAttendance::getAttDate);

        return hrAttendanceMapper.selectList(wrapper);
    }
}
