package com.sasac.platform.asset.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Asset physical location dictionary.
 * <p>
 * Stores building/venue locations where assets are situated.
 */
@Getter
@Setter
@TableName("asset_location")
public class Location extends BaseEntity {

    private String name;

    private Long parentId;

    private String address;

    private Long tenantId;

    private Integer sortOrder;

    private Integer status;
}
