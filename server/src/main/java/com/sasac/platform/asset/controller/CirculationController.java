package com.sasac.platform.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.AssetChange;
import com.sasac.platform.asset.entity.Disposal;
import com.sasac.platform.asset.mapper.AssetChangeMapper;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.asset.mapper.DisposalMapper;
import com.sasac.platform.asset.service.ChangeService;
import com.sasac.platform.asset.vo.CirculationVO;
import com.sasac.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * REST controller for asset circulation operations:
 * stock-in, assignment, transfer, and disposal.
 * <p>
 * All GET endpoints return {@link CirculationVO} records which include
 * {@code assetCode} and {@code assetName} so the front-end always shows
 * human-readable identifiers consistent with the asset ledger.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CirculationController {

    private final AssetChangeMapper changeMapper;
    private final DisposalMapper disposalMapper;
    private final AssetMapper assetMapper;
    private final ChangeService changeService;

    // ──────────────────── Stock-In ────────────────────

    @GetMapping("/stock-ins")
    public ResponseEntity<ApiResponse<List<CirculationVO>>> listStockIns(
            @RequestParam(required = false) Long assetId) {
        List<AssetChange> changes = queryChanges("STOCK_IN", assetId);
        return ResponseEntity.ok(ApiResponse.success(enrichChanges(changes)));
    }

    @PostMapping("/stock-ins")
    public ResponseEntity<ApiResponse<CirculationVO>> createStockIn(
            @Valid @RequestBody AssetChange change) {
        change.setChangeType("STOCK_IN");
        AssetChange saved = changeService.record(change);
        return ResponseEntity.ok(ApiResponse.success(enrichOne(saved)));
    }

    // ──────────────────── Assignment ────────────────────

    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<List<CirculationVO>>> listAssignments(
            @RequestParam(required = false) Long assetId) {
        List<AssetChange> changes = queryChanges("ASSIGNMENT", assetId);
        return ResponseEntity.ok(ApiResponse.success(enrichChanges(changes)));
    }

    @PostMapping("/assignments")
    public ResponseEntity<ApiResponse<CirculationVO>> createAssignment(
            @Valid @RequestBody AssetChange change) {
        change.setChangeType("ASSIGNMENT");
        AssetChange saved = changeService.record(change);
        return ResponseEntity.ok(ApiResponse.success(enrichOne(saved)));
    }

    // ──────────────────── Transfer ────────────────────

    @GetMapping("/transfers")
    public ResponseEntity<ApiResponse<List<CirculationVO>>> listTransfers(
            @RequestParam(required = false) Long assetId) {
        List<AssetChange> changes = queryChanges("TRANSFER", assetId);
        return ResponseEntity.ok(ApiResponse.success(enrichChanges(changes)));
    }

    @PostMapping("/transfers")
    public ResponseEntity<ApiResponse<CirculationVO>> createTransfer(
            @Valid @RequestBody AssetChange change) {
        change.setChangeType("TRANSFER");
        AssetChange saved = changeService.record(change);
        return ResponseEntity.ok(ApiResponse.success(enrichOne(saved)));
    }

    // ──────────────────── Disposal ────────────────────

    @GetMapping("/disposals")
    public ResponseEntity<ApiResponse<List<CirculationVO>>> listDisposals(
            @RequestParam(required = false) Long assetId) {
        LambdaQueryWrapper<Disposal> wrapper = new LambdaQueryWrapper<>();
        if (assetId != null) {
            wrapper.eq(Disposal::getAssetId, assetId);
        }
        wrapper.orderByDesc(Disposal::getCreatedAt);
        List<Disposal> disposals = disposalMapper.selectList(wrapper);
        return ResponseEntity.ok(ApiResponse.success(enrichDisposals(disposals)));
    }

    @PostMapping("/disposals")
    public ResponseEntity<ApiResponse<CirculationVO>> createDisposal(
            @Valid @RequestBody Disposal disposal) {
        disposalMapper.insert(disposal);
        return ResponseEntity.ok(ApiResponse.success(enrichDisposal(disposal)));
    }

    // ──────────────────── Helper methods ────────────────────

    private List<AssetChange> queryChanges(String changeType, Long assetId) {
        LambdaQueryWrapper<AssetChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetChange::getChangeType, changeType);
        if (assetId != null) {
            wrapper.eq(AssetChange::getAssetId, assetId);
        }
        wrapper.orderByDesc(AssetChange::getCreatedAt);
        return changeMapper.selectList(wrapper);
    }

    /**
     * Enriches a list of AssetChange records with assetCode and assetName.
     */
    private List<CirculationVO> enrichChanges(List<AssetChange> changes) {
        if (changes.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Asset> assetMap = loadAssetMap(changes.stream()
                .map(AssetChange::getAssetId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        return changes.stream()
                .map(c -> toVO(c, assetMap.get(c.getAssetId())))
                .collect(Collectors.toList());
    }

    /**
     * Enriches a single AssetChange record with assetCode and assetName.
     */
    private CirculationVO enrichOne(AssetChange change) {
        Asset asset = assetMapper.selectById(change.getAssetId());
        return toVO(change, asset);
    }

    /**
     * Enriches disposal records with asset lookup.
     */
    private List<CirculationVO> enrichDisposals(List<Disposal> disposals) {
        if (disposals.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Asset> assetMap = loadAssetMap(disposals.stream()
                .map(Disposal::getAssetId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        return disposals.stream()
                .map(d -> toDisposalVO(d, assetMap.get(d.getAssetId())))
                .collect(Collectors.toList());
    }

    private CirculationVO enrichDisposal(Disposal disposal) {
        Asset asset = assetMapper.selectById(disposal.getAssetId());
        return toDisposalVO(disposal, asset);
    }

    private Map<Long, Asset> loadAssetMap(java.util.Set<Long> assetIds) {
        if (assetIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Asset> assets = assetMapper.selectBatchIds(assetIds);
        return assets.stream().collect(Collectors.toMap(Asset::getId, Function.identity()));
    }

    private CirculationVO toVO(AssetChange c, Asset a) {
        CirculationVO vo = CirculationVO.builder()
                .id(c.getId())
                .assetId(c.getAssetId())
                .changeType(c.getChangeType())
                .fromOrgId(c.getFromOrgId())
                .toOrgId(c.getToOrgId())
                .fromCustodian(c.getFromCustodian())
                .toCustodian(c.getToCustodian())
                .changeValue(c.getChangeValue())
                .changeDate(c.getChangeDate())
                .reason(c.getReason())
                .remark(c.getRemark())
                .operatorId(c.getOperatorId())
                .createdAt(c.getCreatedAt())
                .build();
        if (a != null) {
            vo.setAssetCode(a.getAssetCode());
            vo.setAssetName(a.getName());
        }
        return vo;
    }

    private CirculationVO toDisposalVO(Disposal d, Asset a) {
        CirculationVO vo = CirculationVO.builder()
                .id(d.getId())
                .assetId(d.getAssetId())
                .disposalType(d.getDisposalType())
                .disposalDate(d.getDisposalDate())
                .bookValue(d.getBookValue())
                .disposalValue(d.getDisposalValue())
                .gainLoss(d.getGainLoss())
                .reason(d.getReason())
                .createdAt(d.getCreatedAt())
                .build();
        if (a != null) {
            vo.setAssetCode(a.getAssetCode());
            vo.setAssetName(a.getName());
        }
        return vo;
    }
}
