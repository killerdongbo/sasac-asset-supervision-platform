package com.sasac.platform.miniapp.dto;

import lombok.Data;

@Data
public class RepairSubmitRequest {
    private Long assetId;
    private String faultDescription;
    private String urgency;
    private String contactPhone;
    private String imageUrls;
}
