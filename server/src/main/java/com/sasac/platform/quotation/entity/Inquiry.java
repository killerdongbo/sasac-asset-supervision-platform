package com.sasac.platform.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("inquiry")
public class Inquiry extends BaseEntity {

    private Long tenantId;
    private String inquiryNo;
    private String title;
    private String category;
    private String specification;
    private Integer quantity = 1;
    private String unit;
    private BigDecimal budgetAmount;
    private String status = "DRAFT";
    private LocalDateTime deadline;
    private String remark;
    private Long createdBy;
}
