package com.sasac.platform.miniapp.dto;

import lombok.Data;

@Data
public class ScanInventoryRequest {
    private Long taskId;
    private String assetCode;
    private String locationCode;
    private String remark;
}
