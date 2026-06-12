package com.sasac.platform.asset.ledger.service;

import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.ledger.entity.AssetLabel;
import com.sasac.platform.asset.service.AssetService;
import com.sasac.platform.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AssetLabelServiceTest {

    @Autowired
    private AssetLabelService assetLabelService;

    @Autowired
    private AssetService assetService;

    private Asset asset;

    @BeforeEach
    void setUp() {
        AssetCreateDTO dto = new AssetCreateDTO();
        dto.setName("测试资产标签");
        dto.setAssetCode("LABEL-TEST-001");
        dto.setCategory("IT_EQUIPMENT");
        dto.setOrgId(1L);
        dto.setTenantId(0L);
        dto.setSpecification("Test Spec");
        dto.setUnit("台");
        dto.setQuantity(1);
        dto.setOriginalValue(new BigDecimal("10000.00"));
        dto.setUseStatus("IN_USE");
        dto.setCustodian("张三");
        dto.setPurchaseDate(LocalDate.of(2024, 1, 15));
        asset = assetService.create(dto);
    }

    @Test
    void shouldGenerateLabel() {
        AssetLabel label = assetLabelService.generateLabel(asset.getId(), 0L);

        assertThat(label.getId()).isNotNull();
        assertThat(label.getAssetId()).isEqualTo(asset.getId());
        assertThat(label.getLabelCode()).isNotBlank();
        assertThat(label.getTenantId()).isEqualTo(0L);
        assertThat(label.getPrintCount()).isEqualTo(0);
        assertThat(label.getLastPrintTime()).isNull();
    }

    @Test
    void shouldRecordPrint() {
        AssetLabel label = assetLabelService.generateLabel(asset.getId(), 0L);
        assertThat(label.getPrintCount()).isEqualTo(0);

        AssetLabel printed = assetLabelService.recordPrint(label.getId());
        assertThat(printed.getPrintCount()).isEqualTo(1);
        assertThat(printed.getLastPrintTime()).isNotNull();
        assertThat(printed.getLastPrintTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldThrowWhenAssetNotFoundForLabelGeneration() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                assetLabelService.generateLabel(-1L, 0L));
        assertThat(ex.getMessage()).contains("资产不存在");
    }

    @Test
    void shouldThrowWhenLabelNotFoundForPrint() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                assetLabelService.recordPrint(-1L));
        assertThat(ex.getMessage()).contains("标签不存在");
    }
}
