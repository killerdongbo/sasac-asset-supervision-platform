package com.sasac.platform.property.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 产权登记 - 产权占有、变动、注销登记信息
 */
@Getter
@Setter
@TableName("pr_registration")
public class PrRegistration extends BaseEntity {

    private Long tenantId;
    private Long orgId;
    private String regNo;
    private String regType;
    private String enterpriseName;
    private String propertyType;
    private String propertyOwner;
    private BigDecimal equityPct;
    private BigDecimal registeredCapital;
    private BigDecimal paidCapital;
    private LocalDate registrationDate;
    private String certNo;
    private String certFileIds;
    private String status;
}
