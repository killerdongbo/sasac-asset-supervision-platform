package com.sasac.platform.system.export.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.Depreciation;
import com.sasac.platform.asset.inventory.entity.InventoryRecord;
import com.sasac.platform.asset.inventory.entity.InventoryTask;
import com.sasac.platform.asset.inventory.mapper.InventoryRecordMapper;
import com.sasac.platform.asset.inventory.mapper.InventoryTaskMapper;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.asset.mapper.DepreciationMapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.system.export.ExportType;
import com.sasac.platform.system.export.dto.ExportRequestDTO;
import com.sasac.platform.system.export.entity.ExportTask;
import com.sasac.platform.system.export.mapper.ExportTaskMapper;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportService {

    private static final int MAX_CONCURRENT_PER_TENANT = 5;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** Export task status constants. */
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    private final ExportTaskMapper exportTaskMapper;
    private final MinioClient minioClient;
    private final AssetMapper assetMapper;
    private final InventoryTaskMapper inventoryTaskMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final DepreciationMapper depreciationMapper;

    @Autowired
    @Lazy
    private ExportService self;

    @Value("${minio.bucket:sasac-assets}")
    private String bucket;

    public ExportService(ExportTaskMapper exportTaskMapper,
                         MinioClient minioClient,
                         AssetMapper assetMapper,
                         InventoryTaskMapper inventoryTaskMapper,
                         InventoryRecordMapper inventoryRecordMapper,
                         DepreciationMapper depreciationMapper) {
        this.exportTaskMapper = exportTaskMapper;
        this.minioClient = minioClient;
        this.assetMapper = assetMapper;
        this.inventoryTaskMapper = inventoryTaskMapper;
        this.inventoryRecordMapper = inventoryRecordMapper;
        this.depreciationMapper = depreciationMapper;
    }

    public ExportTask createTask(Long tenantId, Long userId, ExportRequestDTO dto) {
        long runningCount = exportTaskMapper.selectCount(
                new LambdaQueryWrapper<ExportTask>()
                        .eq(ExportTask::getTenantId, tenantId)
                        .in(ExportTask::getStatus, STATUS_PENDING, STATUS_PROCESSING)
        );
        if (runningCount >= MAX_CONCURRENT_PER_TENANT) {
            throw new BusinessException("当前有太多导出任务正在执行，请稍后再试");
        }

        ExportTask task = new ExportTask();
        task.setTenantId(tenantId);
        task.setExportType(dto.getExportType());
        task.setParams(dto.getParams());
        task.setStatus(STATUS_PENDING);
        task.setCreatedBy(userId);
        exportTaskMapper.insert(task);

        self.executeExportAsync(task.getId());
        return task;
    }

    @Async
    public void executeExportAsync(Long taskId) {
        ExportTask task = exportTaskMapper.selectById(taskId);
        if (task == null) {
            log.warn("Export task {} not found, async execution skipped", taskId);
            return;
        }

        task.setStatus(STATUS_PROCESSING);
        exportTaskMapper.updateById(task);

        try {
            String exportType = task.getExportType();
            switch (exportType) {
                case ExportType.ASSET_LIST -> exportAssetList(task);
                case ExportType.INVENTORY_REPORT -> exportInventoryReport(task);
                case ExportType.DEPRECIATION_LIST -> exportDepreciationList(task);
                default -> throw new BusinessException("不支持的导出类型: " + exportType);
            }

            task.setStatus(STATUS_COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Export task {} failed: {}", taskId, e.getMessage(), e);
            task.setStatus(STATUS_FAILED);
            task.setErrorMessage(e.getMessage());
            task.setCompletedAt(LocalDateTime.now());
        }

        exportTaskMapper.updateById(task);
    }

    public Page<ExportTask> listTasks(Long tenantId, int page, int size) {
        return exportTaskMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<ExportTask>()
                        .eq(ExportTask::getTenantId, tenantId)
                        .orderByDesc(ExportTask::getCreatedAt)
        );
    }

    public ExportTask getTask(Long taskId) {
        return exportTaskMapper.selectById(taskId);
    }

    // ========== Export implementations ==========

    private void exportAssetList(ExportTask task) throws Exception {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .orderByAsc(Asset::getCategory);
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);

        List<AssetExportRow> rows = assets.stream().map(a -> {
            AssetExportRow r = new AssetExportRow();
            r.setName(a.getName());
            r.setAssetCode(a.getAssetCode());
            r.setCategory(a.getCategory());
            r.setSpecification(a.getSpecification());
            r.setLocation(a.getLocation());
            r.setUseStatus(a.getUseStatus());
            r.setOriginalValue(a.getOriginalValue());
            r.setCurrentValue(a.getCurrentValue());
            return r;
        }).collect(Collectors.toList());

        writeAndUpload(task, rows, AssetExportRow.class, "资产台账", "资产台账");
    }

    private void exportInventoryReport(ExportTask task) throws Exception {
        LambdaQueryWrapper<InventoryTask> qw = new LambdaQueryWrapper<InventoryTask>()
                .eq(InventoryTask::getTenantId, task.getTenantId());
        List<InventoryTask> inventoryTasks = inventoryTaskMapper.selectList(qw);

        // Batch-count records per task to avoid N+1
        List<Long> taskIds = inventoryTasks.stream().map(InventoryTask::getId).collect(Collectors.toList());
        Map<Long, Long> countMap = taskIds.isEmpty() ? Map.of() :
                inventoryRecordMapper.selectList(
                        new LambdaQueryWrapper<InventoryRecord>().in(InventoryRecord::getTaskId, taskIds)
                ).stream()
                .collect(Collectors.groupingBy(InventoryRecord::getTaskId, Collectors.counting()));

        List<InventoryReportRow> rows = inventoryTasks.stream().map(t -> {
            InventoryReportRow r = new InventoryReportRow();
            r.setTaskName(t.getTaskName());
            r.setStatus(t.getStatus());
            r.setTotalCount(t.getTotalCount());
            r.setCompletedCount(t.getCompletedCount());
            r.setDiffCount(t.getDiffCount());
            r.setRecordCount(countMap.getOrDefault(t.getId(), 0L).intValue());
            return r;
        }).collect(Collectors.toList());

        writeAndUpload(task, rows, InventoryReportRow.class, "盘点报告", "盘点报告");
    }

    private void exportDepreciationList(ExportTask task) throws Exception {
        LambdaQueryWrapper<Depreciation> qw = new LambdaQueryWrapper<Depreciation>()
                .eq(Depreciation::getTenantId, task.getTenantId())
                .orderByDesc(Depreciation::getDepreciationDate);
        List<Depreciation> depreciations = depreciationMapper.selectList(qw);

        // Batch-query assets to avoid N+1
        List<Long> assetIds = depreciations.stream()
                .map(Depreciation::getAssetId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Asset> assetMap = assetIds.isEmpty() ? Map.of() :
                assetMapper.selectBatchIds(assetIds).stream()
                        .collect(Collectors.toMap(Asset::getId, Function.identity()));

        List<DepreciationRow> rows = depreciations.stream().map(d -> {
            Asset asset = assetMap.get(d.getAssetId());

            DepreciationRow r = new DepreciationRow();
            r.setAssetName(asset != null ? asset.getName() : "未知资产");
            r.setAssetCode(asset != null ? asset.getAssetCode() : "");
            r.setPeriod(d.getPeriod());
            r.setDepreciationAmount(d.getDepreciationAmount());
            r.setBeforeValue(d.getBeforeValue());
            r.setAfterValue(d.getAfterValue());
            r.setDepreciationDate(d.getDepreciationDate());
            return r;
        }).collect(Collectors.toList());

        writeAndUpload(task, rows, DepreciationRow.class, "折旧明细", "折旧明细");
    }

    // ========== MinIO helpers ==========

    private String uploadToMinio(ExportTask task, File file, String fileName) throws Exception {
        String objectKey = "exports/" + task.getTenantId() + "/" + task.getId() + "/" + fileName;
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .stream(new FileInputStream(file), file.length(), -1)
                        .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .build()
        );
        task.setFilePath(objectKey);
        return objectKey;
    }

    public String generateDownloadUrl(Long taskId) {
        ExportTask task = exportTaskMapper.selectById(taskId);
        if (task == null || task.getFilePath() == null) return null;
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(task.getFilePath())
                            .method(Method.GET)
                            .expiry(3600)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for task {}", taskId, e);
            return null;
        }
    }

    private Long parseOrgId(String params) {
        if (params == null || params.isBlank()) return null;
        try {
            var node = OBJECT_MAPPER.readTree(params);
            return node.has("orgId") ? node.get("orgId").asLong() : null;
        } catch (Exception e) {
            log.warn("Failed to parse orgId from params: {}", params);
            return null;
        }
    }

    /**
     * Write Excel rows to a temp file via EasyExcel, upload to MinIO, then clean up.
     */
    private <T> void writeAndUpload(ExportTask task, List<T> rows, Class<T> clazz,
                                     String displayName, String sheetName) throws Exception {
        String fileName = displayName + "_" + System.currentTimeMillis() + ".xlsx";
        File tempFile = File.createTempFile("export_", ".xlsx");
        try {
            EasyExcel.write(tempFile, clazz).sheet(sheetName).doWrite(rows);
            uploadToMinio(task, tempFile, fileName);
            task.setFileName(fileName);
            task.setTotalRows(rows.size());
        } finally {
            tempFile.delete();
        }
    }

    // ========== Excel DTOs ==========

    @Data
    public static class AssetExportRow {
        @ExcelProperty("资产名称")
        private String name;
        @ExcelProperty("资产编码")
        private String assetCode;
        @ExcelProperty("资产分类")
        private String category;
        @ExcelProperty("规格型号")
        private String specification;
        @ExcelProperty("存放地点")
        private String location;
        @ExcelProperty("使用状态")
        private String useStatus;
        @ExcelProperty("原值")
        private BigDecimal originalValue;
        @ExcelProperty("净值")
        private BigDecimal currentValue;
    }

    @Data
    public static class InventoryReportRow {
        @ExcelProperty("任务名称")
        private String taskName;
        @ExcelProperty("状态")
        private String status;
        @ExcelProperty("资产总数")
        private Integer totalCount;
        @ExcelProperty("已完成数")
        private Integer completedCount;
        @ExcelProperty("差异数")
        private Integer diffCount;
        @ExcelProperty("盘点记录数")
        private Integer recordCount;
    }

    @Data
    public static class DepreciationRow {
        @ExcelProperty("资产名称")
        private String assetName;
        @ExcelProperty("资产编码")
        private String assetCode;
        @ExcelProperty("折旧期间")
        private String period;
        @ExcelProperty("折旧金额")
        private BigDecimal depreciationAmount;
        @ExcelProperty("折旧前价值")
        private BigDecimal beforeValue;
        @ExcelProperty("折旧后价值")
        private BigDecimal afterValue;
        @ExcelProperty("折旧日期")
        private LocalDate depreciationDate;
    }
}
