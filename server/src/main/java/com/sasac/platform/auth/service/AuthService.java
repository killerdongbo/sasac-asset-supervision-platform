package com.sasac.platform.auth.service;

import com.sasac.platform.auth.dto.LoginRequest;
import com.sasac.platform.auth.dto.LoginResponse;
import com.sasac.platform.auth.entity.User;
import com.sasac.platform.auth.mapper.UserMapper;
import com.sasac.platform.auth.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service handling user login and JWT token generation.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user with the given credentials.
     * <p>
     * Looks up the user by username, verifies the account is active,
     * checks the password, and generates a JWT token on success.
     *
     * @param request the login request containing username and password
     * @return a {@link LoginResponse} with the JWT token and user info
     * @throws BadCredentialsException if credentials are invalid or account is disabled
     */
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        log.debug("Login attempt for user: {}", username);

        User user = userMapper.findByUsername(username);

        if (user == null) {
            log.warn("Login failed: user '{}' not found", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("Login failed: user '{}' is disabled", username);
            throw new BadCredentialsException("Account is disabled");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login failed: incorrect password for user '{}'", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getTenantId());

        log.info("User '{}' logged in successfully", username);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .tenantId(user.getTenantId())
                .orgId(user.getOrgId())
                .roleCode(user.getRoleCode())
                .build();
    }
}
