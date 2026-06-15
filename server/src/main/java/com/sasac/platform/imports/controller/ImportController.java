package com.sasac.platform.imports.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.imports.dto.ImportResult;
import com.sasac.platform.imports.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] data = importService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"asset_import_template.xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @PostMapping("/assets")
    public ApiResponse<ImportResult> importAssets(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam("file") MultipartFile file) {
        ImportResult result = importService.importAssets(file, tenantId);
        return ApiResponse.success(result);
    }
}
