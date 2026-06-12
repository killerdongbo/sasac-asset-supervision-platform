package com.sasac.platform.asset.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.dto.AssetImportDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for importing assets from Excel files using EasyExcel.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssetImportService {

    private final AssetService assetService;

    /**
     * Result of an import operation.
     */
    @Data
    public static class ImportResult {
        private int totalCount;
        private int successCount;
        private List<String> errors = new ArrayList<>();
    }

    /**
     * Imports assets from an uploaded Excel file.
     *
     * @param file     the uploaded Excel file
     * @param tenantId the tenant ID
     * @param orgId    the organization ID
     * @return import result with counts and error details
     * @throws IOException if the file cannot be read
     */
    public ImportResult importAssets(MultipartFile file, Long tenantId, Long orgId) throws IOException {
        ImportResult result = new ImportResult();

        EasyExcel.read(file.getInputStream(), AssetImportDTO.class, new ReadListener<AssetImportDTO>() {
            @Override
            public void invoke(AssetImportDTO importDTO, AnalysisContext context) {
                result.totalCount++;
                try {
                    AssetCreateDTO createDTO = new AssetCreateDTO();
                    BeanUtils.copyProperties(importDTO, createDTO);
                    createDTO.setOrgId(orgId);
                    createDTO.setTenantId(tenantId);

                    // Parse purchaseDate from String to LocalDate if present
                    if (importDTO.getPurchaseDate() != null && !importDTO.getPurchaseDate().isBlank()) {
                        createDTO.setPurchaseDate(LocalDate.parse(importDTO.getPurchaseDate()));
                    }

                    assetService.create(createDTO);
                    result.successCount++;
                } catch (Exception e) {
                    log.error("Failed to import asset at row {}: {}", result.totalCount, e.getMessage());
                    result.errors.add("Row " + result.totalCount + " (" + importDTO.getName() + "): " + e.getMessage());
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Asset import completed: total={}, success={}, errors={}",
                        result.totalCount, result.successCount, result.errors.size());
            }
        }).sheet().doRead();

        return result;
    }

    /**
     * Downloads an Excel template for asset import with sample data and instructions.
     */
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode("资产导入模板.xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        // Sheet 1: 资产清单 (with headers + example row)
        List<AssetImportDTO> exampleData = new ArrayList<>();
        AssetImportDTO example = new AssetImportDTO();
        example.setName("示例：市委办公楼A栋");
        example.setAssetCode("ZC-2026-0001");
        example.setCategory("REAL_ESTATE");
        example.setSpecification("框架结构/10层");
        example.setUnit("平方米");
        example.setQuantity(1);
        example.setOriginalValue(new java.math.BigDecimal("5000000.00"));
        example.setDepreciationMethod("STRAIGHT_LINE");
        example.setUsefulLifeMonths(240);
        example.setUseStatus("IN_USE");
        example.setUseDepartment("办公室");
        example.setCustodian("张三");
        example.setLocation("湛江市赤坎区");
        example.setPurchaseDate("2024-01-15");
        example.setSourceType("自建");
        example.setCertificateNo("粤(2024)湛江市不动产权第001号");
        example.setRemark("示例数据，导入时请删除此行");
        exampleData.add(example);

        try (OutputStream out = response.getOutputStream()) {
            EasyExcel.write(out, AssetImportDTO.class)
                    .sheet("资产清单")
                    .doWrite(exampleData);

            // Sheet 2: 填写说明
            EasyExcel.write(out)
                    .sheet("填写说明")
                    .doWrite(instructionsData());
        }
    }

    private List<List<String>> instructionsData() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("字段", "必填", "填写说明"));
        data.add(Arrays.asList("资产名称", "是", "资产的名称，如'市委办公楼A栋'"));
        data.add(Arrays.asList("资产编码", "是", "唯一编码，不可与已有资产重复"));
        data.add(Arrays.asList("资产分类", "是", "LAND=土地 / REAL_ESTATE=房产 / EQUIPMENT=设备 / VEHICLE=车辆 / FURNITURE=家具 / IT_EQUIPMENT=IT设备"));
        data.add(Arrays.asList("规格型号", "否", "资产的规格描述"));
        data.add(Arrays.asList("计量单位", "否", "如：平方米、台、辆"));
        data.add(Arrays.asList("数量", "否", "默认为1"));
        data.add(Arrays.asList("原值(元)", "否", "资产原始价值，如 5000000.00"));
        data.add(Arrays.asList("折旧方法", "否", "STRAIGHT_LINE=直线法 / DOUBLE_DECLINING=双倍余额递减法 / SUM_OF_YEARS=年限总和法"));
        data.add(Arrays.asList("使用年限(月)", "否", "如房产240个月(20年)，设备60个月(5年)"));
        data.add(Arrays.asList("使用状态", "否", "IN_USE=使用中 / IDLE=闲置 / RENTED=已出租 / MORTGAGED=已抵押"));
        data.add(Arrays.asList("使用部门", "否", "资产使用部门名称"));
        data.add(Arrays.asList("保管人", "否", "资产保管人姓名"));
        data.add(Arrays.asList("存放地点", "否", "资产存放地址"));
        data.add(Arrays.asList("购置日期", "否", "格式：YYYY-MM-DD，如 2024-01-15"));
        data.add(Arrays.asList("来源方式", "否", "如：自建、采购、划转、捐赠"));
        data.add(Arrays.asList("权属证号", "否", "产权证书编号"));
        data.add(Arrays.asList("备注", "否", "其他需要说明的信息"));
        return data;
    }
}
