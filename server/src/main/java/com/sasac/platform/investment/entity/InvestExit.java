package com.sasac.platform.investment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Exit record for an investment project.
 * <p>
 * Maps to the {@code invest_exit} table.
 */
@Getter
@Setter
@TableName("invest_exit")
public class InvestExit extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private LocalDate exitDate;

    private BigDecimal exitAmount;

    private String exitMethod;

    private BigDecimal returnRate;

    private BigDecimal profitLoss;

    private Long approvalInstanceId;

    private String status;
}
