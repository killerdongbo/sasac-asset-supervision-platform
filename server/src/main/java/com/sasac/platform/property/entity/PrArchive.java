package com.sasac.platform.property.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 产权档案 - 产权相关文档资料
 */
@Getter
@Setter
@TableName("pr_archive")
public class PrArchive extends BaseEntity {

    private Long tenantId;
    private String refType;
    private Long refId;
    private String docName;
    private String docType;
    private String filePath;
    private Long fileSize;
    private Long uploaderId;
    private String uploaderName;
}
