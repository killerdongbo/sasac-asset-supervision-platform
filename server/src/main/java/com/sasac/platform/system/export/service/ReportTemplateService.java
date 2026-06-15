package com.sasac.platform.system.export.service;

import com.sasac.platform.system.export.ExportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportTemplateService {

    public byte[] getTemplate(String exportType) throws IOException {
        String filename = exportType + ".xlsx";
        ClassPathResource resource = new ClassPathResource("report-templates/" + filename);
        if (!resource.exists()) {
            throw new IllegalArgumentException("模板不存在: " + exportType);
        }
        return resource.getInputStream().readAllBytes();
    }

    public String getTemplateFileName(String exportType) {
        return ExportType.label(exportType) + "-模板.xlsx";
    }
}
