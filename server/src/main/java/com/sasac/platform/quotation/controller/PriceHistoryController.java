package com.sasac.platform.quotation.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.quotation.entity.PriceHistory;
import com.sasac.platform.quotation.entity.Quotation;
import com.sasac.platform.quotation.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PriceHistoryController {

    private final QuotationService quotationService;

    @GetMapping("/quotations/inquiry/{inquiryId}")
    public ApiResponse<List<Quotation>> listQuotations(@PathVariable Long inquiryId) {
        return ApiResponse.success(quotationService.listQuotations(inquiryId));
    }

    @PostMapping("/quotations")
    public ApiResponse<Quotation> submitQuotation(@RequestBody Quotation quotation) {
        return ApiResponse.success(quotationService.submitQuotation(quotation));
    }

    @PutMapping("/quotations/{id}/select")
    public ApiResponse<Void> selectQuotation(@PathVariable Long id) {
        quotationService.selectQuotation(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/price-history/trend")
    public ApiResponse<List<PriceHistory>> getPriceTrend(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestParam String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(quotationService.getPriceTrend(tenantId, category, startDate, endDate));
    }

    @GetMapping("/price-history/analysis")
    public ApiResponse<Map<String, Object>> getPriceAnalysis(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @RequestParam String category) {
        return ApiResponse.success(quotationService.getPriceAnalysis(tenantId, category));
    }
}
