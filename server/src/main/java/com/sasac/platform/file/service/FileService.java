package com.sasac.platform.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.file.entity.FileAttachment;
import com.sasac.platform.file.mapper.FileAttachmentMapper;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private static final Set<String> IMAGE_EXTS = Set.of("jpg","jpeg","png","gif","webp","bmp","svg");

    private final MinioClient minioClient;
    private final FileAttachmentMapper fileMapper;

    @Value("${minio.bucket:sasac-assets}")
    private String defaultBucket;

    // ---- 上传 ----

    @Transactional
    public FileAttachment upload(MultipartFile file, Long tenantId, Long uploadBy,
                                  String bizType, Long bizId) {
        try {
            String ext = getExt(file.getOriginalFilename());
            String storageName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            String objectPath = String.format("%d/%s/%s/%s", tenantId,
                    bizType != null ? bizType.toLowerCase() : "general",
                    LocalDateTime.now().toLocalDate().toString(), storageName);

            String md5 = computeMd5(file.getInputStream());
            // 检查重复文件
            FileAttachment existing = fileMapper.selectOne(
                    new LambdaQueryWrapper<FileAttachment>().eq(FileAttachment::getMd5, md5));
            if (existing != null) {
                log.info("Duplicate file detected, reusing: {}", existing.getId());
                return existing;
            }

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(defaultBucket).object(objectPath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType()).build());

            FileAttachment fa = new FileAttachment();
            fa.setTenantId(tenantId);
            fa.setOriginalName(file.getOriginalFilename());
            fa.setStorageName(storageName);
            fa.setBucketName(defaultBucket);
            fa.setObjectPath(objectPath);
            fa.setFileSize(file.getSize());
            fa.setContentType(file.getContentType());
            fa.setFileExt(ext.toLowerCase());
            fa.setMd5(md5);
            fa.setBizType(bizType);
            fa.setBizId(bizId);
            fa.setIsImage(IMAGE_EXTS.contains(ext.toLowerCase()) ? 1 : 0);
            fa.setUploadBy(uploadBy);
            fileMapper.insert(fa);

            return fa;
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Transactional
    public List<FileAttachment> uploadBatch(List<MultipartFile> files, Long tenantId,
                                             Long uploadBy, String bizType, Long bizId) {
        List<FileAttachment> result = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                result.add(upload(file, tenantId, uploadBy, bizType, bizId));
            }
        }
        return result;
    }

    // ---- 下载/预览 ----

    public InputStream download(Long fileId) {
        try {
            FileAttachment fa = getFile(fileId);
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(fa.getBucketName()).object(fa.getObjectPath()).build());
        } catch (Exception e) {
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }

    public String getPreviewUrl(Long fileId) {
        FileAttachment fa = getFile(fileId);
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(fa.getBucketName()).object(fa.getObjectPath())
                    .method(Method.GET).expiry(10, TimeUnit.MINUTES).build());
        } catch (Exception e) {
            throw new BusinessException("生成预览链接失败");
        }
    }

    // ---- 查询 ----

    public FileAttachment getFile(Long id) {
        FileAttachment fa = fileMapper.selectById(id);
        if (fa == null) throw new BusinessException("文件不存在");
        return fa;
    }

    public List<FileAttachment> listByBiz(String bizType, Long bizId) {
        return fileMapper.selectList(new LambdaQueryWrapper<FileAttachment>()
                .eq(FileAttachment::getBizType, bizType)
                .eq(FileAttachment::getBizId, bizId)
                .orderByDesc(FileAttachment::getCreatedAt));
    }

    // ---- 删除 ----

    @Transactional
    public void delete(Long fileId) {
        FileAttachment fa = getFile(fileId);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(fa.getBucketName()).object(fa.getObjectPath()).build());
        } catch (Exception e) {
            log.warn("MinIO delete failed: {}", e.getMessage());
        }
        fileMapper.deleteById(fileId);
    }

    @Transactional
    public void deleteByBiz(String bizType, Long bizId) {
        List<FileAttachment> files = listByBiz(bizType, bizId);
        for (FileAttachment f : files) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(f.getBucketName()).object(f.getObjectPath()).build());
            } catch (Exception ex) { log.warn("MinIO delete failed: {}", ex.getMessage()); }
            fileMapper.deleteById(f.getId());
        }
    }

    // ---- Helpers ----

    private String getExt(String filename) {
        if (filename == null) return "bin";
        int i = filename.lastIndexOf('.');
        return i > 0 ? filename.substring(i + 1) : "bin";
    }

    private String computeMd5(InputStream is) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192]; int n;
            while ((n = is.read(buf)) != -1) md.update(buf, 0, n);
            return Base64.getEncoder().encodeToString(md.digest());
        } catch (Exception e) { return ""; }
    }
}
