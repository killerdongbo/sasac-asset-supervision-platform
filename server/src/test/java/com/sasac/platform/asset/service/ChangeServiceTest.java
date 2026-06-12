package com.sasac.platform.asset.service;

import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.AssetChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ChangeServiceTest {

    @Autowired
    private ChangeService changeService;

    @Autowired
    private AssetService assetService;

    private AssetCreateDTO createDto;

    @BeforeEach
    void setUp() {
        createDto = new AssetCreateDTO();
        createDto.setName("测试资产");
        createDto.setAssetCode("CHG-TEST-001");
        createDto.setCategory("IT_EQUIPMENT");
        createDto.setOrgId(1L);
        createDto.setTenantId(0L);
        createDto.setSpecification("Dell PowerEdge R740");
        createDto.setUnit("台");
        createDto.setQuantity(1);
        createDto.setOriginalValue(new BigDecimal("100000.00"));
        createDto.setUseStatus("IN_USE");
        createDto.setCustodian("张三");
        createDto.setPurchaseDate(LocalDate.of(2024, 1, 15));
    }

    @Test
    void shouldRecordTransferChangeAndUpdateAsset() {
        // Create an asset first
        Asset asset = assetService.create(createDto);

        // Record a TRANSFER change
        AssetChange change = new AssetChange();
        change.setAssetId(asset.getId());
        change.setChangeType("TRANSFER");
        change.setToOrgId(2L);
        change.setToCustodian("李四");
        change.setChangeValue(new BigDecimal("100000.00"));
        change.setChangeDate(LocalDate.now());
        change.setReason("组织架构调整");
        change.setRemark("从IT部门转移至行政部");
        change.setOperatorId("admin");

        AssetChange saved = changeService.record(change);

        // Verify change saved with generated ID
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAssetId()).isEqualTo(asset.getId());
        assertThat(saved.getChangeType()).isEqualTo("TRANSFER");
        assertThat(saved.getToOrgId()).isEqualTo(2L);
        assertThat(saved.getToCustodian()).isEqualTo("李四");

        // Verify asset orgId and custodian updated
        Asset updatedAsset = assetService.getById(asset.getId());
        assertThat(updatedAsset.getOrgId()).isEqualTo(2L);
        assertThat(updatedAsset.getCustodian()).isEqualTo("李四");

        // Verify getHistory returns the change ordered by createdAt desc
        List<AssetChange> history = changeService.getHistory(asset.getId());
        assertThat(history).isNotEmpty();
        assertThat(history.get(0).getChangeType()).isEqualTo("TRANSFER");
        assertThat(history.get(0).getToOrgId()).isEqualTo(2L);
    }

    @Test
    void shouldRecordMortgageChangeAndUpdateStatus() {
        Asset asset = assetService.create(createDto);

        AssetChange change = new AssetChange();
        change.setAssetId(asset.getId());
        change.setChangeType("MORTGAGE");
        change.setChangeValue(new BigDecimal("80000.00"));
        change.setChangeDate(LocalDate.now());
        change.setReason("银行抵押贷款");
        change.setOperatorId("admin");

        changeService.record(change);

        Asset updatedAsset = assetService.getById(asset.getId());
        assertThat(updatedAsset.getUseStatus()).isEqualTo("MORTGAGED");
    }

    @Test
    void shouldRecordRentalChangeAndUpdateStatus() {
        Asset asset = assetService.create(createDto);

        AssetChange change = new AssetChange();
        change.setAssetId(asset.getId());
        change.setChangeType("RENTAL");
        change.setToOrgId(3L);
        change.setChangeValue(new BigDecimal("5000.00"));
        change.setChangeDate(LocalDate.now());
        change.setReason("对外租赁");
        change.setOperatorId("admin");

        changeService.record(change);

        Asset updatedAsset = assetService.getById(asset.getId());
        assertThat(updatedAsset.getUseStatus()).isEqualTo("RENTED");
    }

    @Test
    void shouldReturnEmptyHistoryForNonExistentAsset() {
        List<AssetChange> history = changeService.getHistory(-1L);
        assertThat(history).isEmpty();
    }
}
