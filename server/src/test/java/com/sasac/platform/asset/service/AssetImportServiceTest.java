package com.sasac.platform.asset.service;

import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.asset.entity.Asset;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Excel bulk import of assets.
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AssetImportServiceTest {

    @Autowired
    private AssetImportService importService;

    @Test
    void shouldImportAssetsFromExcel() throws Exception {
        // Create an in-memory Excel workbook using Apache POI
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("资产导入");

        // Header row matching @ExcelProperty column names
        String[] headers = {
                "资产名称", "资产编码", "资产分类", "规格型号", "计量单位", "数量",
                "原值(元)", "折旧方法", "使用年限(月)", "使用状态", "使用部门",
                "保管人", "存放地点", "购置日期", "来源方式", "权属证号", "备注"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Data row
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("测试导入服务器");
        dataRow.createCell(1).setCellValue("IMP-TEST-SRV-001");
        dataRow.createCell(2).setCellValue("IT_EQUIPMENT");
        dataRow.createCell(3).setCellValue("Dell R740");
        dataRow.createCell(4).setCellValue("台");
        dataRow.createCell(5).setCellValue(2);
        dataRow.createCell(6).setCellValue(50000.00);
        dataRow.createCell(7).setCellValue("直线法");
        dataRow.createCell(8).setCellValue(60);
        dataRow.createCell(9).setCellValue("IN_USE");
        dataRow.createCell(10).setCellValue("技术部");
        dataRow.createCell(11).setCellValue("张三");
        dataRow.createCell(12).setCellValue("A栋3楼");
        dataRow.createCell(13).setCellValue("2024-06-15");
        dataRow.createCell(14).setCellValue("购置");
        dataRow.createCell(15).setCellValue("CERT-IMP-001");
        dataRow.createCell(16).setCellValue("测试导入数据");

        // Write workbook to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        // Create MockMultipartFile from the byte array
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-assets.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray()
        );

        // Execute the import
        AssetImportService.ImportResult result = importService.importAssets(file, 0L, 1L);

        // Verify results
        assertThat(result.getSuccessCount()).isGreaterThanOrEqualTo(1);
        assertThat(result.getErrors()).isEmpty();
    }
}
