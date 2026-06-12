package com.sasac.platform.financial.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.financial.service.FinancialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for financial system integration.
 */
@RestController
@RequestMapping("/api/financial")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService financialService;

    @GetMapping("/adapters")
    public ApiResponse<List<String>> getAdapters() {
        return ApiResponse.success(financialService.getAvailableAdapters());
    }

    @PostMapping("/adapters/{name}/test")
    public ApiResponse<Boolean> testConnection(@PathVariable String name,
                                                @RequestBody Map<String, String> config) {
        return ApiResponse.success(financialService.testConnection(name, config));
    }

    @PostMapping("/adapters/{name}/assets")
    public ApiResponse<?> fetchAssets(@PathVariable String name,
                                       @RequestBody Map<String, String> config) {
        return ApiResponse.success(financialService.fetchAssets(name, config));
    }
}
