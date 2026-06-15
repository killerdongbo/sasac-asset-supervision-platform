package com.sasac.platform.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrgAssetSummary {
    private Long orgId;
    private String orgName;
    private int assetCount;
    private BigDecimal originalValue;
    private BigDecimal currentValue;
    private BigDecimal accumulatedDepreciation;
}
