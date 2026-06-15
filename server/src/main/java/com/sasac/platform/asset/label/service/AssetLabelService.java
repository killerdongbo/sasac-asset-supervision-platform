package com.sasac.platform.asset.label.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.label.dto.LabelDataDTO;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetLabelService {

    private final AssetMapper assetMapper;
    private final ObjectMapper objectMapper;

    public LabelDataDTO getLabelData(Long assetId) {
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        return buildLabelData(asset);
    }

    public List<LabelDataDTO> batchGetLabelData(List<Long> assetIds) {
        if (assetIds == null || assetIds.isEmpty()) {
            throw new BusinessException("资产ID列表不能为空");
        }
        List<Asset> assets = assetMapper.selectBatchIds(assetIds);
        return assets.stream().map(this::buildLabelData).toList();
    }

    public void markPrinted(List<Long> assetIds) {
        if (assetIds == null || assetIds.isEmpty()) return;

        List<Asset> assets = assetMapper.selectBatchIds(assetIds);
        for (Asset asset : assets) {
            asset.setLabelStatus("PRINTED");
            asset.setPrintCount(asset.getPrintCount() + 1);
            asset.setLastPrintTime(LocalDateTime.now());
            assetMapper.updateById(asset);
        }
    }

    public Page<Asset> listByLabelStatus(Long tenantId, String labelStatus, int page, int size) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, tenantId)
                .eq(labelStatus != null, Asset::getLabelStatus, labelStatus)
                .orderByDesc(Asset::getCreatedAt);
        return assetMapper.selectPage(new Page<>(page, size), wrapper);
    }

    private LabelDataDTO buildLabelData(Asset asset) {
        String qrContent = generateQrContent(asset);
        return LabelDataDTO.builder()
                .assetId(asset.getId())
                .assetCode(asset.getAssetCode())
                .assetName(asset.getName())
                .category(asset.getCategory())
                .orgName(asset.getUseDepartment())
                .location(asset.getLocation())
                .custodian(asset.getCustodian())
                .qrContent(qrContent)
                .barcode(asset.getAssetCode())
                .build();
    }

    private String generateQrContent(Asset asset) {
        try {
            Map<String, Object> data = Map.of(
                    "id", asset.getId(),
                    "code", asset.getAssetCode(),
                    "name", asset.getName()
            );
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.warn("Failed to generate QR content for asset {}", asset.getId(), e);
            return asset.getAssetCode();
        }
    }
}
