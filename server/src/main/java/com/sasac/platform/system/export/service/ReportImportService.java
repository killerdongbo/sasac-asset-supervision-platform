package com.sasac.platform.system.export.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.listener.ReadListener;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.system.export.ExportType;
import com.sasac.platform.system.export.dto.ImportErrorRow;
import com.sasac.platform.system.export.dto.ImportResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportImportService {

    private final AssetMapper assetMapper;

    public ImportResultDTO preview(MultipartFile file, String exportType) throws IOException {
        Path tmpFile = Files.createTempFile("import-", ".xlsx");
        file.transferTo(tmpFile.toFile());
        try {
            return previewFromFile(tmpFile, exportType);
        } finally {
            tmpFile.toFile().delete();
        }
    }

    /**
     * Preview rows from an already-saved temp file. Shared by {@link #preview(MultipartFile, String)}
     * and {@link #importData(MultipartFile, String, Long)} to avoid double-consuming the
     * MultipartFile stream.
     */
    private ImportResultDTO previewFromFile(Path tmpFile, String exportType) throws IOException {
        List<Object> rows = new CopyOnWriteArrayList<>();
        List<ImportErrorRow> errors = new CopyOnWriteArrayList<>();

        Class<?> dtoClass = resolveDtoClass(exportType);
        EasyExcel.read(tmpFile.toFile(), dtoClass, new ReadListener<Object>() {
            @Override public void invoke(Object data, AnalysisContext ctx) {
                rows.add(data);
            }
            @Override public void onException(Exception e, AnalysisContext ctx) {
                if (e instanceof ExcelDataConvertException ex) {
                    errors.add(ImportErrorRow.builder()
                            .row(ex.getRowIndex() + 1)
                            .field(String.valueOf(ex.getColumnIndex()))
                            .reason("数据格式错误")
                            .build());
                }
            }
            @Override public void doAfterAllAnalysed(AnalysisContext ctx) {}
        }).sheet().doRead();

        int previewSize = Math.min(20, Math.max(0, rows.size()));
        return ImportResultDTO.builder()
                .totalRows(rows.size())
                .successRows(rows.size() - errors.size())
                .errorRows(errors.size())
                .errors(errors)
                .previewData(rows.isEmpty() ? List.of() : rows.subList(0, previewSize))
                .build();
    }

    @Transactional
    public ImportResultDTO importData(MultipartFile file, String exportType, Long tenantId) throws IOException {
        // Save to a temp file first so we can reuse it for both preview and actual import
        Path tmpFile = Files.createTempFile("import-", ".xlsx");
        file.transferTo(tmpFile.toFile());
        try {
            // Run preview on the saved file
            ImportResultDTO preview = previewFromFile(tmpFile, exportType);
            if (preview.getErrorRows() > 0) {
                throw new BusinessException("数据校验未通过，请先修正错误后再导入");
            }

            if (ExportType.ASSET_BASE_LIST.equals(exportType) || ExportType.ASSET_LIST.equals(exportType)) {
                List<Asset> assets = new ArrayList<>();
                EasyExcel.read(tmpFile.toFile(), Asset.class, new ReadListener<Asset>() {
                    @Override public void invoke(Asset data, AnalysisContext ctx) {
                        data.setTenantId(tenantId);
                        data.setId(null);
                        assets.add(data);
                    }
                    @Override public void doAfterAllAnalysed(AnalysisContext ctx) {}
                }).sheet().doRead();

                for (Asset asset : assets) {
                    assetMapper.insert(asset);
                }
                return ImportResultDTO.builder()
                        .totalRows(assets.size()).successRows(assets.size()).errorRows(0)
                        .errors(List.of()).build();
            }
            throw new BusinessException("该报表类型暂不支持导入: " + exportType);
        } finally {
            tmpFile.toFile().delete();
        }
    }

    private Class<?> resolveDtoClass(String exportType) {
        return switch (exportType) {
            case ExportType.ASSET_LIST, ExportType.ASSET_BASE_LIST -> Asset.class;
            case ExportType.BALANCE_SHEET -> com.sasac.platform.report.dto.BalanceSheetRow.class;
            case ExportType.PROBLEM_ASSET_LIST -> com.sasac.platform.report.dto.ProblemAssetRow.class;
            case ExportType.REVITALIZATION_LIST -> com.sasac.platform.report.dto.RevitalizationRow.class;
            default -> throw new BusinessException("暂不支持该类型报表的导入: " + exportType);
        };
    }
}
