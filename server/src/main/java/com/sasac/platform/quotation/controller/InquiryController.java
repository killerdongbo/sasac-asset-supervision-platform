package com.sasac.platform.quotation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.quotation.entity.Inquiry;
import com.sasac.platform.quotation.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final QuotationService quotationService;

    @GetMapping
    public ApiResponse<Page<Inquiry>> list(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(quotationService.listInquiries(tenantId, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Inquiry> get(@PathVariable Long id) {
        return ApiResponse.success(quotationService.getInquiry(id));
    }

    @PostMapping
    public ApiResponse<Inquiry> create(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestBody Inquiry inquiry) {
        inquiry.setTenantId(tenantId);
        return ApiResponse.success(quotationService.createInquiry(inquiry));
    }

    @PutMapping("/{id}/publish")
    public ApiResponse<Inquiry> publish(@PathVariable Long id) {
        return ApiResponse.success(quotationService.publishInquiry(id));
    }

    @PutMapping("/{id}/close")
    public ApiResponse<Inquiry> close(@PathVariable Long id) {
        return ApiResponse.success(quotationService.closeInquiry(id));
    }
}
