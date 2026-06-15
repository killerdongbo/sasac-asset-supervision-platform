package com.sasac.platform.system.export.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.system.export.dto.ExportRequestDTO;
import com.sasac.platform.system.export.dto.ImportResultDTO;
import com.sasac.platform.system.export.entity.ExportTask;
import com.sasac.platform.system.export.service.ExportService;
import com.sasac.platform.system.export.service.ReportImportService;
import com.sasac.platform.system.export.service.ReportTemplateService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/exports")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;
    private final ReportTemplateService templateService;
    private final ReportImportService importService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExportTask>> createExport(
            @Valid @RequestBody ExportRequestDTO dto) {
        Long tenantId = 0L;
        Long userId = 0L;
        ExportTask task = exportService.createTask(tenantId, userId, dto);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExportTask>>> listExports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long tenantId = 0L;
        Page<ExportTask> result = exportService.listTasks(tenantId, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        ExportTask task = exportService.getTask(id);
        if (task == null) {
            throw new BusinessException("导出任务不存在");
        }
        if (!ExportService.STATUS_COMPLETED.equals(task.getStatus())) {
            throw new BusinessException("导出任务尚未完成");
        }
        try {
            byte[] data = exportService.downloadFile(id);
            String encodedName = java.net.URLEncoder.encode(task.getFileName() != null ? task.getFileName() : "export.xlsx",
                    java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName)
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(data);
        } catch (Exception e) {
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate(@RequestParam String type) {
        try {
            byte[] data = templateService.getTemplate(type);
            String fileName = templateService.getTemplateFileName(type);
            String encodedName = java.net.URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8)
                    .replace("+", "%20");
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName)
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(data);
        } catch (Exception e) {
            throw new BusinessException("模板下载失败: " + e.getMessage());
        }
    }

    @PostMapping("/import/preview")
    public ResponseEntity<ApiResponse<ImportResultDTO>> previewImport(
            @RequestParam("file") MultipartFile file,
            @RequestParam String exportType) {
        try {
            ImportResultDTO result = importService.preview(file, exportType);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            throw new BusinessException("导入预览失败: " + e.getMessage());
        }
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<ImportResultDTO>> importData(
            @RequestParam("file") MultipartFile file,
            @RequestParam String exportType) {
        Long tenantId = 0L;
        try {
            ImportResultDTO result = importService.importData(file, exportType, tenantId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            throw new BusinessException("导入失败: " + e.getMessage());
        }
    }
}
