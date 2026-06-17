package com.sasac.platform.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for recording a transaction monitoring record.
 */
@Data
public class TransactionDTO {

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    private String exchangeName;

    private String listingNo;

    private String projectName;

    private BigDecimal listingPrice;

    private LocalDate bidStartDate;

    private LocalDate bidEndDate;

    private BigDecimal transactionPrice;

    private String buyerName;
}
