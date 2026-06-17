package com.sasac.platform.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Document entity for project-related file attachments.
 */
@Getter
@Setter
@TableName("pm_document")
public class PmDocument extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private String docName;

    private String docType;

    private String filePath;

    private Long fileSize;

    private Long uploaderId;

    private String uploaderName;
}
