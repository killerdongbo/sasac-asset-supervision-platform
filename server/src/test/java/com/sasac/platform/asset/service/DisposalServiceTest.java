package com.sasac.platform.asset.service;

import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.Disposal;
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
class DisposalServiceTest {

    @Autowired
    private DisposalService disposalService;

    @Autowired
    private AssetService assetService;

    private AssetCreateDTO createDto;

    @BeforeEach
    void setUp() {
        createDto = new AssetCreateDTO();
        createDto.setName("测试资产-处置");
        createDto.setAssetCode("DISP-TEST-" + System.nanoTime());
        createDto.setCategory("IT_EQUIPMENT");
        createDto.setOrgId(1L);
        createDto.setTenantId(0L);
        createDto.setOriginalValue(new BigDecimal("100000.00"));
        createDto.setUsefulLifeMonths(120);
        createDto.setResidualRate(new BigDecimal("0.05"));
        createDto.setDepreciationMethod("STRAIGHT_LINE");
        createDto.setUseStatus("IN_USE");
        createDto.setCustodian("张三");
        createDto.setPurchaseDate(LocalDate.of(2024, 1, 15));
    }

    @Test
    void shouldExecuteScrapDisposal() {
        // Given
        Asset asset = assetService.create(createDto);

        Disposal disposal = new Disposal();
        disposal.setAssetId(asset.getId());
        disposal.setDisposalType("SCRAP");
        disposal.setDisposalValue(BigDecimal.ZERO);
        disposal.setReason("设备报废");

        // When
        Disposal result = disposalService.execute(disposal);

        // Then: disposal record created with correct values
        assertThat(result.getId()).isNotNull();
        assertThat(result.getDisposalType()).isEqualTo("SCRAP");
        assertThat(result.getDisposalDate()).isEqualTo(LocalDate.now());
        assertThat(result.getBookValue()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(result.getDisposalValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getGainLoss()).isEqualByComparingTo(new BigDecimal("-100000.00"));

        // Then: asset status updated to DISPOSED
        Asset updatedAsset = assetService.getById(asset.getId());
        assertThat(updatedAsset.getUseStatus()).isEqualTo("DISPOSED");
    }

    @Test
    void shouldExecuteSellWithPositiveGain() {
        // Given: asset with depreciated currentValue
        Asset asset = assetService.create(createDto);
        asset.setCurrentValue(new BigDecimal("80000.00"));
        assetService.update(asset.getId(), asset);

        Disposal disposal = new Disposal();
        disposal.setAssetId(asset.getId());
        disposal.setDisposalType("SELL");
        disposal.setDisposalValue(new BigDecimal("90000.00"));
        disposal.setReason("资产出售");

        // When
        Disposal result = disposalService.execute(disposal);

        // Then: gainLoss is positive (90000 - 80000 = 10000)
        assertThat(result.getGainLoss()).isEqualByComparingTo(new BigDecimal("10000.00"));

        // Then: asset status updated to DISPOSED
        Asset updatedAsset = assetService.getById(asset.getId());
        assertThat(updatedAsset.getUseStatus()).isEqualTo("DISPOSED");
    }

    @Test
    void shouldThrowExceptionForAlreadyDisposedAsset() {
        // Given: asset already disposed
        Asset asset = assetService.create(createDto);

        Disposal firstDisposal = new Disposal();
        firstDisposal.setAssetId(asset.getId());
        firstDisposal.setDisposalType("SCRAP");
        firstDisposal.setDisposalValue(BigDecimal.ZERO);
        firstDisposal.setDisposalDate(LocalDate.now());
        disposalService.execute(firstDisposal);

        // When/Then: second disposal attempt throws BusinessException
        Disposal secondDisposal = new Disposal();
        secondDisposal.setAssetId(asset.getId());
        secondDisposal.setDisposalType("WRITE_OFF");
        secondDisposal.setDisposalValue(BigDecimal.ZERO);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                disposalService.execute(secondDisposal));
        assertThat(ex.getMessage()).contains("已处置");
    }

    @Test
    void shouldThrowExceptionForNonExistentAsset() {
        // Given
        Disposal disposal = new Disposal();
        disposal.setAssetId(-1L);
        disposal.setDisposalType("SCRAP");
        disposal.setDisposalValue(BigDecimal.ZERO);

        // When/Then
        BusinessException ex = assertThrows(BusinessException.class, () ->
                disposalService.execute(disposal));
        assertThat(ex.getMessage()).contains("资产不存在");
    }
}
