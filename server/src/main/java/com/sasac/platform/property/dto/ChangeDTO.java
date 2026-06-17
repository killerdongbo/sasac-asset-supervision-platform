package com.sasac.platform.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for recording a property right change.
 */
@Data
public class ChangeDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "变动类型不能为空")
    private String changeType;

    private String beforeData;

    private String afterData;

    private String changeReason;

    private String approvalFileIds;

    private LocalDate effectiveDate;
}
