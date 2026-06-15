package com.sasac.platform.asset.ledger.service;

import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.ledger.entity.AssetLabel;
import com.sasac.platform.asset.ledger.mapper.AssetLabelMapper;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for generating and managing asset labels.
 * <p>
 * Labels use UUID-based codes and track print counts for audit purposes.
 */
@Service("ledgerAssetLabelService")
@RequiredArgsConstructor
public class AssetLabelService {

    private final AssetLabelMapper assetLabelMapper;
    private final AssetMapper assetMapper;

    /**
     * Generates a new label for the specified asset.
     * <p>
     * The label is assigned a UUID-based unique code and initialised
     * with a print count of zero.
     *
     * @param assetId  the asset to label
     * @param tenantId the tenant context
     * @return the newly created label
     * @throws BusinessException if the asset does not exist
     */
    @Transactional
    public AssetLabel generateLabel(Long assetId, Long tenantId) {
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        String labelCode = UUID.randomUUID().toString();

        AssetLabel label = new AssetLabel();
        label.setAssetId(assetId);
        label.setLabelCode(labelCode);
        label.setBarcodeData(labelCode);
        label.setPrintCount(0);
        label.setTenantId(tenantId);

        assetLabelMapper.insert(label);
        return label;
    }

    /**
     * Records a print event for the given label.
     * <p>
     * Increments the print count and sets the last print time to now.
     *
     * @param labelId the label ID
     * @return the updated label
     * @throws BusinessException if the label does not exist
     */
    @Transactional
    public AssetLabel recordPrint(Long labelId) {
        AssetLabel label = assetLabelMapper.selectById(labelId);
        if (label == null) {
            throw new BusinessException("标签不存在");
        }

        label.setPrintCount(label.getPrintCount() == null ? 1 : label.getPrintCount() + 1);
        label.setLastPrintTime(LocalDateTime.now());

        assetLabelMapper.updateById(label);
        return label;
    }
}
