package com.sasac.platform.property.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易监测 - 产权交易监测数据
 */
@Getter
@Setter
@TableName("pr_transaction_monitor")
public class PrTransactionMonitor extends BaseEntity {

    private Long tenantId;
    private String exchangeName;
    private String listingNo;
    private String projectName;
    private BigDecimal listingPrice;
    private LocalDate bidStartDate;
    private LocalDate bidEndDate;
    private BigDecimal transactionPrice;
    private String buyerName;
    private BigDecimal priceDeviationPct;
    private Boolean isAbnormal;
}
