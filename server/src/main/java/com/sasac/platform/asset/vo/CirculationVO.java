package com.sasac.platform.asset.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * View object for circulation (stock-in / assignment / transfer) records,
 * enriched with assetCode and assetName from the asset table so the
 * front-end displays human-readable identifiers instead of raw numeric IDs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CirculationVO {

    // ── asset_change fields ──
    private Long id;
    private Long assetId;
    private String assetCode;
    private String assetName;
    private String changeType;
    private Long fromOrgId;
    private Long toOrgId;
    private String fromCustodian;
    private String toCustodian;
    private BigDecimal changeValue;
    private LocalDate changeDate;
    private String reason;
    private String remark;
    private String operatorId;
    private LocalDateTime createdAt;

    // ── disposal-specific fields (null for change records) ──
    private String disposalType;
    private LocalDate disposalDate;
    private BigDecimal bookValue;
    private BigDecimal disposalValue;
    private BigDecimal gainLoss;
}
