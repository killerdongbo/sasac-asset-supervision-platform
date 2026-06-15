package com.sasac.platform.asset.label.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.label.dto.LabelDataDTO;
import com.sasac.platform.asset.label.service.AssetLabelService;
import com.sasac.platform.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets/labels")
@RequiredArgsConstructor
public class AssetLabelController {

    private final AssetLabelService assetLabelService;

    @GetMapping("/{assetId}")
    public ResponseEntity<ApiResponse<LabelDataDTO>> getLabelData(@PathVariable Long assetId) {
        LabelDataDTO data = assetLabelService.getLabelData(assetId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<LabelDataDTO>>> batchGetLabelData(
            @RequestBody List<Long> assetIds) {
        List<LabelDataDTO> data = assetLabelService.batchGetLabelData(assetIds);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/mark-printed")
    public ResponseEntity<ApiResponse<Void>> markPrinted(@RequestBody List<Long> assetIds) {
        assetLabelService.markPrinted(assetIds);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Asset>>> listByStatus(
            @RequestParam(required = false) String labelStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long tenantId = 0L;
        Page<Asset> result = assetLabelService.listByLabelStatus(tenantId, labelStatus, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
