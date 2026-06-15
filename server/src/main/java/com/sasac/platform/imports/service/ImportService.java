package com.sasac.platform.imports.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.imports.dto.ImportResult;
import com.sasac.platform.imports.dto.ImportResult.RowError;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final AssetMapper assetMapper;

    // ---- 模板下载 ----

    public byte[] generateTemplate() {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("资产导入模板");
            // 表头样式
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true); headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"*资产名称","*资产编码","*资产分类","规格型号","单位","数量",
                    "原值(元)","*使用状态","存放位置","保管人","采购日期","备注"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]); cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 3500);
            }

            // 示例行
            Row demoRow = sheet.createRow(1);
            demoRow.createCell(0).setCellValue("示例：办公笔记本电脑");
            demoRow.createCell(1).setCellValue("ZC-2026-0001");
            demoRow.createCell(2).setCellValue("办公设备");
            demoRow.createCell(3).setCellValue("ThinkPad X1");
            demoRow.createCell(4).setCellValue("台");
            demoRow.createCell(5).setCellValue(1);
            demoRow.createCell(6).setCellValue(8000.00);
            demoRow.createCell(7).setCellValue("IN_USE");
            demoRow.createCell(8).setCellValue("A栋302");
            demoRow.createCell(9).setCellValue("张三");
            demoRow.createCell(10).setCellValue("2026-01-15");

            Sheet guide = wb.createSheet("填写说明");
            Row g1 = guide.createRow(0); g1.createCell(0).setCellValue("带*的为必填字段");
            Row g2 = guide.createRow(1); g2.createCell(0).setCellValue("使用状态可选：IN_USE(在用)、IDLE(闲置)、RENTED(出租)");
            Row g3 = guide.createRow(2); g3.createCell(0).setCellValue("日期格式：YYYY-MM-DD");
            Row g4 = guide.createRow(3); g4.createCell(0).setCellValue("数量默认为1，原值单位为元");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            return bos.toByteArray();
        } catch (Exception e) { throw new RuntimeException("生成模板失败", e); }
    }

    // ---- 导入 ----

    @Transactional
    public ImportResult importAssets(MultipartFile file, Long tenantId) {
        List<RowError> errors = new ArrayList<>();
        List<Asset> successList = new ArrayList<>();
        int totalRows = 0;

        try (InputStream is = file.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            totalRows = sheet.getLastRowNum(); // 不含表头

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Asset asset = parseRow(row, tenantId);
                    // 校验
                    List<RowError> rowErrors = validateAsset(asset, i + 1);
                    if (!rowErrors.isEmpty()) {
                        errors.addAll(rowErrors);
                        continue;
                    }
                    // 重复检测
                    Asset dup = assetMapper.selectOne(
                            new LambdaQueryWrapper<Asset>().eq(Asset::getAssetCode, asset.getAssetCode()));
                    if (dup != null) {
                        errors.add(RowError.builder().row(i + 1).field("assetCode")
                                .message("资产编码已存在: " + asset.getAssetCode()).build());
                        continue;
                    }
                    successList.add(asset);
                } catch (Exception e) {
                    errors.add(RowError.builder().row(i + 1).field("").message("解析失败: " + e.getMessage()).build());
                }
            }
        } catch (Exception e) {
            errors.add(RowError.builder().row(0).field("").message("文件读取失败: " + e.getMessage()).build());
        }

        // 批量写入成功行
        for (Asset a : successList) {
            assetMapper.insert(a);
        }

        return ImportResult.builder()
                .totalRows(totalRows)
                .successCount(successList.size())
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    private Asset parseRow(Row row, Long tenantId) {
        Asset a = new Asset();
        a.setTenantId(tenantId);
        a.setName(getStr(row, 0));
        a.setAssetCode(getStr(row, 1));
        a.setCategory(getStr(row, 2));
        a.setSpecification(getStr(row, 3));
        a.setUnit(getStr(row, 4) != null ? getStr(row, 4) : "台");
        a.setQuantity((int) getNum(row, 5));
        a.setOriginalValue(BigDecimal.valueOf(getNum(row, 6)));
        a.setUseStatus(getStr(row, 7) != null ? getStr(row, 7) : "IN_USE");
        a.setLocation(getStr(row, 8));
        a.setCustodian(getStr(row, 9));
        try { a.setPurchaseDate(java.time.LocalDate.parse(getStr(row, 10))); } catch (Exception ignored) {}
        a.setRemark(getStr(row, 11));
        if (a.getQuantity() == null) a.setQuantity(1);
        return a;
    }

    private List<RowError> validateAsset(Asset a, int row) {
        List<RowError> errs = new ArrayList<>();
        if (isBlank(a.getName())) errs.add(RowError.builder().row(row).field("name").message("资产名称不能为空").build());
        if (isBlank(a.getAssetCode())) errs.add(RowError.builder().row(row).field("assetCode").message("资产编码不能为空").build());
        if (isBlank(a.getCategory())) errs.add(RowError.builder().row(row).field("category").message("资产分类不能为空").build());
        if (isBlank(a.getUseStatus())) errs.add(RowError.builder().row(row).field("useStatus").message("使用状态不能为空").build());
        if (a.getAssetCode() != null && a.getAssetCode().length() > 64)
            errs.add(RowError.builder().row(row).field("assetCode").message("编码不能超过64字符").build());
        return errs;
    }

    private String getStr(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        CellType type = cell.getCellType();
        if (type == CellType.STRING) return cell.getStringCellValue().trim();
        if (type == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return cell.toString().trim();
    }

    private double getNum(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return 0;
        return cell.getNumericCellValue();
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
