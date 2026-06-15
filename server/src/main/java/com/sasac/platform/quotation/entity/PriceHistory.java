package com.sasac.platform.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@TableName("price_history")
public class PriceHistory extends BaseEntity {

    private Long tenantId;
    private String category;
    private String specification;
    private Long supplierId;
    private String supplierName;
    private BigDecimal unitPrice;
    private String sourceType = "QUOTATION";
    private Long sourceId;
    private LocalDate recordDate;
}
