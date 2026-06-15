package com.sasac.platform.asset.label.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LabelDataDTO {

    private Long assetId;
    private String assetCode;
    private String assetName;
    private String category;
    private String orgName;
    private String location;
    private String custodian;
    private String qrContent;
    private String barcode;
}
