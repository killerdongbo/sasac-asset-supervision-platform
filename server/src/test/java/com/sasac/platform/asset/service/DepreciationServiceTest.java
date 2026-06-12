package com.sasac.platform.asset.service;

import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.Depreciation;
import com.sasac.platform.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DepreciationServiceTest {

    @Autowired
    private DepreciationService depreciationService;

    @Autowired
    private AssetService assetService;

    private AssetCreateDTO createDto;

    @BeforeEach
    void setUp() {
        createDto = new AssetCreateDTO();
        createDto.setName("测试资产-折旧");
        createDto.setAssetCode("DEPR-TEST-001");
        createDto.setCategory("IT_EQUIPMENT");
        createDto.setOrgId(1L);
        createDto.setTenantId(0L);
        createDto.setOriginalValue(new BigDecimal("120000.00"));
        createDto.setUsefulLifeMonths(120);
        createDto.setResidualRate(new BigDecimal("0.05"));
        createDto.setDepreciationMethod("STRAIGHT_LINE");
        createDto.setUseStatus("IN_USE");
        createDto.setCustodian("张三");
        createDto.setPurchaseDate(LocalDate.of(2024, 1, 15));
    }

    @Test
    void shouldCalculateStraightLineMonthlyDepreciation() {
        // Given
        Asset asset = assetService.create(createDto);

        // When
        BigDecimal monthly = depreciationService.calcMonthlyDepreciation(asset);

        // Then: (120000 * 0.95) / 120 = 950.00
        assertThat(monthly).isEqualByComparingTo(new BigDecimal("950.00"));
    }

    @Test
    void shouldRunDepreciationAndUpdateAssetValues() {
        // Given
        Asset asset = assetService.create(createDto);

        // When
        Depreciation depreciation = depreciationService.runMonthlyDepreciation(asset.getId());

        // Then: depreciation record created with correct amounts
        assertThat(depreciation.getId()).isNotNull();
        assertThat(depreciation.getAssetId()).isEqualTo(asset.getId());
        assertThat(depreciation.getDepreciationAmount()).isEqualByComparingTo(new BigDecimal("950.00"));
        assertThat(depreciation.getBeforeValue()).isEqualByComparingTo(new BigDecimal("120000.00"));
        assertThat(depreciation.getAfterValue()).isEqualByComparingTo(new BigDecimal("119050.00"));

        // Then: asset values updated
        Asset updatedAsset = assetService.getById(asset.getId());
        assertThat(updatedAsset.getAccumulatedDepreciation()).isEqualByComparingTo(new BigDecimal("950.00"));
        assertThat(updatedAsset.getCurrentValue()).isEqualByComparingTo(new BigDecimal("119050.00"));
    }

    @Test
    void shouldNotDepreciateBelowResidualValue() {
        // Given: asset close to residual value (residual = 120000 * 0.05 = 6000)
        Asset asset = assetService.create(createDto);
        asset.setCurrentValue(new BigDecimal("6500.00"));
        asset.setAccumulatedDepreciation(new BigDecimal("113500.00"));
        assetService.update(asset.getId(), asset);

        // When
        Depreciation depreciation = depreciationService.runMonthlyDepreciation(asset.getId());

        // Then: depreciation should be capped so currentValue doesn't go below residual (6000)
        assertThat(depreciation.getDepreciationAmount()).isEqualByComparingTo(new BigDecimal("500.00"));

        Asset updatedAsset = assetService.getById(asset.getId());
        assertThat(updatedAsset.getCurrentValue()).isEqualByComparingTo(new BigDecimal("6000.00"));
    }

    @Test
    void shouldThrowExceptionForDisposedAsset() {
        // Given: disposed asset
        Asset asset = assetService.create(createDto);
        asset.setUseStatus("DISPOSED");
        assetService.update(asset.getId(), asset);

        // When/Then
        BusinessException ex = assertThrows(BusinessException.class, () ->
                depreciationService.runMonthlyDepreciation(asset.getId()));
        assertThat(ex.getMessage()).contains("已处置资产不计提折旧");
    }

    @Test
    void shouldThrowExceptionForNonExistentAsset() {
        // When/Then
        BusinessException ex = assertThrows(BusinessException.class, () ->
                depreciationService.runMonthlyDepreciation(-1L));
        assertThat(ex.getMessage()).contains("资产不存在");
    }

    @Test
    void shouldReturnZeroForNullOriginalValue() {
        // Given: asset with null original value
        Asset asset = assetService.create(createDto);
        asset.setOriginalValue(null);
        assetService.update(asset.getId(), asset);

        // When
        BigDecimal monthly = depreciationService.calcMonthlyDepreciation(asset);

        // Then
        assertThat(monthly).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldReturnZeroForZeroOriginalValue() {
        // Given: asset with zero original value
        Asset asset = assetService.create(createDto);
        asset.setOriginalValue(BigDecimal.ZERO);
        assetService.update(asset.getId(), asset);

        // When
        BigDecimal monthly = depreciationService.calcMonthlyDepreciation(asset);

        // Then
        assertThat(monthly).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
