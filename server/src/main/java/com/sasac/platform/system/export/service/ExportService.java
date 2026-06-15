package com.sasac.platform.system.export.service;

import com.alibaba.excel.EasyExcel;
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
import com.sasac.platform.system.export.dto.AssetExportRow;
import com.sasac.platform.system.export.dto.DepreciationRow;
import com.sasac.platform.system.export.dto.ExportRequestDTO;
import com.sasac.platform.system.export.dto.InventoryReportRow;
import com.sasac.platform.system.export.entity.ExportTask;
import com.sasac.platform.system.export.mapper.ExportTaskMapper;
import com.sasac.platform.asset.inspection.entity.InspectionAnomaly;
import com.sasac.platform.asset.inspection.mapper.InspectionAnomalyMapper;
import com.sasac.platform.report.dto.BalanceSheetRow;
import com.sasac.platform.report.dto.BuildingAssetDetailRow;
import com.sasac.platform.report.dto.CreditorRightsRow;
import com.sasac.platform.report.dto.DataAssetDetailRow;
import com.sasac.platform.report.dto.EquityInvestmentRow;
import com.sasac.platform.report.dto.FranchiseRightRow;
import com.sasac.platform.report.dto.InfrastructureDetailRow;
import com.sasac.platform.report.dto.IntangibleAssetDetailRow;
import com.sasac.platform.report.dto.InventoryDetailRow;
import com.sasac.platform.report.dto.LandAssetDetailRow;
import com.sasac.platform.report.dto.MachineryEquipDetailRow;
import com.sasac.platform.report.dto.MonetaryFundDetailRow;
import com.sasac.platform.report.dto.NaturalResourceDetailRow;
import com.sasac.platform.report.dto.OtherFixedAssetDetailRow;
import com.sasac.platform.report.dto.PeFundInvestmentRow;
import com.sasac.platform.report.dto.ProblemAssetRow;
import com.sasac.platform.report.dto.ReconciliationRow;
import com.sasac.platform.report.dto.RevitalizationRow;
import com.sasac.platform.report.entity.EquityInvestment;
import com.sasac.platform.report.entity.CreditorRights;
import com.sasac.platform.report.entity.PeFundInvestment;
import com.sasac.platform.report.entity.FranchiseRight;
import com.sasac.platform.report.entity.DataAsset;
import com.sasac.platform.report.entity.NaturalResource;
import com.sasac.platform.report.entity.CashAccount;
import com.sasac.platform.report.mapper.EquityInvestmentMapper;
import com.sasac.platform.report.mapper.CreditorRightsMapper;
import com.sasac.platform.report.mapper.PeFundInvestmentMapper;
import com.sasac.platform.report.mapper.FranchiseRightMapper;
import com.sasac.platform.report.mapper.DataAssetMapper;
import com.sasac.platform.report.mapper.NaturalResourceMapper;
import com.sasac.platform.report.mapper.CashAccountMapper;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final InspectionAnomalyMapper inspectionAnomalyMapper;
    private final EquityInvestmentMapper equityInvestmentMapper;
    private final CreditorRightsMapper creditorRightsMapper;
    private final PeFundInvestmentMapper peFundInvestmentMapper;
    private final FranchiseRightMapper franchiseRightMapper;
    private final DataAssetMapper dataAssetMapper;
    private final NaturalResourceMapper naturalResourceMapper;
    private final CashAccountMapper cashAccountMapper;

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
                         DepreciationMapper depreciationMapper,
                         InspectionAnomalyMapper inspectionAnomalyMapper,
                         EquityInvestmentMapper equityInvestmentMapper,
                         CreditorRightsMapper creditorRightsMapper,
                         PeFundInvestmentMapper peFundInvestmentMapper,
                         FranchiseRightMapper franchiseRightMapper,
                         DataAssetMapper dataAssetMapper,
                         NaturalResourceMapper naturalResourceMapper,
                         CashAccountMapper cashAccountMapper) {
        this.exportTaskMapper = exportTaskMapper;
        this.minioClient = minioClient;
        this.assetMapper = assetMapper;
        this.inventoryTaskMapper = inventoryTaskMapper;
        this.inventoryRecordMapper = inventoryRecordMapper;
        this.depreciationMapper = depreciationMapper;
        this.inspectionAnomalyMapper = inspectionAnomalyMapper;
        this.equityInvestmentMapper = equityInvestmentMapper;
        this.creditorRightsMapper = creditorRightsMapper;
        this.peFundInvestmentMapper = peFundInvestmentMapper;
        this.franchiseRightMapper = franchiseRightMapper;
        this.dataAssetMapper = dataAssetMapper;
        this.naturalResourceMapper = naturalResourceMapper;
        this.cashAccountMapper = cashAccountMapper;
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
                case ExportType.ASSET_BASE_LIST -> exportAssetBaseList(task);
                case ExportType.LAND_ASSET_DETAIL -> exportLandAssetDetail(task);
                case ExportType.BUILDING_ASSET_DETAIL -> exportBuildingAssetDetail(task);
                case ExportType.INVENTORY_DETAIL -> exportInventoryDetail(task);
                case ExportType.INTANGIBLE_ASSET_DETAIL -> exportIntangibleAssetDetail(task);
                case ExportType.MACHINERY_EQUIP_DETAIL -> exportMachineryEquipDetail(task);
                case ExportType.INFRASTRUCTURE_DETAIL -> exportInfrastructureDetail(task);
                case ExportType.OTHER_FIXED_ASSET_DETAIL -> exportOtherFixedAssetDetail(task);
                case ExportType.REVITALIZATION_LIST -> exportRevitalizationList(task);
                case ExportType.PROBLEM_ASSET_LIST -> exportProblemAssetList(task);
                case ExportType.EQUITY_INVESTMENT_DETAIL -> exportEquityInvestmentDetail(task);
                case ExportType.CREDITOR_RIGHTS_DETAIL -> exportCreditorRightsDetail(task);
                case ExportType.PE_FUND_INVESTMENT_DETAIL -> exportPeFundInvestmentDetail(task);
                case ExportType.FRANCHISE_RIGHT_DETAIL -> exportFranchiseRightDetail(task);
                case ExportType.DATA_ASSET_DETAIL -> exportDataAssetDetail(task);
                case ExportType.NATURAL_RESOURCE_DETAIL -> exportNaturalResourceDetail(task);
                case ExportType.MONETARY_FUND_DETAIL -> exportMonetaryFundDetail(task);
                case ExportType.BALANCE_SHEET -> exportBalanceSheet(task);
                case ExportType.RECONCILIATION_TABLE -> exportReconciliationTable(task);
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

    // ========== Asset base list ==========

    private void exportAssetBaseList(ExportTask task) {
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

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, AssetExportRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Land asset detail ==========

    private void exportLandAssetDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .like(Asset::getCategory, "土地");
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);
        AtomicInteger seq = new AtomicInteger(1);
        List<LandAssetDetailRow> rows = assets.stream()
            .map(a -> LandAssetDetailRow.builder()
                .seq(seq.getAndIncrement())
                .assetCode(a.getAssetCode())
                .landName(a.getName())
                .location(a.getLocation())
                .area(a.getSpecification() != null ? new BigDecimal(a.getSpecification().replaceAll("[^0-9.]", "")) : null)
                .ownershipType(getAssetAttr(a, "ownershipType"))
                .certificateNo(a.getCertificateNo())
                .usageCategory(a.getCategory())
                .acquisitionMethod(getAssetAttr(a, "acquisitionMethod"))
                .acquisitionDate(a.getPurchaseDate())
                .usefulLifeYears(a.getUsefulLifeMonths() != null ? a.getUsefulLifeMonths() / 12 : null)
                .originalValue(a.getOriginalValue())
                .currentValue(a.getCurrentValue())
                .isIdle("IDLE".equals(a.getUseStatus()) ? "是" : "否")
                .remark(a.getRemark())
                .build())
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, LandAssetDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Building asset detail ==========

    private void exportBuildingAssetDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .like(Asset::getCategory, "房屋");
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);
        AtomicInteger seq = new AtomicInteger(1);
        List<BuildingAssetDetailRow> rows = assets.stream()
            .map(a -> BuildingAssetDetailRow.builder()
                .seq(seq.getAndIncrement())
                .assetCode(a.getAssetCode())
                .buildingName(a.getName())
                .location(a.getLocation())
                .structureType(getAssetAttr(a, "structureType"))
                .area(a.getSpecification() != null ? new BigDecimal(a.getSpecification().replaceAll("[^0-9.]", "")) : null)
                .certificateNo(a.getCertificateNo())
                .completionDate(null)
                .usefulLifeYears(a.getUsefulLifeMonths() != null ? a.getUsefulLifeMonths() / 12 : null)
                .originalValue(a.getOriginalValue())
                .currentValue(a.getCurrentValue())
                .useStatus(a.getUseStatus())
                .isRented(null)
                .annualRent(null)
                .remark(a.getRemark())
                .build())
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, BuildingAssetDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Inventory detail ==========

    private void exportInventoryDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .like(Asset::getCategory, "存货");
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);
        AtomicInteger seq = new AtomicInteger(1);
        List<InventoryDetailRow> rows = assets.stream()
            .map(a -> InventoryDetailRow.builder()
                .seq(seq.getAndIncrement())
                .assetCode(a.getAssetCode())
                .inventoryName(a.getName())
                .category(a.getCategory())
                .unit(a.getUnit())
                .quantity(a.getQuantity() != null ? BigDecimal.valueOf(a.getQuantity()) : null)
                .unitPrice(null)
                .amount(a.getOriginalValue())
                .location(a.getLocation())
                .storageAge(null)
                .isStagnant(null)
                .impairmentProvision(null)
                .inventoryDate(null)
                .remark(a.getRemark())
                .build())
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, InventoryDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Intangible asset detail ==========

    private void exportIntangibleAssetDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .like(Asset::getCategory, "无形");
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);
        AtomicInteger seq = new AtomicInteger(1);
        List<IntangibleAssetDetailRow> rows = assets.stream()
            .map(a -> IntangibleAssetDetailRow.builder()
                .seq(seq.getAndIncrement())
                .assetCode(a.getAssetCode())
                .assetName(a.getName())
                .category(a.getCategory())
                .registrationNo(a.getCertificateNo())
                .acquisitionMethod(getAssetAttr(a, "acquisitionMethod"))
                .acquisitionDate(a.getPurchaseDate())
                .amortizationYears(a.getUsefulLifeMonths() != null ? a.getUsefulLifeMonths() / 12 : null)
                .originalValue(a.getOriginalValue())
                .accumulatedAmortization(a.getAccumulatedDepreciation())
                .currentValue(a.getCurrentValue())
                .isPledged(null)
                .remark(a.getRemark())
                .build())
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, IntangibleAssetDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Machinery & equipment detail ==========

    private void exportMachineryEquipDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .and(w -> w.like(Asset::getCategory, "机器")
                    .or().like(Asset::getCategory, "设备"));
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);
        AtomicInteger seq = new AtomicInteger(1);
        List<MachineryEquipDetailRow> rows = assets.stream()
            .map(a -> MachineryEquipDetailRow.builder()
                .seq(seq.getAndIncrement())
                .assetCode(a.getAssetCode())
                .equipmentName(a.getName())
                .specification(a.getSpecification())
                .manufacturer(null)
                .serialNo(null)
                .productionDate(null)
                .operationDate(null)
                .designLifeYears(a.getUsefulLifeMonths() != null ? a.getUsefulLifeMonths() / 12 : null)
                .originalValue(a.getOriginalValue())
                .currentValue(a.getCurrentValue())
                .newnessRate(null)
                .useStatus(a.getUseStatus())
                .maintenanceFrequency(null)
                .location(a.getLocation())
                .remark(a.getRemark())
                .build())
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, MachineryEquipDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Infrastructure detail ==========

    private void exportInfrastructureDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .like(Asset::getCategory, "基础");
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);
        AtomicInteger seq = new AtomicInteger(1);
        List<InfrastructureDetailRow> rows = assets.stream()
            .map(a -> InfrastructureDetailRow.builder()
                .seq(seq.getAndIncrement())
                .assetCode(a.getAssetCode())
                .facilityName(a.getName())
                .facilityType(a.getCategory())
                .location(a.getLocation())
                .scale(a.getSpecification())
                .unit(a.getUnit())
                .completionDate(null)
                .designLifeYears(a.getUsefulLifeMonths() != null ? a.getUsefulLifeMonths() / 12 : null)
                .originalValue(a.getOriginalValue())
                .currentValue(a.getCurrentValue())
                .useStatus(a.getUseStatus())
                .managementDept(a.getUseDepartment())
                .isTollOperation(null)
                .annualRevenue(null)
                .remark(a.getRemark())
                .build())
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, InfrastructureDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Other fixed asset detail (catch-all) ==========

    private void exportOtherFixedAssetDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .and(w -> w.notLike(Asset::getCategory, "土地")
                    .notLike(Asset::getCategory, "房屋")
                    .notLike(Asset::getCategory, "存货")
                    .notLike(Asset::getCategory, "无形")
                    .notLike(Asset::getCategory, "机器")
                    .notLike(Asset::getCategory, "设备")
                    .notLike(Asset::getCategory, "基础"));
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);
        AtomicInteger seq = new AtomicInteger(1);
        List<OtherFixedAssetDetailRow> rows = assets.stream()
            .map(a -> OtherFixedAssetDetailRow.builder()
                .seq(seq.getAndIncrement())
                .assetCode(a.getAssetCode())
                .assetName(a.getName())
                .category(a.getCategory())
                .specification(a.getSpecification())
                .unit(a.getUnit())
                .quantity(a.getQuantity())
                .acquisitionDate(a.getPurchaseDate())
                .originalValue(a.getOriginalValue())
                .currentValue(a.getCurrentValue())
                .accumulatedDepreciation(a.getAccumulatedDepreciation())
                .useStatus(a.getUseStatus())
                .location(a.getLocation())
                .useDepartment(a.getUseDepartment())
                .custodian(a.getCustodian())
                .remark(a.getRemark())
                .build())
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, OtherFixedAssetDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Revitalization list (idle assets) ==========

    private void exportRevitalizationList(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId())
                .eq(Asset::getUseStatus, "IDLE");
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> assets = assetMapper.selectList(qw);
        AtomicInteger seq = new AtomicInteger(1);
        List<RevitalizationRow> rows = assets.stream()
            .map(a -> RevitalizationRow.builder()
                .seq(seq.getAndIncrement())
                .assetCode(a.getAssetCode())
                .assetName(a.getName())
                .revitalizationMethod(null)
                .originalStatus(a.getUseStatus())
                .originalValue(a.getOriginalValue())
                .revitalizationRevenue(null)
                .revitalizationDate(null)
                .currentStatus(a.getUseStatus())
                .beneficiary(null)
                .approvalNo(null)
                .remark(a.getRemark())
                .build())
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, RevitalizationRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Problem asset list (from inspection anomalies) ==========

    private void exportProblemAssetList(ExportTask task) {
        // Note: orgId filtering is not applied here because InspectionAnomaly
        // lacks an orgId column. Cross-org filtering will be supported when
        // the anomaly table is extended with org affiliation.
        LambdaQueryWrapper<InspectionAnomaly> qw = new LambdaQueryWrapper<InspectionAnomaly>()
                .eq(InspectionAnomaly::getTenantId, task.getTenantId());
        List<InspectionAnomaly> anomalies = inspectionAnomalyMapper.selectList(qw);

        List<Long> assetIds = anomalies.stream()
                .map(InspectionAnomaly::getAssetId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Asset> assetMap = assetIds.isEmpty() ? Map.of() :
                assetMapper.selectBatchIds(assetIds).stream()
                        .collect(Collectors.toMap(Asset::getId, Function.identity()));

        AtomicInteger seq = new AtomicInteger(1);
        List<ProblemAssetRow> rows = anomalies.stream()
            .map(anomaly -> {
                Asset asset = assetMap.get(anomaly.getAssetId());
                ProblemAssetRow r = new ProblemAssetRow();
                r.setSeq(seq.getAndIncrement());
                r.setAssetCode(asset != null ? asset.getAssetCode() : null);
                r.setAssetName(asset != null ? asset.getName() : null);
                r.setProblemType(anomaly.getAnomalyType());
                r.setDescription(anomaly.getDescription());
                r.setAmount(asset != null ? asset.getOriginalValue() : null);
                r.setFoundDate(anomaly.getCreatedAt() != null ? anomaly.getCreatedAt().toLocalDate() : null);
                r.setMeasure(anomaly.getResolution());
                r.setResponsibleDept(null);
                r.setStatus(anomaly.getStatus());
                r.setCompletedDate(null);
                r.setRemark(null);
                return r;
            })
            .collect(Collectors.toList());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, ProblemAssetRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Reports needing new tables (empty data for now) ==========

    private void exportEquityInvestmentDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<EquityInvestment> qw = new LambdaQueryWrapper<EquityInvestment>()
                .eq(EquityInvestment::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(EquityInvestment::getOrgId, orgId);
        }
        List<EquityInvestment> list = equityInvestmentMapper.selectList(qw);
        List<EquityInvestmentRow> rows = list.stream()
            .map(e -> EquityInvestmentRow.builder()
                .enterpriseName(e.getEnterpriseName())
                .creditCode(e.getCreditCode())
                .investDate(e.getInvestDate())
                .investMethod(e.getInvestMethod())
                .shareRatio(e.getShareRatio())
                .investAmount(e.getInvestAmount())
                .cumulativeDividend(e.getCumulativeDividend())
                .bookValue(e.getBookValue())
                .fairValue(e.getFairValue())
                .isImpaired(e.getIsImpaired() != null && e.getIsImpaired() ? "是" : "否")
                .impairmentAmount(e.getImpairmentAmount())
                .enterpriseStatus(e.getEnterpriseStatus())
                .remark(e.getRemark())
                .build())
            .collect(Collectors.toList());
        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, EquityInvestmentRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    private void exportCreditorRightsDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<CreditorRights> qw = new LambdaQueryWrapper<CreditorRights>()
                .eq(CreditorRights::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(CreditorRights::getOrgId, orgId);
        }
        List<CreditorRights> list = creditorRightsMapper.selectList(qw);
        List<CreditorRightsRow> rows = list.stream()
            .map(e -> CreditorRightsRow.builder()
                .creditorCode(e.getCreditorCode())
                .debtorName(e.getDebtorName())
                .rightsType(e.getRightsType())
                .amount(e.getAmount())
                .formedDate(e.getFormedDate())
                .aging(e.getAging())
                .badDebtProvision(e.getBadDebtProvision())
                .estimatedRecoverable(e.getEstimatedRecoverable())
                .collectionStatus(e.getCollectionStatus())
                .contractNo(e.getContractNo())
                .isLitigation(e.getIsLitigation() != null && e.getIsLitigation() ? "是" : "否")
                .remark(e.getRemark())
                .build())
            .collect(Collectors.toList());
        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, CreditorRightsRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    private void exportPeFundInvestmentDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<PeFundInvestment> qw = new LambdaQueryWrapper<PeFundInvestment>()
                .eq(PeFundInvestment::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(PeFundInvestment::getOrgId, orgId);
        }
        List<PeFundInvestment> list = peFundInvestmentMapper.selectList(qw);
        List<PeFundInvestmentRow> rows = list.stream()
            .map(e -> PeFundInvestmentRow.builder()
                .fundName(e.getFundName())
                .fundManager(e.getFundManager())
                .fundType(e.getFundType())
                .subscribedAmount(e.getSubscribedAmount())
                .paidAmount(e.getPaidAmount())
                .investDate(e.getInvestDate())
                .fundDuration(e.getFundDuration())
                .currentNav(e.getCurrentNav())
                .cumulativeReturn(e.getCumulativeReturn())
                .isViolation(e.getIsViolation() != null && e.getIsViolation() ? "是" : "否")
                .recordNo(e.getRecordNo())
                .remark(e.getRemark())
                .build())
            .collect(Collectors.toList());
        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, PeFundInvestmentRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    private void exportFranchiseRightDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<FranchiseRight> qw = new LambdaQueryWrapper<FranchiseRight>()
                .eq(FranchiseRight::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(FranchiseRight::getOrgId, orgId);
        }
        List<FranchiseRight> list = franchiseRightMapper.selectList(qw);
        List<FranchiseRightRow> rows = list.stream()
            .map(e -> FranchiseRightRow.builder()
                .rightName(e.getRightName())
                .authorizer(e.getAuthorizer())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .authorizedArea(e.getAuthorizedArea())
                .businessScope(e.getBusinessScope())
                .authorizationFee(e.getAuthorizationFee())
                .annualFee(e.getAnnualFee())
                .isExpired(e.getIsExpired() != null && e.getIsExpired() ? "是" : "否")
                .remark(e.getRemark())
                .build())
            .collect(Collectors.toList());
        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, FranchiseRightRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    private void exportDataAssetDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<DataAsset> qw = new LambdaQueryWrapper<DataAsset>()
                .eq(DataAsset::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(DataAsset::getOrgId, orgId);
        }
        List<DataAsset> list = dataAssetMapper.selectList(qw);
        List<DataAssetDetailRow> rows = list.stream()
            .map(e -> DataAssetDetailRow.builder()
                .dataName(e.getDataName())
                .dataType(e.getDataType())
                .dataVolume(e.getDataVolume())
                .storageMethod(e.getStorageMethod())
                .securityLevel(e.getSecurityLevel())
                .isInBalanceSheet(e.getIsInBalanceSheet() != null && e.getIsInBalanceSheet() ? "是" : "否")
                .balanceSheetValue(e.getBalanceSheetValue())
                .usageScenario(e.getUsageScenario())
                .remark(e.getRemark())
                .build())
            .collect(Collectors.toList());
        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, DataAssetDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    private void exportNaturalResourceDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<NaturalResource> qw = new LambdaQueryWrapper<NaturalResource>()
                .eq(NaturalResource::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(NaturalResource::getOrgId, orgId);
        }
        List<NaturalResource> list = naturalResourceMapper.selectList(qw);
        List<NaturalResourceDetailRow> rows = list.stream()
            .map(e -> NaturalResourceDetailRow.builder()
                .resourceName(e.getResourceName())
                .resourceType(e.getResourceType())
                .location(e.getLocation())
                .areaOrReserve(e.getAreaOrReserve())
                .unit(e.getUnit())
                .certificateNo(e.getCertificateNo())
                .acquisitionMethod(e.getAcquisitionMethod())
                .usefulLifeYears(e.getUsefulLifeYears())
                .bookValue(e.getBookValue())
                .appraisedValue(e.getAppraisedValue())
                .isDeveloped(e.getIsDeveloped() != null && e.getIsDeveloped() ? "是" : "否")
                .remark(e.getRemark())
                .build())
            .collect(Collectors.toList());
        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, NaturalResourceDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    private void exportMonetaryFundDetail(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<CashAccount> qw = new LambdaQueryWrapper<CashAccount>()
                .eq(CashAccount::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(CashAccount::getOrgId, orgId);
        }
        List<CashAccount> list = cashAccountMapper.selectList(qw);
        List<MonetaryFundDetailRow> rows = list.stream()
            .map(e -> MonetaryFundDetailRow.builder()
                .accountName(e.getAccountName())
                .bankName(e.getBankName())
                .accountNo(e.getAccountNo())
                .currency(e.getCurrency())
                .bookBalance(e.getBookBalance())
                .statementBalance(e.getStatementBalance())
                .diffAmount(e.getDiffAmount())
                .diffReason(e.getDiffReason())
                .accountType(e.getAccountType())
                .isRestricted(e.getIsRestricted() != null && e.getIsRestricted() ? "是" : "否")
                .restrictedAmount(e.getRestrictedAmount())
                .reconciliationDate(e.getReconciliationDate())
                .remark(e.getRemark())
                .build())
            .collect(Collectors.toList());
        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, MonetaryFundDetailRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Balance sheet ==========

    private void exportBalanceSheet(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> allAssets = assetMapper.selectList(qw);

        BigDecimal totalOriginalValue = allAssets.stream()
                .map(Asset::getOriginalValue)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCurrentValue = allAssets.stream()
                .map(Asset::getCurrentValue)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BalanceSheetRow> rows = new ArrayList<>();
        rows.add(BalanceSheetRow.builder().seq(1).item("流动资产合计").lineNo(1).endBalance(null).startBalance(null).remark("").build());
        rows.add(BalanceSheetRow.builder().seq(2).item("  货币资金").lineNo(2).endBalance(null).startBalance(null).remark("").build());
        rows.add(BalanceSheetRow.builder().seq(3).item("非流动资产合计").lineNo(3).endBalance(null).startBalance(null).remark("").build());
        rows.add(BalanceSheetRow.builder().seq(4).item("  固定资产").lineNo(4).endBalance(totalOriginalValue).startBalance(null).remark("原值合计").build());
        rows.add(BalanceSheetRow.builder().seq(5).item("  固定资产净值").lineNo(5).endBalance(totalCurrentValue).startBalance(null).remark("净值合计").build());
        rows.add(BalanceSheetRow.builder().seq(6).item("  无形资产").lineNo(6).endBalance(null).startBalance(null).remark("").build());
        rows.add(BalanceSheetRow.builder().seq(7).item("资产总计").lineNo(7).endBalance(totalOriginalValue).startBalance(null).remark("").build());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, BalanceSheetRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
    }

    // ========== Reconciliation table ==========

    private void exportReconciliationTable(ExportTask task) {
        Long orgId = parseOrgId(task.getParams());
        LambdaQueryWrapper<Asset> qw = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getTenantId, task.getTenantId());
        if (orgId != null) {
            qw.eq(Asset::getOrgId, orgId);
        }
        List<Asset> allAssets = assetMapper.selectList(qw);

        // Define category groups: {like-pattern, detail-table-name}
        String[][] categoryGroups = {
            {"土地", "土地类资产清查明细表"},
            {"房屋", "房屋建筑类清查明细表"},
            {"机器", "大型机器设备清查明细表"},
            {"设备", "大型机器设备清查明细表"},
            {"基础", "基础设施资产清查明细表"},
            {"存货", "存货清查明细表"},
            {"无形", "无形资产清查明细表"},
        };

        List<ReconciliationRow> rows = new ArrayList<>();
        AtomicInteger seq = new AtomicInteger(1);

        for (String[] group : categoryGroups) {
            String categoryLike = group[0];
            String tableName = group[1];

            List<Asset> groupAssets = allAssets.stream()
                .filter(a -> a.getCategory() != null && a.getCategory().contains(categoryLike))
                .collect(Collectors.toList());

            BigDecimal detailOriginalValue = groupAssets.stream()
                .map(Asset::getOriginalValue)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal detailCurrentValue = groupAssets.stream()
                .map(Asset::getCurrentValue)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            rows.add(ReconciliationRow.builder()
                .seq(seq.getAndIncrement())
                .category(categoryLike)
                .detailTableName(tableName)
                .detailCount(groupAssets.size())
                .detailOriginalValue(detailOriginalValue)
                .detailCurrentValue(detailCurrentValue)
                .baseListOriginalValue(detailOriginalValue)
                .baseListCurrentValue(detailCurrentValue)
                .originalValueDiff(BigDecimal.ZERO)
                .currentValueDiff(BigDecimal.ZERO)
                .diffReason("同一数据源")
                .isConsistent("是")
                .build());
        }

        // Other — catch-all
        List<Asset> otherAssets = allAssets.stream()
            .filter(a -> a.getCategory() == null
                || (!a.getCategory().contains("土地")
                    && !a.getCategory().contains("房屋")
                    && !a.getCategory().contains("机器")
                    && !a.getCategory().contains("设备")
                    && !a.getCategory().contains("基础")
                    && !a.getCategory().contains("存货")
                    && !a.getCategory().contains("无形")))
            .collect(Collectors.toList());

        BigDecimal otherOriginalValue = otherAssets.stream()
            .map(Asset::getOriginalValue)
            .filter(v -> v != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal otherCurrentValue = otherAssets.stream()
            .map(Asset::getCurrentValue)
            .filter(v -> v != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        rows.add(ReconciliationRow.builder()
            .seq(seq.getAndIncrement())
            .category("其他")
            .detailTableName("其他固定资产清查明细表")
            .detailCount(otherAssets.size())
            .detailOriginalValue(otherOriginalValue)
            .detailCurrentValue(otherCurrentValue)
            .baseListOriginalValue(otherOriginalValue)
            .baseListCurrentValue(otherCurrentValue)
            .originalValueDiff(BigDecimal.ZERO)
            .currentValueDiff(BigDecimal.ZERO)
            .diffReason("同一数据源")
            .isConsistent("是")
            .build());

        String displayName = ExportType.label(task.getExportType());
        try {
            writeAndUpload(task, rows, ReconciliationRow.class, displayName, displayName);
        } catch (Exception e) {
            throw new RuntimeException("导出" + displayName + "失败", e);
        }
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

    /**
     * Download exported file content directly (proxied through backend).
     * Avoids the internal MinIO hostname issue with presigned URLs.
     */
    public byte[] downloadFile(Long taskId) throws Exception {
        ExportTask task = exportTaskMapper.selectById(taskId);
        if (task == null || task.getFilePath() == null) return null;
        try (var stream = minioClient.getObject(
                io.minio.GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(task.getFilePath())
                        .build())) {
            return stream.readAllBytes();
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

    /**
     * Placeholder helper to retrieve extended attributes from an Asset.
     * Currently returns null; will be replaced with proper attribute
     * resolution when extended attribute tables (e.g. asset_attr) are introduced.
     */
    private String getAssetAttr(Asset a, String key) {
        return null;
    }

}
