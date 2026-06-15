package com.sasac.platform.asset.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.asset.lifecycle.dto.LifecycleSummaryDTO;
import com.sasac.platform.asset.lifecycle.entity.AssetLifecycleEvent;
import com.sasac.platform.asset.lifecycle.service.AssetLifecycleService;
import com.sasac.platform.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assets/{assetId}/lifecycle")
@RequiredArgsConstructor
public class AssetLifecycleController {

    private final AssetLifecycleService lifecycleService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AssetLifecycleEvent>>> getTimeline(
            @PathVariable Long assetId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String eventType,
            @RequestParam(defaultValue = "false") boolean ascending) {
        // TODO: extract tenantId from security context in production
        Long tenantId = 0L;
        Page<AssetLifecycleEvent> result = lifecycleService.getTimeline(
                tenantId, assetId, eventType, page, size, ascending);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<LifecycleSummaryDTO>> getSummary(
            @PathVariable Long assetId) {
        Long tenantId = 0L;
        LifecycleSummaryDTO summary = lifecycleService.getSummary(tenantId, assetId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
