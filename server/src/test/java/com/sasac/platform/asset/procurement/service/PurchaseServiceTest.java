package com.sasac.platform.asset.procurement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.procurement.dto.PurchaseRequestCreateDTO;
import com.sasac.platform.asset.procurement.entity.PurchaseAcceptance;
import com.sasac.platform.asset.procurement.entity.PurchaseRequest;
import com.sasac.platform.asset.procurement.mapper.PurchaseAcceptanceMapper;
import com.sasac.platform.asset.procurement.mapper.PurchaseRequestMapper;
import com.sasac.platform.asset.service.AssetService;
import com.sasac.platform.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PurchaseServiceTest {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private PurchaseRequestMapper purchaseRequestMapper;

    @Autowired
    private PurchaseAcceptanceMapper purchaseAcceptanceMapper;

    @Autowired
    private AssetService assetService;

    private PurchaseRequestCreateDTO createDto;

    @BeforeEach
    void setUp() {
        createDto = new PurchaseRequestCreateDTO();
        createDto.setAssetName("测试服务器");
        createDto.setQuantity(5);
        createDto.setBudget(new BigDecimal("100000.00"));
        createDto.setSupplierId(1L);
        createDto.setOrgId(1L);
        createDto.setTenantId(0L);
        createDto.setRequestReason("业务扩展需要");
        createDto.setRemark("紧急采购");
    }

    @Test
    void shouldCreatePurchaseRequest() {
        PurchaseRequest request = purchaseService.createRequest(createDto);

        assertThat(request.getId()).isNotNull();
        assertThat(request.getAssetName()).isEqualTo("测试服务器");
        assertThat(request.getQuantity()).isEqualTo(5);
        assertThat(request.getBudget()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(request.getSupplierId()).isEqualTo(1L);
        assertThat(request.getOrgId()).isEqualTo(1L);
        assertThat(request.getTenantId()).isEqualTo(0L);
        assertThat(request.getRequestReason()).isEqualTo("业务扩展需要");
        assertThat(request.getRemark()).isEqualTo("紧急采购");
        assertThat(request.getStatus()).isEqualTo("PENDING");

        // Verify persisted
        PurchaseRequest found = purchaseRequestMapper.selectById(request.getId());
        assertThat(found).isNotNull();
        assertThat(found.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void shouldAcceptPurchaseRequest() {
        PurchaseRequest request = purchaseService.createRequest(createDto);

        // Accept with pass
        PurchaseAcceptance acceptance = purchaseService.accept(request.getId(), true, "验收合格");

        assertThat(acceptance.getId()).isNotNull();
        assertThat(acceptance.getRequestId()).isEqualTo(request.getId());
        assertThat(acceptance.getAcceptanceResult()).isEqualTo("PASS");
        assertThat(acceptance.getActualQuantity()).isEqualTo(5);
        assertThat(acceptance.getActualAmount()).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(acceptance.getCheckRemark()).isEqualTo("验收合格");

        // Verify request status updated
        PurchaseRequest updated = purchaseRequestMapper.selectById(request.getId());
        assertThat(updated.getStatus()).isEqualTo("ACCEPTED");

        // Verify acceptance persisted
        PurchaseAcceptance found = purchaseAcceptanceMapper.selectById(acceptance.getId());
        assertThat(found).isNotNull();
        assertThat(found.getAcceptanceResult()).isEqualTo("PASS");
    }

    @Test
    void shouldRejectPurchaseRequest() {
        PurchaseRequest request = purchaseService.createRequest(createDto);

        // Reject
        PurchaseAcceptance acceptance = purchaseService.accept(request.getId(), false, "规格不符合要求");

        assertThat(acceptance.getAcceptanceResult()).isEqualTo("FAIL");
        assertThat(acceptance.getCheckRemark()).isEqualTo("规格不符合要求");

        // Verify request status updated to REJECTED
        PurchaseRequest updated = purchaseRequestMapper.selectById(request.getId());
        assertThat(updated.getStatus()).isEqualTo("REJECTED");
    }

    @Test
    void shouldThrowWhenAcceptingNonExistentRequest() {
        assertThatThrownBy(() -> purchaseService.accept(999L, true, ""))
                .isInstanceOf(BusinessException.class)
                .hasMessage("采购申请不存在");
    }

    @Test
    void shouldThrowWhenAcceptingAlreadyAcceptedRequest() {
        PurchaseRequest request = purchaseService.createRequest(createDto);
        purchaseService.accept(request.getId(), true, "验收合格");

        assertThatThrownBy(() -> purchaseService.accept(request.getId(), true, "再次验收"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("采购申请状态不允许验收");
    }

    @Test
    void shouldThrowWhenAcceptingRejectedRequest() {
        PurchaseRequest request = purchaseService.createRequest(createDto);
        purchaseService.accept(request.getId(), false, "不合格");

        assertThatThrownBy(() -> purchaseService.accept(request.getId(), true, "再次验收"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("采购申请状态不允许验收");
    }

    @Test
    void shouldConvertAcceptedRequestToAsset() {
        PurchaseRequest request = purchaseService.createRequest(createDto);
        purchaseService.accept(request.getId(), true, "验收合格");

        Asset asset = purchaseService.convertToAsset(request.getId());

        assertThat(asset.getId()).isNotNull();
        assertThat(asset.getName()).isEqualTo("测试服务器");
        assertThat(asset.getOrgId()).isEqualTo(1L);
        assertThat(asset.getSourceType()).isEqualTo("PURCHASE");
        assertThat(asset.getQuantity()).isEqualTo(5);
        assertThat(asset.getOriginalValue()).isEqualByComparingTo(new BigDecimal("100000.00"));

        // Verify asset persisted
        Asset found = assetService.getById(asset.getId());
        assertThat(found).isNotNull();

        // Verify acceptance record has assetId
        PurchaseAcceptance acceptance = purchaseAcceptanceMapper.selectOne(
                new LambdaQueryWrapper<PurchaseAcceptance>()
                        .eq(PurchaseAcceptance::getRequestId, request.getId())
        );
        assertThat(acceptance).isNotNull();
        assertThat(acceptance.getAssetId()).isEqualTo(asset.getId());
    }

    @Test
    void shouldThrowWhenConvertingNonExistentRequest() {
        assertThatThrownBy(() -> purchaseService.convertToAsset(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("采购申请不存在");
    }

    @Test
    void shouldThrowWhenConvertingPendingRequest() {
        PurchaseRequest request = purchaseService.createRequest(createDto);

        assertThatThrownBy(() -> purchaseService.convertToAsset(request.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("采购申请未验收通过，无法转为资产");
    }

    @Test
    void shouldThrowWhenConvertingRejectedRequest() {
        PurchaseRequest request = purchaseService.createRequest(createDto);
        purchaseService.accept(request.getId(), false, "不合格");

        assertThatThrownBy(() -> purchaseService.convertToAsset(request.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("采购申请未验收通过，无法转为资产");
    }

    @Test
    void shouldThrowWhenConvertingAlreadyConvertedRequest() {
        PurchaseRequest request = purchaseService.createRequest(createDto);
        purchaseService.accept(request.getId(), true, "验收合格");
        purchaseService.convertToAsset(request.getId());

        assertThatThrownBy(() -> purchaseService.convertToAsset(request.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("该采购申请已转为资产");
    }

    @Test
    void shouldListAllRequests() {
        purchaseService.createRequest(createDto);

        PurchaseRequestCreateDTO dto2 = new PurchaseRequestCreateDTO();
        dto2.setAssetName("办公桌");
        dto2.setQuantity(10);
        dto2.setBudget(new BigDecimal("5000.00"));
        dto2.setSupplierId(2L);
        dto2.setOrgId(1L);
        dto2.setTenantId(0L);
        purchaseService.createRequest(dto2);

        assertThat(purchaseService.findAll()).hasSize(2);
    }
}
