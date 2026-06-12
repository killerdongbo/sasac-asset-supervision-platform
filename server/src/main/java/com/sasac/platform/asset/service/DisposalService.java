package com.sasac.platform.asset.service;

import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.Disposal;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.asset.mapper.DisposalMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Service for executing asset disposals (scrap, sell, write-off).
 */
@Service
@RequiredArgsConstructor
public class DisposalService {

    private final DisposalMapper disposalMapper;
    private final AssetMapper assetMapper;

    /**
     * Executes an asset disposal.
     * <p>
     * Validates the asset exists and is not already disposed, calculates
     * gain/loss, updates the asset status to DISPOSED, and records the
     * disposal transaction.
     *
     * @param disposal the disposal details
     * @return the saved disposal record with generated ID
     * @throws BusinessException if the asset does not exist or is already disposed
     */
    @Transactional
    public Disposal execute(Disposal disposal) {
        // Validate asset exists
        Asset asset = assetMapper.selectById(disposal.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // Validate asset is not already disposed
        if ("DISPOSED".equals(asset.getUseStatus())) {
            throw new BusinessException("资产已处置，不能重复处置");
        }

        // Set disposal date to now if not provided
        if (disposal.getDisposalDate() == null) {
            disposal.setDisposalDate(LocalDate.now());
        }

        // Set book value from asset's current value
        BigDecimal bookValue = asset.getCurrentValue() != null
                ? asset.getCurrentValue()
                : BigDecimal.ZERO;
        disposal.setBookValue(bookValue);

        // Calculate gain/loss
        BigDecimal disposalValue = disposal.getDisposalValue() != null
                ? disposal.getDisposalValue()
                : BigDecimal.ZERO;
        disposal.setGainLoss(disposalValue.subtract(bookValue));

        // Update asset status
        asset.setUseStatus("DISPOSED");
        assetMapper.updateById(asset);

        // Save disposal record
        disposalMapper.insert(disposal);

        return disposal;
    }
}
