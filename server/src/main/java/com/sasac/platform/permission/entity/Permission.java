package com.sasac.platform.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_permission")
public class Permission extends BaseEntity {

    private Long parentId;
    private String permCode;
    private String permName;
    private String permType;
    private String path;
    private String icon;
    private Integer sortOrder;
    private Integer status = 1;
}
