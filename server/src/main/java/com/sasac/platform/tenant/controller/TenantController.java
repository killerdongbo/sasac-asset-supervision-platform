package com.sasac.platform.tenant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.tenant.entity.Tenant;
import com.sasac.platform.tenant.entity.TenantConfig;
import com.sasac.platform.tenant.entity.TenantUsage;
import com.sasac.platform.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public ApiResponse<Page<Tenant>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(tenantService.listTenants(keyword, status, page, size));
    }

    @GetMapping("/all")
    public ApiResponse<List<Tenant>> listAll() {
        return ApiResponse.success(tenantService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Tenant> get(@PathVariable Long id) {
        return ApiResponse.success(tenantService.getTenant(id));
    }

    @PostMapping
    public ApiResponse<Tenant> create(@RequestBody Tenant tenant) {
        return ApiResponse.success(tenantService.createTenant(tenant));
    }

    @PutMapping("/{id}")
    public ApiResponse<Tenant> update(@PathVariable Long id, @RequestBody Tenant tenant) {
        return ApiResponse.success(tenantService.updateTenant(id, tenant));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        tenantService.toggleStatus(id, body.get("status"));
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/configs")
    public ApiResponse<List<TenantConfig>> getConfigs(@PathVariable Long id) {
        return ApiResponse.success(tenantService.getConfigs(id));
    }

    @PostMapping("/{id}/configs")
    public ApiResponse<Void> saveConfig(@PathVariable Long id, @RequestBody Map<String, String> body) {
        tenantService.saveConfig(id, body.get("key"), body.get("value"), body.get("description"));
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/usage")
    public ApiResponse<TenantUsage> getUsage(@PathVariable Long id) {
        return ApiResponse.success(tenantService.getUsage(id));
    }

    @PostMapping("/{id}/usage/refresh")
    public ApiResponse<TenantUsage> refreshUsage(@PathVariable Long id) {
        return ApiResponse.success(tenantService.refreshUsage(id));
    }
}
