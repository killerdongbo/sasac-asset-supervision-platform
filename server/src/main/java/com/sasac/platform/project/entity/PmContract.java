package com.sasac.platform.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Contract entity linked to a project, tracking contract details and status.
 */
@Getter
@Setter
@TableName("pm_contract")
public class PmContract extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private String contractNo;

    private String contractName;

    private String counterparty;

    private BigDecimal amount;

    private LocalDate signDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private String paymentTerms;

    private String status;
}
