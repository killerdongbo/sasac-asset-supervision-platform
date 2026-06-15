package com.sasac.platform.screen.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.screen.dto.ScreenDataDTO;
import com.sasac.platform.screen.service.ScreenDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screen")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenDataService screenDataService;

    @GetMapping("/data")
    public ApiResponse<ScreenDataDTO> getScreenData(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "1") Long tenantId) {
        return ApiResponse.success(screenDataService.getScreenData(tenantId));
    }
}
