package com.sasac.platform.asset.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Asset maintenance provider dictionary.
 * <p>
 * Records external service providers responsible for asset upkeep.
 */
@Getter
@Setter
@TableName("asset_maintenance_provider")
public class MaintenanceProvider extends BaseEntity {

    private String name;

    private String contact;

    private String phone;

    private String serviceTypes;

    private Long tenantId;

    private Integer status;
}
