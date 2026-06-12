package com.sasac.platform.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login response DTO.
 * <p>
 * Returned upon successful authentication, containing the JWT token
 * along with basic user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String realName;
    private Long tenantId;
    private Long orgId;
    private String roleCode;
}
