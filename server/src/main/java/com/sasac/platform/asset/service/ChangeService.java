package com.sasac.platform.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.AssetChange;
import com.sasac.platform.asset.mapper.AssetChangeMapper;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for recording and querying asset change history.
 */
@Service
@RequiredArgsConstructor
public class ChangeService {

    private final AssetChangeMapper assetChangeMapper;
    private final AssetMapper assetMapper;

    /**
     * Records an asset change and updates the asset state accordingly.
     *
     * @param change the change record to save
     * @return the saved change record with generated ID
     * @throws BusinessException if the asset does not exist or change type is unsupported
     */
    @Transactional
    public AssetChange record(AssetChange change) {
        // Validate asset exists
        Asset asset = assetMapper.selectById(change.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // Capture current state as "from" values
        change.setFromOrgId(asset.getOrgId());
        change.setFromCustodian(asset.getCustodian());

        // Apply change based on type
        switch (change.getChangeType()) {
            case "STOCK_IN":
                asset.setUseStatus("IN_USE");
                if (change.getToOrgId() != null) {
                    asset.setOrgId(change.getToOrgId());
                }
                break;
            case "ASSIGNMENT":
                if (change.getToOrgId() != null) {
                    asset.setOrgId(change.getToOrgId());
                }
                break;
            case "TRANSFER":
                if (change.getToOrgId() != null) {
                    asset.setOrgId(change.getToOrgId());
                }
                break;
            case "MORTGAGE":
                asset.setUseStatus("MORTGAGED");
                break;
            case "RENTAL":
                asset.setUseStatus("RENTED");
                break;
            case "REPAIR":
                // Repair does not change status or org, just recorded
                break;
            case "STATUS_CHANGE":
                // Status change is a generic record, no specific asset field update
                break;
            default:
                throw new BusinessException("不支持的变动类型: " + change.getChangeType());
        }

        // Update custodian if provided
        if (change.getToCustodian() != null && !change.getToCustodian().isBlank()) {
            asset.setCustodian(change.getToCustodian());
        }

        // Persist changes
        assetMapper.updateById(asset);
        assetChangeMapper.insert(change);

        return change;
    }

    /**
     * Retrieves the change history for a given asset, ordered by creation time descending.
     *
     * @param assetId the asset ID
     * @return list of change records, newest first
     */
    public List<AssetChange> getHistory(Long assetId) {
        LambdaQueryWrapper<AssetChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetChange::getAssetId, assetId);
        wrapper.orderByDesc(AssetChange::getCreatedAt);
        return assetChangeMapper.selectList(wrapper);
    }
}
