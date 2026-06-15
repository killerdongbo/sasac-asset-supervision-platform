package com.sasac.platform.report.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.report.dto.GroupStatisticsDTO;
import com.sasac.platform.report.service.GroupStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics/group")
@RequiredArgsConstructor
public class GroupStatisticsController {

    private final GroupStatisticsService groupStatisticsService;

    @GetMapping("/{orgId}")
    public ApiResponse<GroupStatisticsDTO> getGroupStatistics(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @PathVariable Long orgId) {
        return ApiResponse.success(groupStatisticsService.getGroupStatistics(tenantId, orgId));
    }
}
