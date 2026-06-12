package com.sasac.platform.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing an asset disposal record.
 * <p>
 * Records the disposal of an asset via scrapping, selling, or writing off,
 * including the financial gain/loss calculation.
 */
@Getter
@Setter
@TableName("disposal")
public class Disposal extends BaseEntity {

    /**
     * The ID of the asset being disposed.
     */
    private Long assetId;

    /**
     * Disposal type: SCRAP, SELL, or WRITE_OFF.
     */
    private String disposalType;

    /**
     * Date of disposal.
     */
    private LocalDate disposalDate;

    /**
     * Book value of the asset at the time of disposal (asset's current value).
     */
    private BigDecimal bookValue;

    /**
     * Actual disposal value (sale price or residual recovery).
     */
    private BigDecimal disposalValue;

    /**
     * Gain or loss on disposal (disposalValue - bookValue).
     */
    private BigDecimal gainLoss;

    /**
     * Reason for disposal.
     */
    private String reason;

    /**
     * Approval document ID.
     */
    private String approvalId;

    /**
     * Additional remarks.
     */
    private String remark;
}
