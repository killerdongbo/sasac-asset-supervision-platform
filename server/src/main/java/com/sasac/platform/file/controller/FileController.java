package com.sasac.platform.file.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.file.entity.FileAttachment;
import com.sasac.platform.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<FileAttachment> upload(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId,
            @RequestParam(required = false) Long uploadBy) {
        return ApiResponse.success(fileService.upload(file, tenantId,
                uploadBy != null ? uploadBy : 0L, bizType, bizId));
    }

    @PostMapping("/upload/batch")
    public ApiResponse<List<FileAttachment>> uploadBatch(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId,
            @RequestParam(required = false) Long uploadBy) {
        return ApiResponse.success(fileService.uploadBatch(files, tenantId,
                uploadBy != null ? uploadBy : 0L, bizType, bizId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        FileAttachment fa = fileService.getFile(id);
        InputStream is = fileService.download(id);
        try {
            byte[] bytes = is.readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fa.getOriginalName() + "\"")
                    .contentType(MediaType.parseMediaType(
                            fa.getContentType() != null ? fa.getContentType() : "application/octet-stream"))
                    .body(bytes);
        } catch (Exception e) {
            throw new RuntimeException("下载失败");
        }
    }

    @GetMapping("/{id}/preview")
    public ApiResponse<String> preview(@PathVariable Long id) {
        return ApiResponse.success(fileService.getPreviewUrl(id));
    }

    @GetMapping("/{id}")
    public ApiResponse<FileAttachment> detail(@PathVariable Long id) {
        return ApiResponse.success(fileService.getFile(id));
    }

    @GetMapping("/biz")
    public ApiResponse<List<FileAttachment>> listByBiz(
            @RequestParam String bizType, @RequestParam Long bizId) {
        return ApiResponse.success(fileService.listByBiz(bizType, bizId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return ApiResponse.success(null);
    }
}
