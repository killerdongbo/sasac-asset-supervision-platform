package com.sasac.platform.system.export.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("export_task")
public class ExportTask extends BaseEntity {

    private Long tenantId;

    private String exportType;

    private String fileName;

    private String filePath;

    private String status;

    private String params;

    private String reportPeriod;

    private Integer totalRows;

    private String errorMessage;

    private Long createdBy;

    private LocalDateTime completedAt;
}
