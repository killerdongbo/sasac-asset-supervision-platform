package com.sasac.platform.asset.ledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a printed asset label with barcode data.
 * <p>
 * Each asset can have one or more labels, each identified by a
 * unique label code (UUID-based) and tracking print history.
 */
@Getter
@Setter
@TableName("asset_label")
public class AssetLabel extends BaseEntity {

    /**
     * Asset ID this label belongs to.
     */
    private Long assetId;

    /**
     * Unique label code, generated as a UUID string.
     */
    private String labelCode;

    /**
     * Barcode data payload (e.g. encoded labelCode or asset info).
     */
    private String barcodeData;

    /**
     * Number of times this label has been printed.
     */
    private Integer printCount = 0;

    /**
     * Timestamp of the most recent print.
     */
    private LocalDateTime lastPrintTime;

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;
}
