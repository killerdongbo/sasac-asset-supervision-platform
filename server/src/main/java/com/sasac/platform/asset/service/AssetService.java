package com.sasac.platform.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.dto.AssetQueryDTO;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.ledger.entity.AssetChangeLog;
import com.sasac.platform.asset.ledger.mapper.AssetChangeLogMapper;
import com.sasac.platform.asset.lifecycle.service.LifecycleEventPublisher;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service for asset CRUD operations.
 */
@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetMapper assetMapper;
    private final AssetChangeLogMapper assetChangeLogMapper;
    private final LifecycleEventPublisher lifecycleEventPublisher;

    /**
     * Creates a new asset from the provided DTO.
     *
     * @param dto the asset creation data
     * @return the created asset with generated ID
     * @throws BusinessException if the asset code already exists
     */
    @Transactional
    public Asset create(AssetCreateDTO dto) {
        // Check asset code uniqueness
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Asset::getAssetCode, dto.getAssetCode());
        Long count = assetMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("资产编码已存在");
        }

        Asset asset = new Asset();
        BeanUtils.copyProperties(dto, asset);

        // Set depreciation defaults
        asset.setCurrentValue(dto.getOriginalValue());
        asset.setAccumulatedDepreciation(BigDecimal.ZERO);

        // Set registration date to today if not provided
        if (asset.getRegistrationDate() == null) {
            asset.setRegistrationDate(LocalDate.now());
        }

        assetMapper.insert(asset);

        lifecycleEventPublisher.publish(
                0L, asset.getId(), "STOCK_IN",
                "资产入库登记：" + asset.getName(),
                null, "asset", asset.getId(),
                null, null);

        return asset;
    }

    /**
     * Retrieves an asset by ID.
     *
     * @param id the asset ID
     * @return the asset
     * @throws BusinessException if the asset is not found
     */
    public Asset getById(Long id) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        return asset;
    }

    /**
     * Queries assets with dynamic filters and pagination.
     *
     * @param query the query DTO containing filters
     * @return list of matching assets
     */
    public List<Asset> query(AssetQueryDTO query) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();

        if (query.getOrgId() != null) {
            wrapper.eq(Asset::getOrgId, query.getOrgId());
        }
        if (query.getCategory() != null && !query.getCategory().isBlank()) {
            wrapper.eq(Asset::getCategory, query.getCategory());
        }
        if (query.getUseStatus() != null && !query.getUseStatus().isBlank()) {
            wrapper.eq(Asset::getUseStatus, query.getUseStatus());
        }
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w
                    .like(Asset::getName, query.getKeyword())
                    .or()
                    .like(Asset::getAssetCode, query.getKeyword())
            );
        }

        wrapper.orderByDesc(Asset::getId);

        Page<Asset> page = assetMapper.selectPage(
                new Page<>(query.getPage(), query.getLimit()),
                wrapper
        );
        return page.getRecords();
    }

    /**
     * Updates an existing asset and writes a {@link AssetChangeLog} entry for
     * every field whose value changed.
     *
     * @param id     the asset ID to update
     * @param update the object containing fields to update
     * @return the updated asset
     * @throws BusinessException if the asset is not found
     */
    @Transactional
    public Asset update(Long id, Asset update) {
        Asset existing = getById(id);

        // Capture old values before overwriting
        compareAndLogChanges(existing, update, id);

        BeanUtils.copyProperties(update, existing, "id", "assetCode", "tenantId",
                "createdAt", "updatedAt", "deleted");

        assetMapper.updateById(existing);

        // Return a fresh copy from the database
        return assetMapper.selectById(id);
    }

    /**
     * Compares old and new asset values and persists an {@link AssetChangeLog}
     * entry for each field that differs.
     */
    private void compareAndLogChanges(Asset oldAsset, Asset newAsset, Long assetId) {
        List<AssetChangeLog> logs = new ArrayList<>();

        // Compare only mutable fields that can change via update
        compareField(logs, "name", oldAsset.getName(), newAsset.getName(), assetId);
        compareField(logs, "category", oldAsset.getCategory(), newAsset.getCategory(), assetId);
        compareField(logs, "specification", oldAsset.getSpecification(), newAsset.getSpecification(), assetId);
        compareField(logs, "unit", oldAsset.getUnit(), newAsset.getUnit(), assetId);
        compareField(logs, "quantity", oldAsset.getQuantity(), newAsset.getQuantity(), assetId);
        compareField(logs, "originalValue", oldAsset.getOriginalValue(), newAsset.getOriginalValue(), assetId);
        compareField(logs, "currentValue", oldAsset.getCurrentValue(), newAsset.getCurrentValue(), assetId);
        compareField(logs, "accumulatedDepreciation", oldAsset.getAccumulatedDepreciation(), newAsset.getAccumulatedDepreciation(), assetId);
        compareField(logs, "depreciationMethod", oldAsset.getDepreciationMethod(), newAsset.getDepreciationMethod(), assetId);
        compareField(logs, "usefulLifeMonths", oldAsset.getUsefulLifeMonths(), newAsset.getUsefulLifeMonths(), assetId);
        compareField(logs, "residualRate", oldAsset.getResidualRate(), newAsset.getResidualRate(), assetId);
        compareField(logs, "useStatus", oldAsset.getUseStatus(), newAsset.getUseStatus(), assetId);
        compareField(logs, "useDepartment", oldAsset.getUseDepartment(), newAsset.getUseDepartment(), assetId);
        compareField(logs, "custodian", oldAsset.getCustodian(), newAsset.getCustodian(), assetId);
        compareField(logs, "location", oldAsset.getLocation(), newAsset.getLocation(), assetId);
        compareField(logs, "address", oldAsset.getAddress(), newAsset.getAddress(), assetId);
        compareField(logs, "purchaseDate", oldAsset.getPurchaseDate(), newAsset.getPurchaseDate(), assetId);
        compareField(logs, "registrationDate", oldAsset.getRegistrationDate(), newAsset.getRegistrationDate(), assetId);
        compareField(logs, "sourceType", oldAsset.getSourceType(), newAsset.getSourceType(), assetId);
        compareField(logs, "certificateNo", oldAsset.getCertificateNo(), newAsset.getCertificateNo(), assetId);
        compareField(logs, "photoIds", oldAsset.getPhotoIds(), newAsset.getPhotoIds(), assetId);
        compareField(logs, "remark", oldAsset.getRemark(), newAsset.getRemark(), assetId);

        if (!logs.isEmpty()) {
            logs.forEach(assetChangeLogMapper::insert);
        }
    }

    private void compareField(List<AssetChangeLog> logs, String fieldName,
                               Object before, Object after, Long assetId) {
        String beforeStr = toStringSafe(before);
        String afterStr = toStringSafe(after);

        if (!Objects.equals(beforeStr, afterStr)) {
            AssetChangeLog log = new AssetChangeLog();
            log.setAssetId(assetId);
            log.setChangeField(fieldName);
            log.setBeforeValue(beforeStr);
            log.setAfterValue(afterStr);
            logs.add(log);
        }
    }

    private static String toStringSafe(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return value.toString();
    }

    /**
     * Soft-deletes an asset by ID.
     *
     * @param id the asset ID to delete
     * @throws BusinessException if the asset is not found
     */
    @Transactional
    public void delete(Long id) {
        getById(id);
        assetMapper.deleteById(id);
    }
}
