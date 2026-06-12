package com.sasac.platform.asset.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Asset supplier dictionary.
 * <p>
 * Tracks vendors that supply assets to the organization.
 */
@Getter
@Setter
@TableName("asset_supplier")
public class Supplier extends BaseEntity {

    private String name;

    private String contact;

    private String phone;

    private String address;

    private String businessScope;

    private Long tenantId;

    private Integer status;
}
