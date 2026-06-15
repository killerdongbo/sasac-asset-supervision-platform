package com.sasac.platform.asset.procurement.controller;

import com.sasac.platform.asset.procurement.entity.PurchaseAcceptance;
import com.sasac.platform.asset.procurement.service.PurchaseService;
import com.sasac.platform.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for purchase acceptance records.
 */
@RestController
@RequestMapping("/api/purchase-acceptances")
@RequiredArgsConstructor
public class PurchaseAcceptanceController {

    private final PurchaseService purchaseService;

    /**
     * Lists all purchase acceptance records.
     *
     * @return API response with the list of acceptance records
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseAcceptance>>> listAcceptances() {
        List<PurchaseAcceptance> acceptances = purchaseService.findAllAcceptances();
        return ResponseEntity.ok(ApiResponse.success(acceptances));
    }
}
