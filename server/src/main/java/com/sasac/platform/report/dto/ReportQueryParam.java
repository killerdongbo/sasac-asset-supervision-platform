package com.sasac.platform.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportQueryParam {
    private Long tenantId;
    private Long orgId;
    private String category;
    private String useStatus;
    private String startDate;
    private String endDate;
    private String periodType;
    private String disposalType;
}
