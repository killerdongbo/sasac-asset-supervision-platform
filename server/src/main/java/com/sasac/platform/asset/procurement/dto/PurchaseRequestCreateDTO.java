package com.sasac.platform.asset.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating a purchase request.
 */
@Data
public class PurchaseRequestCreateDTO {

    @NotBlank(message = "资产名称不能为空")
    private String assetName;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @NotNull(message = "预算不能为空")
    private BigDecimal budget;

    @NotNull(message = "供应商不能为空")
    private Long supplierId;

    @NotNull(message = "所属组织不能为空")
    private Long orgId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private String requestReason;

    private String remark;
}
