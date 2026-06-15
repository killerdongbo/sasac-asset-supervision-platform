package com.sasac.platform.miniapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WxLoginResponse {
    private String token;
    private String openId;
    private String realName;
    private Long userId;
    private Long tenantId;
}
