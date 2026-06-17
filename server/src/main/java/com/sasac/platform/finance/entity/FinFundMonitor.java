package com.sasac.platform.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Fund monitoring transaction entity.
 */
@Getter
@Setter
@TableName("fin_fund_monitor")
public class FinFundMonitor extends BaseEntity {

    private Long tenantId;
    private Long orgId;
    private String transactionNo;
    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private String payer;
    private String payee;
    private String purpose;
    private Boolean isFlagged;
    private String flagReason;
}
