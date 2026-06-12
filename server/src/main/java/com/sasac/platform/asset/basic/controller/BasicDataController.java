package com.sasac.platform.asset.basic.controller;

import com.sasac.platform.asset.basic.entity.Location;
import com.sasac.platform.asset.basic.entity.MaintenanceProvider;
import com.sasac.platform.asset.basic.entity.Supplier;
import com.sasac.platform.asset.basic.service.BasicDataService;
import com.sasac.platform.asset.entity.AssetCategory;
import com.sasac.platform.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for basic-data dictionary CRUD operations.
 * <p>
 * Exposes endpoints for Locations, Suppliers, MaintenanceProviders,
 * and AssetCategories under the {@code /api/basic-data} prefix.
 */
@RestController
@RequestMapping("/api/basic-data")
@RequiredArgsConstructor
public class BasicDataController {

    private final BasicDataService basicDataService;

    // ---------------------------------------------------------------
    // Location
    // ---------------------------------------------------------------

    @PostMapping("/locations")
    public ResponseEntity<ApiResponse<Location>> createLocation(@RequestBody Location location) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.createLocation(location)));
    }

    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<Location>>> listLocations() {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.listLocations()));
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<ApiResponse<Location>> getLocation(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.getLocation(id)));
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<ApiResponse<Location>> updateLocation(@PathVariable Long id,
                                                                @RequestBody Location location) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.updateLocation(id, location)));
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable Long id) {
        basicDataService.deleteLocation(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ---------------------------------------------------------------
    // Supplier
    // ---------------------------------------------------------------

    @PostMapping("/suppliers")
    public ResponseEntity<ApiResponse<Supplier>> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.createSupplier(supplier)));
    }

    @GetMapping("/suppliers")
    public ResponseEntity<ApiResponse<List<Supplier>>> listSuppliers() {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.listSuppliers()));
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<ApiResponse<Supplier>> getSupplier(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.getSupplier(id)));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<ApiResponse<Supplier>> updateSupplier(@PathVariable Long id,
                                                                @RequestBody Supplier supplier) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.updateSupplier(id, supplier)));
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        basicDataService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ---------------------------------------------------------------
    // MaintenanceProvider
    // ---------------------------------------------------------------

    @PostMapping("/maintenance-providers")
    public ResponseEntity<ApiResponse<MaintenanceProvider>> createMaintenanceProvider(
            @RequestBody MaintenanceProvider provider) {
        return ResponseEntity.ok(ApiResponse.success(
                basicDataService.createMaintenanceProvider(provider)));
    }

    @GetMapping("/maintenance-providers")
    public ResponseEntity<ApiResponse<List<MaintenanceProvider>>> listMaintenanceProviders() {
        return ResponseEntity.ok(ApiResponse.success(
                basicDataService.listMaintenanceProviders()));
    }

    @GetMapping("/maintenance-providers/{id}")
    public ResponseEntity<ApiResponse<MaintenanceProvider>> getMaintenanceProvider(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                basicDataService.getMaintenanceProvider(id)));
    }

    @PutMapping("/maintenance-providers/{id}")
    public ResponseEntity<ApiResponse<MaintenanceProvider>> updateMaintenanceProvider(
            @PathVariable Long id, @RequestBody MaintenanceProvider provider) {
        return ResponseEntity.ok(ApiResponse.success(
                basicDataService.updateMaintenanceProvider(id, provider)));
    }

    @DeleteMapping("/maintenance-providers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaintenanceProvider(@PathVariable Long id) {
        basicDataService.deleteMaintenanceProvider(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ---------------------------------------------------------------
    // AssetCategory
    // ---------------------------------------------------------------

    @PostMapping("/asset-categories")
    public ResponseEntity<ApiResponse<AssetCategory>> createCategory(
            @RequestBody AssetCategory category) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.createCategory(category)));
    }

    @GetMapping("/asset-categories")
    public ResponseEntity<ApiResponse<List<AssetCategory>>> listAssetCategories() {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.listAssetCategories()));
    }

    @GetMapping("/asset-categories/{id}")
    public ResponseEntity<ApiResponse<AssetCategory>> getAssetCategory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.getAssetCategory(id)));
    }

    @PutMapping("/asset-categories/{id}")
    public ResponseEntity<ApiResponse<AssetCategory>> updateCategory(@PathVariable Long id,
                                                                     @RequestBody AssetCategory category) {
        return ResponseEntity.ok(ApiResponse.success(basicDataService.updateCategory(id, category)));
    }

    @DeleteMapping("/asset-categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        basicDataService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
