package com.sasac.platform.asset.service;

import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.Depreciation;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.asset.mapper.DepreciationMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Service for calculating and recording monthly asset depreciation.
 */
@Service
@RequiredArgsConstructor
public class DepreciationService {

    private final DepreciationMapper depreciationMapper;
    private final AssetMapper assetMapper;

    /**
     * Calculate the monthly depreciation amount for an asset using the straight-line method.
     * <p>
     * Formula: monthly = (originalValue * (1 - residualRate)) / usefulLifeMonths
     *
     * @param asset the asset to calculate depreciation for
     * @return the monthly depreciation amount, or zero if originalValue is null or zero
     */
    public BigDecimal calcMonthlyDepreciation(Asset asset) {
        if (asset.getOriginalValue() == null
                || asset.getOriginalValue().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal residualRate = asset.getResidualRate() != null
                ? asset.getResidualRate()
                : BigDecimal.ZERO;

        // For STRAIGHT_LINE and any other method, default to straight-line calculation
        BigDecimal netValue = asset.getOriginalValue()
                .multiply(BigDecimal.ONE.subtract(residualRate));
        return netValue.divide(
                BigDecimal.valueOf(asset.getUsefulLifeMonths()),
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Run monthly depreciation for a specific asset.
     * <p>
     * Calculates the depreciation amount, creates a depreciation record,
     * and updates the asset's accumulated depreciation and current value.
     *
     * @param assetId the asset ID
     * @return the created Depreciation record
     * @throws BusinessException if the asset does not exist or is disposed
     */
    @Transactional
    public Depreciation runMonthlyDepreciation(Long assetId) {
        // Validate asset exists
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // Validate asset is not disposed
        if ("DISPOSED".equals(asset.getUseStatus())) {
            throw new BusinessException("已处置资产不计提折旧");
        }

        // Calculate monthly depreciation
        BigDecimal monthlyDepreciation = calcMonthlyDepreciation(asset);

        // Determine before and after values
        BigDecimal beforeValue = asset.getCurrentValue() != null
                ? asset.getCurrentValue()
                : BigDecimal.ZERO;

        BigDecimal residualValue = asset.getOriginalValue()
                .multiply(asset.getResidualRate() != null
                        ? asset.getResidualRate()
                        : BigDecimal.ZERO);

        BigDecimal potentialAfterValue = beforeValue.subtract(monthlyDepreciation);
        BigDecimal afterValue;

        // Cap depreciation so current value does not fall below residual value
        if (potentialAfterValue.compareTo(residualValue) < 0) {
            BigDecimal maxDepreciation = beforeValue.subtract(residualValue);
            if (maxDepreciation.compareTo(BigDecimal.ZERO) > 0) {
                monthlyDepreciation = maxDepreciation;
            } else {
                monthlyDepreciation = BigDecimal.ZERO;
            }
            afterValue = beforeValue.subtract(monthlyDepreciation);
        } else {
            afterValue = potentialAfterValue;
        }

        // Build period string
        String period = YearMonth.now().toString();

        // Create depreciation record
        Depreciation depreciation = new Depreciation();
        depreciation.setAssetId(assetId);
        depreciation.setDepreciationAmount(monthlyDepreciation);
        depreciation.setDepreciationDate(LocalDate.now());
        depreciation.setBeforeValue(beforeValue);
        depreciation.setAfterValue(afterValue);
        depreciation.setPeriod(period);

        // Update asset
        BigDecimal currentAccumulated = asset.getAccumulatedDepreciation() != null
                ? asset.getAccumulatedDepreciation()
                : BigDecimal.ZERO;
        asset.setAccumulatedDepreciation(currentAccumulated.add(monthlyDepreciation));
        asset.setCurrentValue(afterValue);

        // Persist
        depreciationMapper.insert(depreciation);
        assetMapper.updateById(asset);

        return depreciation;
    }
}
