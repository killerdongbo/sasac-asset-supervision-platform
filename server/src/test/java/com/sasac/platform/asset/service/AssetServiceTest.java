package com.sasac.platform.asset.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.dto.AssetQueryDTO;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AssetServiceTest {

    @Autowired
    private AssetService assetService;

    private AssetCreateDTO createDto;

    @BeforeEach
    void setUp() {
        createDto = new AssetCreateDTO();
        createDto.setName("办公服务器");
        createDto.setAssetCode("SRV-2024-001");
        createDto.setCategory("IT_EQUIPMENT");
        createDto.setOrgId(1L);
        createDto.setTenantId(0L);
        createDto.setSpecification("Dell PowerEdge R740");
        createDto.setUnit("台");
        createDto.setQuantity(2);
        createDto.setOriginalValue(new BigDecimal("50000.00"));
        createDto.setUseStatus("IN_USE");
        createDto.setCustodian("张三");
        createDto.setPurchaseDate(LocalDate.of(2024, 1, 15));
    }

    @Test
    void shouldCreateAndRetrieveAsset() {
        Asset created = assetService.create(createDto);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("办公服务器");
        assertThat(created.getAssetCode()).isEqualTo("SRV-2024-001");
        assertThat(created.getCurrentValue()).isEqualByComparingTo(new BigDecimal("50000.00"));
        assertThat(created.getAccumulatedDepreciation()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(created.getRegistrationDate()).isEqualTo(LocalDate.now());

        Asset found = assetService.getById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("办公服务器");
    }

    @Test
    void shouldQueryByOrgId() {
        assetService.create(createDto);

        AssetCreateDTO dto2 = new AssetCreateDTO();
        dto2.setName("办公家具");
        dto2.setAssetCode("FRN-2024-001");
        dto2.setCategory("FURNITURE");
        dto2.setOrgId(2L);
        dto2.setTenantId(0L);
        assetService.create(dto2);

        AssetQueryDTO query = new AssetQueryDTO();
        query.setOrgId(1L);
        query.setPage(1);
        query.setLimit(20);

        Page<Asset> results = assetService.query(query);
        assertThat(results.getRecords()).hasSize(1);
        assertThat(results.getRecords().get(0).getName()).isEqualTo("办公服务器");
    }

    @Test
    void shouldUpdateAsset() {
        Asset created = assetService.create(createDto);

        Asset update = new Asset();
        update.setName("升级版办公服务器");
        update.setUseStatus("IDLE");
        update.setCustodian("李四");

        Asset updated = assetService.update(created.getId(), update);
        assertThat(updated.getName()).isEqualTo("升级版办公服务器");
        assertThat(updated.getUseStatus()).isEqualTo("IDLE");
        assertThat(updated.getCustodian()).isEqualTo("李四");
        assertThat(updated.getAssetCode()).isEqualTo("SRV-2024-001");
    }

    @Test
    void shouldRejectDuplicateAssetCode() {
        assetService.create(createDto);

        AssetCreateDTO duplicate = new AssetCreateDTO();
        duplicate.setName("另一台服务器");
        duplicate.setAssetCode("SRV-2024-001");
        duplicate.setCategory("IT_EQUIPMENT");
        duplicate.setOrgId(1L);
        duplicate.setTenantId(0L);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                assetService.create(duplicate));
        assertThat(ex.getMessage()).contains("资产编码已存在");
    }

    @Test
    void shouldDeleteAsset() {
        Asset created = assetService.create(createDto);

        assetService.delete(created.getId());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                assetService.getById(created.getId()));
        assertThat(ex.getMessage()).contains("资产不存在");
    }
}
