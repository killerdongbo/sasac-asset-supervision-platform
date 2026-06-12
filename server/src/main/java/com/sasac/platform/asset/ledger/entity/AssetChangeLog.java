package com.sasac.platform.asset.ledger.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity recording a single field-level change to an asset.
 * <p>
 * When {@link com.sasac.platform.asset.service.AssetService#update} is called,
 * it compares old and new values and writes one AssetChangeLog entry per
 * changed field, providing a fine-grained audit trail.
 */
@Getter
@Setter
@TableName("asset_change_log")
public class AssetChangeLog extends BaseEntity {

    /**
     * Asset ID that was changed.
     */
    private Long assetId;

    /**
     * Name of the changed field (e.g. "name", "useStatus", "custodian").
     */
    private String changeField;

    /**
     * Value before the change, stored as string.
     */
    private String beforeValue;

    /**
     * Value after the change, stored as string.
     */
    private String afterValue;

    /**
     * Operator who performed the change.
     */
    private String operatorId;

    /**
     * Optional remark about the change.
     */
    private String remark;
}
