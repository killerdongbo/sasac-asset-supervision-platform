package com.sasac.platform.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

/**
 * Login response DTO.
 * <p>
 * Returned upon successful authentication, containing the JWT token
 * along with basic user information and permission codes.
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
    @Builder.Default
    private Set<String> permissions = Collections.emptySet();
    @Builder.Default
    private Set<String> roles = Collections.emptySet();
}
