package com.sasac.platform.investment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Deal / transaction record for an investment project.
 * <p>
 * Maps to the {@code invest_deal} table.
 */
@Getter
@Setter
@TableName("invest_deal")
public class InvestDeal extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private LocalDate dealDate;

    private BigDecimal dealAmount;

    private BigDecimal equityAcquired;

    private String paymentTerms;

    private String agreementNo;

    private String status;
}
