package com.sasac.platform.asset.procurement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Purchase request entity.
 * <p>
 * Represents a procurement request for assets. The lifecycle starts with
 * {@code PENDING} status and transitions through acceptance or rejection.
 */
@Getter
@Setter
@TableName("purchase_request")
public class PurchaseRequest extends BaseEntity {

    private String assetName;

    private Integer quantity;

    private BigDecimal budget;

    private Long supplierId;

    private Long orgId;

    private Long tenantId;

    private String requestReason;

    /**
     * Status: PENDING, APPROVED, ACCEPTED, REJECTED.
     */
    private String status;

    /**
     * Optional external approval workflow instance ID.
     */
    private String approvalInstanceId;

    private String remark;
}
