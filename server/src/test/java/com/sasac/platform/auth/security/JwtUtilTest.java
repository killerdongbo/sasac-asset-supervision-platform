package com.sasac.platform.auth.security;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET = "test-secret-key-with-at-least-256-bits-length-for-hs256-algorithm";
    private static final long EXPIRATION = 3600000L; // 1 hour
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET_KEY, EXPIRATION);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        Long userId = 42L;
        String username = "testuser";
        Long tenantId = 100L;

        String token = jwtUtil.generateToken(userId, username, tenantId);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(userId, jwtUtil.getUserId(token));
        assertEquals(username, jwtUtil.getUsername(token));
        assertEquals(tenantId, jwtUtil.getTenantId(token));
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {
        JwtUtil shortLivedJwt = new JwtUtil(SECRET_KEY, 1L); // 1 ms expiration
        String token = shortLivedJwt.generateToken(1L, "test", 1L);

        // Wait for expiration
        Thread.sleep(10);

        assertFalse(shortLivedJwt.validateToken(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.jwt.token"));
    }

    @Test
    void shouldRejectNullToken() {
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void shouldRejectEmptyToken() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void shouldExtractClaimsFromValidToken() {
        Long userId = 99L;
        String username = "admin";
        Long tenantId = 200L;

        String token = jwtUtil.generateToken(userId, username, tenantId);

        assertEquals(userId, jwtUtil.getUserId(token));
        assertEquals(username, jwtUtil.getUsername(token));
        assertEquals(tenantId, jwtUtil.getTenantId(token));
    }

    @Test
    void shouldThrowExceptionWhenGettingClaimsFromInvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtUtil.getUserId("invalid.token.here");
        });
    }
}
