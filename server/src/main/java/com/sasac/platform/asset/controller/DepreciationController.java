package com.sasac.platform.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Depreciation;
import com.sasac.platform.asset.mapper.DepreciationMapper;
import com.sasac.platform.asset.service.DepreciationService;
import com.sasac.platform.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for asset depreciation management.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepreciationController {

    private final DepreciationMapper depreciationMapper;
    private final DepreciationService depreciationService;

    /**
     * Lists all depreciation records, optionally filtered by asset ID.
     *
     * @param assetId optional asset ID filter
     * @return list of depreciation records
     */
    @GetMapping("/depreciation-records")
    public ResponseEntity<ApiResponse<List<Depreciation>>> listRecords(
            @RequestParam(required = false) Long assetId) {
        LambdaQueryWrapper<Depreciation> wrapper = new LambdaQueryWrapper<>();
        if (assetId != null) {
            wrapper.eq(Depreciation::getAssetId, assetId);
        }
        wrapper.orderByDesc(Depreciation::getDepreciationDate);
        return ResponseEntity.ok(ApiResponse.success(depreciationMapper.selectList(wrapper)));
    }

    /**
     * Runs monthly depreciation for a specific asset.
     *
     * @param assetId the asset ID
     * @return the created depreciation record
     */
    @PostMapping("/assets/{id}/depreciate")
    public ResponseEntity<ApiResponse<Depreciation>> runDepreciation(
            @PathVariable("id") Long assetId) {
        Depreciation record = depreciationService.runMonthlyDepreciation(assetId);
        return ResponseEntity.ok(ApiResponse.success(record));
    }
}
