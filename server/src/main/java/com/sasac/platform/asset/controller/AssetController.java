package com.sasac.platform.asset.controller;

import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.dto.AssetQueryDTO;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.AssetChange;
import com.sasac.platform.asset.service.AssetImportService;
import com.sasac.platform.asset.service.AssetService;
import com.sasac.platform.asset.service.ChangeService;
import com.sasac.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST controller for asset CRUD operations.
 */
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final AssetImportService importService;
    private final ChangeService changeService;

    /**
     * Creates a new asset.
     *
     * @param dto the asset creation data
     * @return API response with the created asset
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Asset>> create(@Valid @RequestBody AssetCreateDTO dto) {
        Asset created = assetService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Retrieves an asset by ID.
     *
     * @param id the asset ID
     * @return API response with the asset
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Asset>> getById(@PathVariable Long id) {
        Asset asset = assetService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(asset));
    }

    /**
     * Queries assets with filters and pagination.
     *
     * @param query the query parameters
     * @return API response with the list of assets
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Asset>>> query(@ModelAttribute AssetQueryDTO query) {
        List<Asset> assets = assetService.query(query);
        return ResponseEntity.ok(ApiResponse.success(assets));
    }

    /**
     * Updates an existing asset.
     *
     * @param id     the asset ID to update
     * @param update the updated asset fields
     * @return API response with the updated asset
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Asset>> update(@PathVariable Long id,
                                                     @RequestBody Asset update) {
        Asset updated = assetService.update(id, update);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Deletes an asset by ID.
     *
     * @param id the asset ID to delete
     * @return API response indicating success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Records an asset change (transfer, mortgage, rental, etc.).
     *
     * @param id     the asset ID
     * @param change the change record
     * @return API response with the saved change record
     */
    @PostMapping("/{id}/changes")
    public ResponseEntity<ApiResponse<AssetChange>> recordChange(@PathVariable Long id,
                                                                  @Valid @RequestBody AssetChange change) {
        change.setAssetId(id);
        AssetChange saved = changeService.record(change);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    /**
     * Retrieves the change history for an asset.
     *
     * @param id the asset ID
     * @return API response with the list of change records
     */
    @GetMapping("/{id}/changes")
    public ResponseEntity<ApiResponse<List<AssetChange>>> getChangeHistory(@PathVariable Long id) {
        List<AssetChange> history = changeService.getHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * Imports assets from an uploaded Excel file.
     *
     * @param file     the uploaded Excel file
     * @param orgId    the organization ID
     * @param tenantId the tenant ID
     * @return API response with the import result
     * @throws IOException if the file cannot be read
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AssetImportService.ImportResult>> importAssets(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long orgId,
            @RequestParam Long tenantId) throws IOException {
        AssetImportService.ImportResult result = importService.importAssets(file, tenantId, orgId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Downloads the Excel template for asset import.
     *
     * @param response the HTTP response
     * @throws IOException if writing fails
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        importService.downloadTemplate(response);
    }
}
