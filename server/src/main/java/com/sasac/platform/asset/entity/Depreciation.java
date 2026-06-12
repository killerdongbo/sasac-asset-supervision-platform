package com.sasac.platform.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a monthly depreciation record for an asset.
 */
@Getter
@Setter
@TableName("depreciation")
public class Depreciation extends BaseEntity {

    private Long assetId;

    private BigDecimal depreciationAmount;

    private LocalDate depreciationDate;

    private BigDecimal beforeValue;

    private BigDecimal afterValue;

    /**
     * Period in YYYY-MM format, e.g. "2026-06".
     */
    private String period;
}
