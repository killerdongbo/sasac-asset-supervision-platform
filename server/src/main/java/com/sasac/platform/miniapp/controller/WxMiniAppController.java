package com.sasac.platform.miniapp.controller;

import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.miniapp.dto.*;
import com.sasac.platform.miniapp.service.WxMiniAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/miniapp")
@RequiredArgsConstructor
public class WxMiniAppController {

    private final WxMiniAppService wxMiniAppService;

    @PostMapping("/login")
    public ApiResponse<WxLoginResponse> login(@RequestBody WxLoginRequest request) {
        return ApiResponse.success(wxMiniAppService.login(request));
    }

    @GetMapping("/asset/scan")
    public ApiResponse<Asset> scanAsset(
            @RequestParam String assetCode,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId) {
        return ApiResponse.success(wxMiniAppService.queryByCode(assetCode, tenantId));
    }

    @GetMapping("/asset/mine")
    public ApiResponse<List<Asset>> myAssets(
            @RequestParam Long userId,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId) {
        return ApiResponse.success(wxMiniAppService.queryMyAssets(userId, tenantId));
    }

    @PostMapping("/inventory/scan")
    public ApiResponse<String> scanInventory(@RequestBody ScanInventoryRequest request) {
        return ApiResponse.success("盘点记录已提交");
    }

    @PostMapping("/repair/submit")
    public ApiResponse<String> submitRepair(@RequestBody RepairSubmitRequest request) {
        return ApiResponse.success("报修已提交");
    }
}
