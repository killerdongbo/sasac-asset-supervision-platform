package com.sasac.platform.system.export.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.system.export.dto.ExportRequestDTO;
import com.sasac.platform.system.export.entity.ExportTask;
import com.sasac.platform.system.export.service.ExportService;
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

@RestController
@RequestMapping("/api/exports")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

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
    public ResponseEntity<ApiResponse<String>> download(@PathVariable Long id) {
        ExportTask task = exportService.getTask(id);
        if (task == null) {
            throw new BusinessException("导出任务不存在");
        }
        if (!ExportService.STATUS_COMPLETED.equals(task.getStatus())) {
            throw new BusinessException("导出任务尚未完成");
        }
        String downloadUrl = exportService.generateDownloadUrl(id);
        if (downloadUrl == null) {
            throw new BusinessException("无法生成下载链接");
        }
        return ResponseEntity.ok(ApiResponse.success(downloadUrl));
    }
}
