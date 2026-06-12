package com.sasac.platform.asset.procurement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Purchase acceptance entity.
 * <p>
 * Records the result of the acceptance check for a purchase request.
 * When the check passes (PASS) the request can be converted to an asset.
 */
@Getter
@Setter
@TableName("purchase_acceptance")
public class PurchaseAcceptance extends BaseEntity {

    private Long requestId;

    /**
     * PASS or FAIL.
     */
    private String acceptanceResult;

    private Integer actualQuantity;

    private BigDecimal actualAmount;

    private String checkRemark;

    /**
     * The asset ID created from this acceptance (set after conversion).
     */
    private Long assetId;

    private Long tenantId;
}
