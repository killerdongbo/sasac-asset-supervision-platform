package com.sasac.platform.asset.procurement.controller;

import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.procurement.dto.PurchaseRequestCreateDTO;
import com.sasac.platform.asset.procurement.entity.PurchaseAcceptance;
import com.sasac.platform.asset.procurement.entity.PurchaseRequest;
import com.sasac.platform.asset.procurement.service.PurchaseService;
import com.sasac.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for purchase management.
 */
@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * Creates a new purchase request.
     *
     * @param dto the purchase request data
     * @return API response with the created purchase request
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseRequest>> create(
            @Valid @RequestBody PurchaseRequestCreateDTO dto) {
        PurchaseRequest request = purchaseService.createRequest(dto);
        return ResponseEntity.ok(ApiResponse.success(request));
    }

    /**
     * Lists all purchase requests.
     *
     * @return API response with the list of purchase requests
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseRequest>>> list() {
        List<PurchaseRequest> requests = purchaseService.findAll();
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    /**
     * Accepts or rejects a purchase request.
     *
     * @param id     the purchase request ID
     * @param passed acceptance result (true = pass, false = fail)
     * @param remark acceptance remark
     * @return API response with the acceptance record
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<PurchaseAcceptance>> accept(
            @PathVariable Long id,
            @RequestParam boolean passed,
            @RequestParam(defaultValue = "") String remark) {
        PurchaseAcceptance acceptance = purchaseService.accept(id, passed, remark);
        return ResponseEntity.ok(ApiResponse.success(acceptance));
    }

    /**
     * Converts an accepted purchase request into a fixed asset.
     * <p>
     * The organization is taken from the original purchase request.
     *
     * @param id the purchase request ID
     * @return API response with the created asset
     */
    @PostMapping("/{id}/convert")
    public ResponseEntity<ApiResponse<Asset>> convert(@PathVariable Long id) {
        Asset asset = purchaseService.convertToAsset(id);
        return ResponseEntity.ok(ApiResponse.success(asset));
    }
}
