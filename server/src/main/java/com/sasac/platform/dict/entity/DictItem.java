package com.sasac.platform.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_dict_item")
public class DictItem extends BaseEntity {
    private String dictCode;
    private String itemKey;
    private String itemValue;
    private Integer sortOrder = 0;
    private Integer status = 1;
    private String remark;
}
