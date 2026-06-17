package com.sasac.platform.decision.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating a new decision resolution.
 */
@Data
public class ResolutionCreateDTO {

    @NotBlank(message = "决议标题不能为空")
    private String title;

    @NotBlank(message = "决议内容不能为空")
    private String content;

    private String voteResult;

    private Long itemId;
}
