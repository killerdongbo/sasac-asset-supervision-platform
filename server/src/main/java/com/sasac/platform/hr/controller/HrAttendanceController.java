package com.sasac.platform.hr.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.hr.dto.AttendanceRecordDTO;
import com.sasac.platform.hr.entity.HrAttendance;
import com.sasac.platform.hr.service.HrAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for attendance recording and querying.
 */
@RestController
@RequestMapping("/api/hr/attendances")
@RequiredArgsConstructor
public class HrAttendanceController {

    private final HrAttendanceService hrAttendanceService;

    /**
     * Records attendance for an employee.
     *
     * @param dto the attendance record data
     * @return API response with the saved attendance
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HrAttendance>> record(@Valid @RequestBody AttendanceRecordDTO dto) {
        HrAttendance attendance = hrAttendanceService.record(dto);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }

    /**
     * Queries attendance records for an employee in a given year/month.
     *
     * @param employeeId the employee ID
     * @param tenantId   the tenant ID
     * @param year       the year
     * @param month      the month
     * @return API response with attendance list
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HrAttendance>>> query(
            @RequestParam Long employeeId,
            @RequestParam Long tenantId,
            @RequestParam int year,
            @RequestParam int month) {
        List<HrAttendance> records = hrAttendanceService.queryByEmployee(employeeId, tenantId, year, month);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
