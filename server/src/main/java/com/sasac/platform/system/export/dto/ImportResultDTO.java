package com.sasac.platform.system.export.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportResultDTO {
    private int totalRows;
    private int successRows;
    private int errorRows;
    private List<ImportErrorRow> errors;
    private List<?> previewData;
}
