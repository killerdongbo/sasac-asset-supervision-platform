package com.sasac.platform.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_dict_type")
public class DictType extends BaseEntity {
    private String dictCode;
    private String dictName;
    private Integer status = 1;
    private String remark;
}
