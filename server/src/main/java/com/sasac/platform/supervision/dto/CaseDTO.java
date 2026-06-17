package com.sasac.platform.supervision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for opening a violation case.
 */
@Getter
@Setter
public class CaseDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "案件标题不能为空")
    private String caseTitle;

    @NotBlank(message = "违规类型不能为空")
    private String violationType;

    private Long suspectId;

    private String suspectName;
}
