package com.sasac.platform.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_file")
public class FileAttachment extends BaseEntity {
    private Long tenantId;
    private String originalName;
    private String storageName;
    private String bucketName;
    private String objectPath;
    private Long fileSize;
    private String contentType;
    private String fileExt;
    private String md5;
    private String bizType;
    private Long bizId;
    private Integer isImage = 0;
    private String thumbPath;
    private Long uploadBy;
}
