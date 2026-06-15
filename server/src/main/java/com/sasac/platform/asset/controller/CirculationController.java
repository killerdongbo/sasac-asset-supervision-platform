package com.sasac.platform.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.AssetChange;
import com.sasac.platform.asset.entity.Disposal;
import com.sasac.platform.asset.mapper.AssetChangeMapper;
import com.sasac.platform.asset.mapper.DisposalMapper;
import com.sasac.platform.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for asset circulation operations:
 * stock-in, assignment, transfer, and disposal.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CirculationController {

    private final AssetChangeMapper changeMapper;
    private final DisposalMapper disposalMapper;

    /**
     * Lists stock-in records.
     *
     * @param assetId optional filter by asset ID
     * @return list of stock-in change records
     */
    @GetMapping("/stock-ins")
    public ResponseEntity<ApiResponse<List<AssetChange>>> listStockIns(
            @RequestParam(required = false) Long assetId) {
        LambdaQueryWrapper<AssetChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetChange::getChangeType, "STOCK_IN");
        if (assetId != null) {
            wrapper.eq(AssetChange::getAssetId, assetId);
        }
        wrapper.orderByDesc(AssetChange::getCreatedAt);
        return ResponseEntity.ok(ApiResponse.success(changeMapper.selectList(wrapper)));
    }

    /**
     * Lists assignment records.
     *
     * @param assetId optional filter by asset ID
     * @return list of assignment change records
     */
    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<List<AssetChange>>> listAssignments(
            @RequestParam(required = false) Long assetId) {
        LambdaQueryWrapper<AssetChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetChange::getChangeType, "ASSIGNMENT");
        if (assetId != null) {
            wrapper.eq(AssetChange::getAssetId, assetId);
        }
        wrapper.orderByDesc(AssetChange::getCreatedAt);
        return ResponseEntity.ok(ApiResponse.success(changeMapper.selectList(wrapper)));
    }

    /**
     * Lists transfer records.
     *
     * @param assetId optional filter by asset ID
     * @return list of transfer change records
     */
    @GetMapping("/transfers")
    public ResponseEntity<ApiResponse<List<AssetChange>>> listTransfers(
            @RequestParam(required = false) Long assetId) {
        LambdaQueryWrapper<AssetChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetChange::getChangeType, "TRANSFER");
        if (assetId != null) {
            wrapper.eq(AssetChange::getAssetId, assetId);
        }
        wrapper.orderByDesc(AssetChange::getCreatedAt);
        return ResponseEntity.ok(ApiResponse.success(changeMapper.selectList(wrapper)));
    }

    /**
     * Lists disposal records.
     *
     * @param assetId optional filter by asset ID
     * @return list of disposal records
     */
    @GetMapping("/disposals")
    public ResponseEntity<ApiResponse<List<Disposal>>> listDisposals(
            @RequestParam(required = false) Long assetId) {
        LambdaQueryWrapper<Disposal> wrapper = new LambdaQueryWrapper<>();
        if (assetId != null) {
            wrapper.eq(Disposal::getAssetId, assetId);
        }
        wrapper.orderByDesc(Disposal::getCreatedAt);
        return ResponseEntity.ok(ApiResponse.success(disposalMapper.selectList(wrapper)));
    }
}
