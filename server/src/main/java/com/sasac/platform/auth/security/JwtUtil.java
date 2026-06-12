package com.sasac.platform.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for JWT token generation and validation.
 * Uses HMAC-SHA256 for signing with configurable secret and expiration.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey key;
    private final long expiration;

    /**
     * Primary constructor used by Spring DI with configuration values.
     */
    @Autowired
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * Package-private constructor for testing.
     * Allows direct construction with a pre-built key without Spring property injection.
     */
    JwtUtil(SecretKey key, long expiration) {
        this.key = key;
        this.expiration = expiration;
    }

    /**
     * Generates a JWT token for the given user.
     *
     * @param userId   the user's ID
     * @param username the user's username
     * @param tenantId the user's tenant ID
     * @return a signed JWT string
     */
    public String generateToken(Long userId, String username, Long tenantId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("tenantId", tenantId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT string
     * @return true if the token is valid and not expired
     */
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the user ID from the token.
     *
     * @param token the JWT string
     * @return the user ID stored in the subject claim
     */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extracts the username from the token.
     *
     * @param token the JWT string
     * @return the username claim
     */
    public String getUsername(String token) {
        Claims claims = parseClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Extracts the tenant ID from the token.
     *
     * @param token the JWT string
     * @return the tenant ID claim
     */
    public Long getTenantId(String token) {
        Claims claims = parseClaims(token);
        return claims.get("tenantId", Long.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
