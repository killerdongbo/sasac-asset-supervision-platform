package com.sasac.platform.majorevent.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lawsuit entity tracking legal proceedings against or by the organization.
 * <p>
 * Maps to the {@code me_lawsuit} table. Each record captures case details,
 * involved parties, claim amounts, and the current trial stage.
 */
@Getter
@Setter
@TableName("me_lawsuit")
public class MeLawsuit extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Reference to the associated major event.
     */
    private Long eventId;

    /**
     * Court case number assigned by the judicial authority.
     */
    private String caseNo;

    /**
     * Name of the court handling this case.
     */
    private String court;

    /**
     * Name of the plaintiff (party bringing the suit).
     */
    private String plaintiff;

    /**
     * Name of the defendant (party being sued).
     */
    private String defendant;

    /**
     * Monetary amount claimed in the lawsuit.
     */
    private BigDecimal claimAmount;

    /**
     * Monetary amount awarded by the court judgment.
     */
    private BigDecimal judgmentAmount;

    /**
     * Name of the law firm representing the organization.
     */
    private String lawFirm;

    /**
     * Name of the attorney handling the case.
     */
    private String attorney;

    /**
     * Current progress or phase of the trial.
     */
    private String trialProgress;

    /**
     * Date when the court judgment was issued.
     */
    private LocalDate judgmentDate;

    /**
     * Status: PENDING, ONGOING, WON, LOST, SETTLED, APPEALED.
     */
    private String status = "PENDING";
}
