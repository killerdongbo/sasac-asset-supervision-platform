package com.sasac.platform.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing an asset change record (stock-in, assignment, transfer,
 * mortgage, rental, repair, status change).
 */
@Getter
@Setter
@TableName("asset_change")
public class AssetChange extends BaseEntity {

    private Long assetId;

    /**
     * Change type: STOCK_IN, ASSIGNMENT, TRANSFER, MORTGAGE, RENTAL, REPAIR, STATUS_CHANGE.
     */
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
}
