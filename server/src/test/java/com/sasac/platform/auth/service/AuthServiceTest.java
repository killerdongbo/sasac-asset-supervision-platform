package com.sasac.platform.auth.service;

import com.sasac.platform.auth.dto.LoginRequest;
import com.sasac.platform.auth.dto.LoginResponse;
import com.sasac.platform.auth.entity.User;
import com.sasac.platform.auth.mapper.UserMapper;
import com.sasac.platform.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_withValidCredentials_shouldReturnLoginResponse() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("hashed");
        user.setRealName("管理员");
        user.setTenantId(0L);
        user.setRoleCode("SYSTEM_ADMIN");
        user.setStatus(1);

        when(userMapper.findByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("admin123", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "admin", 0L)).thenReturn("test-token");

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRealName()).isEqualTo("管理员");
        assertThat(response.getTenantId()).isEqualTo(0L);
        assertThat(response.getRoleCode()).isEqualTo("SYSTEM_ADMIN");
    }

    @Test
    void login_withBadPassword_shouldThrowBadCredentialsException() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("hashed");
        user.setStatus(1);

        when(userMapper.findByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong");

        // Act & Assert
        assertThrows(BadCredentialsException.class, () ->
                authService.login(request));
    }

    @Test
    void login_withNonExistentUsername_shouldThrowBadCredentialsException() {
        // Arrange
        when(userMapper.findByUsername("unknown")).thenReturn(null);

        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("anything");

        // Act & Assert
        assertThrows(BadCredentialsException.class, () ->
                authService.login(request));
    }

    @Test
    void login_withDisabledAccount_shouldThrowBadCredentialsException() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("disabled");
        user.setPassword("hashed");
        user.setStatus(0);

        when(userMapper.findByUsername("disabled")).thenReturn(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("disabled");
        request.setPassword("hashed");

        // Act & Assert
        assertThrows(BadCredentialsException.class, () ->
                authService.login(request));
    }
}
