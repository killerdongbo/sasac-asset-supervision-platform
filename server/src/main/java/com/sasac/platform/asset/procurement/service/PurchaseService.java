package com.sasac.platform.asset.procurement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.procurement.dto.PurchaseRequestCreateDTO;
import com.sasac.platform.asset.procurement.entity.PurchaseAcceptance;
import com.sasac.platform.asset.procurement.entity.PurchaseRequest;
import com.sasac.platform.asset.procurement.mapper.PurchaseAcceptanceMapper;
import com.sasac.platform.asset.procurement.mapper.PurchaseRequestMapper;
import com.sasac.platform.asset.service.AssetService;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for purchase management.
 * <p>
 * Handles the full lifecycle: request creation, acceptance check,
 * and conversion of accepted purchases into fixed assets.
 */
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRequestMapper purchaseRequestMapper;
    private final PurchaseAcceptanceMapper purchaseAcceptanceMapper;
    private final AssetService assetService;

    /**
     * Creates a new purchase request with PENDING status.
     *
     * @param dto the request data
     * @return the created purchase request with generated ID
     */
    @Transactional
    public PurchaseRequest createRequest(PurchaseRequestCreateDTO dto) {
        PurchaseRequest request = new PurchaseRequest();
        BeanUtils.copyProperties(dto, request);
        request.setStatus("PENDING");

        purchaseRequestMapper.insert(request);
        return request;
    }

    /**
     * Returns all purchase requests (unpaginated).
     *
     * @return list of all purchase requests
     */
    public List<PurchaseRequest> findAll() {
        return purchaseRequestMapper.selectList(null);
    }

    /**
     * Accepts or rejects a purchase request with the acceptance result.
     * <p>
     * When passed is true the request status becomes ACCEPTED and a
     * PASS acceptance record is created. When passed is false the
     * request status becomes REJECTED and a FAIL record is created.
     *
     * @param requestId the purchase request ID
     * @param passed    true if the acceptance check passed, false otherwise
     * @param remark    acceptance remark
     * @return the created acceptance record
     * @throws BusinessException if the request does not exist or is not in PENDING status
     */
    @Transactional
    public PurchaseAcceptance accept(Long requestId, boolean passed, String remark) {
        PurchaseRequest request = purchaseRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("采购申请不存在");
        }
        if (!"PENDING".equals(request.getStatus())) {
            throw new BusinessException("采购申请状态不允许验收");
        }

        PurchaseAcceptance acceptance = new PurchaseAcceptance();
        acceptance.setRequestId(requestId);
        acceptance.setAcceptanceResult(passed ? "PASS" : "FAIL");
        acceptance.setCheckRemark(remark);
        acceptance.setTenantId(request.getTenantId());

        if (passed) {
            acceptance.setActualQuantity(request.getQuantity());
            acceptance.setActualAmount(request.getBudget());
        }

        purchaseAcceptanceMapper.insert(acceptance);

        request.setStatus(passed ? "ACCEPTED" : "REJECTED");
        purchaseRequestMapper.updateById(request);

        return acceptance;
    }

    /**
     * Converts an accepted purchase request into a fixed asset.
     * <p>
     * The request must be in ACCEPTED status. An {@link AssetCreateDTO}
     * is built from the request data and passed to {@link AssetService#create}.
     * The resulting asset ID is recorded in the acceptance record.
     *
     * @param requestId the purchase request ID
     * @return the created asset
     * @throws BusinessException if the request does not exist, is not ACCEPTED,
     *                           or has already been converted
     */
    @Transactional
    public Asset convertToAsset(Long requestId) {
        PurchaseRequest request = purchaseRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("采购申请不存在");
        }
        if (!"ACCEPTED".equals(request.getStatus())) {
            throw new BusinessException("采购申请未验收通过，无法转为资产");
        }

        LambdaQueryWrapper<PurchaseAcceptance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseAcceptance::getRequestId, requestId);
        PurchaseAcceptance acceptance = purchaseAcceptanceMapper.selectOne(wrapper);
        if (acceptance == null) {
            throw new BusinessException("验收记录不存在");
        }
        if (acceptance.getAssetId() != null) {
            throw new BusinessException("该采购申请已转为资产");
        }

        AssetCreateDTO assetDTO = new AssetCreateDTO();
        assetDTO.setName(request.getAssetName());
        assetDTO.setAssetCode("PR-" + requestId + "-" + UUID.randomUUID().toString().substring(0, 8));
        assetDTO.setCategory("EQUIPMENT");
        assetDTO.setOrgId(request.getOrgId());
        assetDTO.setTenantId(request.getTenantId());
        assetDTO.setQuantity(request.getQuantity());
        assetDTO.setOriginalValue(request.getBudget());
        assetDTO.setSourceType("PURCHASE");
        assetDTO.setPurchaseDate(LocalDate.now());
        assetDTO.setRegistrationDate(LocalDate.now());
        assetDTO.setRemark(request.getRequestReason());

        Asset asset = assetService.create(assetDTO);

        acceptance.setAssetId(asset.getId());
        purchaseAcceptanceMapper.updateById(acceptance);

        return asset;
    }
}
