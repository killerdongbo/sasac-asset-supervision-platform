package com.sasac.platform.performance.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for recording the actual value of a performance indicator.
 */
@Getter
@Setter
public class RecordActualDTO {

    private BigDecimal actualValue;
}
