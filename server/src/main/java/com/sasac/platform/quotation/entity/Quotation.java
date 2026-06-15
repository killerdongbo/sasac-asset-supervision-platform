package com.sasac.platform.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("quotation")
public class Quotation extends BaseEntity {

    private Long tenantId;
    private Long inquiryId;
    private Long supplierId;
    private String supplierName;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal taxRate;
    private Integer deliveryDays;
    private Integer warrantyMonths;
    private String remark;
    private Integer isSelected = 0;
    private LocalDateTime quotedAt;
}
