package com.sasac.platform.decision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating a new decision item.
 */
@Data
public class DecisionItemCreateDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotBlank(message = "事项类型不能为空")
    private String itemType;

    @NotBlank(message = "事项标题不能为空")
    private String title;

    private String description;

    private BigDecimal amount;

    private String proposerName;

    private String department;
}
